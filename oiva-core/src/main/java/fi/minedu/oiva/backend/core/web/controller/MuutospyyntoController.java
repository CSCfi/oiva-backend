package fi.minedu.oiva.backend.core.web.controller;

import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Application;
import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Esittelija;
import fi.minedu.oiva.backend.core.service.DefaultPebbleService;
import fi.minedu.oiva.backend.core.service.LupaRenderService;
import fi.minedu.oiva.backend.core.service.LupamuutosService;
import fi.minedu.oiva.backend.core.service.MuutospyyntoService;
import fi.minedu.oiva.backend.core.service.PrinceXMLService;
import fi.minedu.oiva.backend.core.util.RequestUtils;
import fi.minedu.oiva.backend.model.entity.OivaTemplates;
import fi.minedu.oiva.backend.model.entity.oiva.Liite;
import fi.minedu.oiva.backend.model.entity.oiva.Lupa;
import fi.minedu.oiva.backend.model.entity.oiva.Muutos;
import fi.minedu.oiva.backend.model.entity.oiva.Muutospyynto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.core.service.MuutospyyntoService.Action;
import static fi.minedu.oiva.backend.core.service.MuutospyyntoService.Muutospyyntotila;
import static fi.minedu.oiva.backend.core.util.AsyncUtil.async;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.get500;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.getOr400;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.getOr404;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.notFound;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RestController
@RequestMapping(
        value = "${api.url.prefix}" + MuutospyyntoController.path,
        produces = {MediaType.APPLICATION_JSON_VALUE})
@Api(description = "Muutospyyntöjen hallinta")
@OivaAccess_Application
@Transactional
public class MuutospyyntoController {

    private static final Logger logger = LoggerFactory.getLogger(MuutospyyntoController.class);

    public static final String path = "/muutospyynnot";

    private final MuutospyyntoService muutospyyntoService;
    private final LupamuutosService lupamuutosService;
    private final DefaultPebbleService pebbleService;
    private final LupaRenderService lupaRenderService;
    private final PrinceXMLService princeXMLService;

    @Autowired
    public MuutospyyntoController(MuutospyyntoService muutospyyntoService, LupamuutosService lupamuutosService,
                                  DefaultPebbleService pebbleService, LupaRenderService lupaRenderService,
                                  PrinceXMLService princeXMLService) {
        this.muutospyyntoService = muutospyyntoService;
        this.lupamuutosService = lupamuutosService;
        this.pebbleService = pebbleService;
        this.lupaRenderService = lupaRenderService;
        this.princeXMLService = princeXMLService;
    }


    // palauttaa kaikki järjestäjän muutospyynnöt
    @RequestMapping(method = GET, value = "/{oid}/**")
    @ApiOperation(notes = "Palauttaa muutospyynnöt järjestäjän oid:n perusteella", value = "")
    public CompletableFuture<Collection<Muutospyynto>> getByOid(final @PathVariable String oid,
                                                                final HttpServletRequest request) {
        return async(() -> muutospyyntoService.getByOid(RequestUtils.getPathVariable(request, oid)));
    }

    @OivaAccess_Esittelija
    @RequestMapping(method = GET, value = "")
    @ApiOperation(notes = "Palauttaa muutospyynnöt esittelijälle.", value = "")
    public CompletableFuture<Collection<Muutospyynto>> getMuutospyynnot(
            @RequestParam() List<Muutospyyntotila> tilat,
            @RequestParam(required = false, defaultValue = "false") boolean vainOmat,
            @RequestParam(required = false) String koulutustyyppi) {
        return async(() -> muutospyyntoService.getMuutospyynnot(tilat, vainOmat, koulutustyyppi));
    }


    // hakee yksittäisen muutospyynnön UUID:llä
    @RequestMapping(method = GET, value = "/id/{uuid}")
    @ApiOperation(notes = "Palauttaa muutospyynnön uuid:n perusteella", value = "")
    public CompletableFuture<HttpEntity<Muutospyynto>> getById(final @PathVariable String uuid) {
        return getOr404(async(() -> muutospyyntoService.getByUuid(uuid)));
    }

    // palauttaa kaikki muutospyynnön muutokset
    @RequestMapping(method = GET, value = "/muutokset/{muutospyyntoUuid}")
    @ApiOperation(notes = "Palauttaa kaikki muutospyynnön muutokset", value = "")
    public CompletableFuture<Collection<Muutos>> getMuutoksetByMuutospyyntoId(final @PathVariable String muutospyyntoUuid) {
        return async(() -> muutospyyntoService.getByMuutospyyntoUuid(muutospyyntoUuid));
    }

    @ApiOperation(notes = "Tallentaa muutospyynnön", value = "")
    @RequestMapping(method = POST, value = "/tallenna", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpEntity<Muutospyynto> save(@RequestPart(value = "muutospyynto") Muutospyynto muutospyynto,
                                         MultipartHttpServletRequest request) {
        Optional<Muutospyynto> result;
        if (muutospyynto.getUuid() != null) {
            result = muutospyyntoService.executeAction(muutospyynto.getUuid().toString(), Action.TALLENNA, muutospyynto, request.getFileMap());
        } else {
            logger.debug("Creating new muutospyynto");
            result = muutospyyntoService.executeAction(null, Action.LUO, muutospyynto, request.getFileMap());
        }
        return getOr400(result);
    }

    @OivaAccess_Esittelija
    @ApiOperation(notes = "Tallentaa esittelijän luoman lupamuutoksen", value = "")
    @RequestMapping(method = POST, value = "/esittelija/tallenna", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpEntity<Muutospyynto> tallennaLupamuutos(@RequestPart(value = "muutospyynto") Muutospyynto muutospyynto,
                                         MultipartHttpServletRequest request) {
        Optional<Muutospyynto> result;
        if (muutospyynto.getUuid() != null) {
            result = lupamuutosService.executeAction(muutospyynto.getUuid().toString(), Action.TALLENNA, muutospyynto, request.getFileMap());
        } else {
            logger.debug("Creating new esittelijan muutospyynto");
            result = lupamuutosService.executeAction(null, Action.LUO, muutospyynto, request.getFileMap());
        }
        return getOr400(result);
    }

    @RequestMapping(method = POST, value = "{muutospyyntoUuid}/liitteet/paatoskirje")
    @ApiOperation(
            notes = "Asettaa annetun päätöskirje PDF tiedoston muutospyynnön liitteeksi",
            value = "",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompletableFuture<HttpEntity<Muutospyynto>> setPaatoskirjeAttachment(
            @RequestPart(value = "muutospyynto") Muutospyynto muutospyynto,
            MultipartHttpServletRequest request,
            @PathVariable String muutospyyntoUuid) {

        return getOr404(async(() -> muutospyyntoService.setPaatoskirjeLiite(muutospyynto, request.getFileMap() )));
    }

    @RequestMapping(method = GET, value = "{muutospyyntoUuid}/liitteet/")
    @ApiOperation(notes = "Palauttaa kaikki muutospyynnön liitteet", value = "")
    public CompletableFuture<HttpEntity<Collection<Liite>>> getLiitteetByMuutospyyntoUuid(final @PathVariable String muutospyyntoUuid) {
        return getOr404(async(() -> muutospyyntoService.getLiitteetByUuid(muutospyyntoUuid)));
    }

    // hakee yksittäisen muutoksen (jos tarvii)
    @RequestMapping(method = GET, value = "/muutos/id/{uuid}")
    @ApiOperation(notes = "Palauttaa muutoksen tietokantatunnuksen perusteella", value = "")
    public CompletableFuture<HttpEntity<Muutos>> getMuutosByUuid(final @PathVariable String uuid) {
        return getOr404(async(() -> muutospyyntoService.getMuutosByUuId(uuid)));
    }


    // Vaihda muutospyynnön tilaa -> valmiina käsittelyyn
    @ApiOperation(notes = "Vie muutospyyntö esittelijän käsittelyyn", value = "")
    @RequestMapping(method = POST, value = "/tila/avoin/{uuid}")
    public HttpEntity<UUID> vieKasittelyyn(final @PathVariable String uuid) {
        return getOr404(muutospyyntoService.executeAction(uuid, Action.LAHETA).map(Muutospyynto::getUuid));
    }

    // Vaihda muutospyynnön tilaa -> ota esittelijänä käsittelyyn
    @ApiOperation(notes = "Ota muutospyyntö esittelijän käsittelyyn", value = "")
    @RequestMapping(method = POST, value = "/tila/valmistelussa/{uuid}")
    public HttpEntity<UUID> kasittelyssa(final @PathVariable String uuid) {
        return getOr404(muutospyyntoService.executeAction(uuid, Action.OTA_KASITTELYYN).map(Muutospyynto::getUuid));
    }

    @ApiOperation(notes = "Merkitse muutospyyntö esitellyksi", value = "")
    @RequestMapping(method = POST, value = "/tila/esittelyssa/{uuid}")
    public HttpEntity<UUID> esittele(final @PathVariable String uuid) {
        return getOr404(muutospyyntoService.executeAction(uuid, Action.ESITTELE).map(Muutospyynto::getUuid));
    }

    @ApiOperation(notes = "Merkitse muutospyyntö päätetyksi ja muodostaa muutospyynnön pohjalta uuden luvan", value = "")
    @RequestMapping(method = POST, value = "/tila/paatetty/{uuid}")
    public HttpEntity<UUID> valmis(final @PathVariable String uuid) {
        return getOr404(muutospyyntoService.executeAction(uuid, Action.PAATA).map(Muutospyynto::getUuid));
    }

    @ApiOperation(notes = "Poista muutospyyntö.", value = "")
    @RequestMapping(method = DELETE, value = "/{uuid}")
    public HttpEntity<UUID> delete(final @PathVariable String uuid) {
        return getOr404(muutospyyntoService.executeAction(uuid, Action.POISTA).map(Muutospyynto::getUuid));
    }

    @OivaAccess_Esittelija
    @RequestMapping(value = "/pdf/esikatsele/{uuid}", method = GET)
    @Produces({ MediaType.APPLICATION_PDF_VALUE })
    @ResponseBody
    @ApiOperation(notes = "Tuottaa muutospyynnön mukaisen luonnoksen tulevasta luvasta PDF-muodossa", value = "")
    public void previewPdf(final @PathVariable String uuid, final HttpServletResponse response, final HttpServletRequest request) {
        Optional<Muutospyynto> muutospyyntoOpt = muutospyyntoService.getByUuid(uuid);
        Optional<Lupa> lupaOpt = muutospyyntoService.generateLupaFromMuutospyynto(uuid);
        try {
            if (lupaOpt.isPresent() && muutospyyntoOpt.isPresent()) {
                final OivaTemplates.RenderOptions renderOptions = lupaRenderService.getLupaRenderOptions(lupaOpt).orElseThrow(IllegalStateException::new);
                final String lupaHtml = pebbleService.toHTML(lupaOpt.get(), renderOptions).orElseThrow(IllegalStateException::new);
                response.setContentType(MediaType.APPLICATION_PDF_VALUE);
                final String filename = '"' + muutospyyntoService.getMuutospyyntoPreviewPdfName(lupaOpt.get(), muutospyyntoOpt.get()) + '"';
                response.setHeader("Content-Disposition", "inline; filename=" + filename);
                if (!princeXMLService.toPDF(lupaHtml, response.getOutputStream(), renderOptions)) {
                    response.setStatus(get500().getStatusCode().value());
                    response.getWriter().write("Failed to generate lupa with uuid " + uuid);
                }
            } else {
                response.setStatus(notFound().getStatusCode().value());
                response.getWriter().write("No such muutospyynto with uuid " + uuid);
            }
        } catch (Exception e) {
            logger.error("Failed to generate muutospyynto lupa pdf with uuid {}", uuid, e);
            response.setStatus(get500().getStatusCode().value());
        }
    }

    @ApiOperation(notes = "Tarkistaa onko järjestelmässä muutospyynnölle syötetty asianumero", value = "")
    @RequestMapping(method = POST, value = "/duplikaattiasianumero")
    public Boolean duplicateAsianumeroExists(@RequestPart(value = "uuid", required = false) String uuid, @RequestPart(value = "asianumero") String asianumero) {
        return muutospyyntoService.duplicateAsianumeroExists(uuid, asianumero);
    }
}
