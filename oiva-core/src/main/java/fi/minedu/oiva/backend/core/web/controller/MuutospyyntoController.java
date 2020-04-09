package fi.minedu.oiva.backend.core.web.controller;

import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Application;
import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Esittelija;
import fi.minedu.oiva.backend.core.service.LupamuutosService;
import fi.minedu.oiva.backend.core.service.MuutospyyntoService;
import fi.minedu.oiva.backend.core.util.RequestUtils;
import fi.minedu.oiva.backend.model.entity.oiva.Liite;
import fi.minedu.oiva.backend.model.entity.oiva.Muutos;
import fi.minedu.oiva.backend.model.entity.oiva.Muutospyynto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.core.service.MuutospyyntoService.Action;
import static fi.minedu.oiva.backend.core.service.MuutospyyntoService.Muutospyyntotila;
import static fi.minedu.oiva.backend.core.util.AsyncUtil.async;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.getOr400;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.getOr404;
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

    @Value("${templates.base.path}")
    private String templateBasePath;

    public static final String path = "/muutospyynnot";

    private final MuutospyyntoService muutospyyntoService;
    private final LupamuutosService lupamuutosService;

    @Autowired
    public MuutospyyntoController(MuutospyyntoService muutospyyntoService, LupamuutosService lupamuutosService) {
        this.muutospyyntoService = muutospyyntoService;
        this.lupamuutosService = lupamuutosService;
    }


    // palauttaa kaikki järjestäjän muutospyynnöt
    @RequestMapping(method = GET, value = "/{ytunnus}")
    @ApiOperation(notes = "Palauttaa muutospyynnöt järjestäjän ytunnuksen perusteella", value = "")
    public CompletableFuture<Collection<Muutospyynto>> getByYtunnus(final @PathVariable String ytunnus,
                                                                    final HttpServletRequest request) {
        return async(() -> muutospyyntoService.getByYtunnus(RequestUtils.getPathVariable(request, ytunnus)));
    }

    // palauttaa kaikki avoimet muutospyynnöt
    @OivaAccess_Esittelija
    @RequestMapping(method = GET, value = "/avoimet")
    @ApiOperation(notes = "Palauttaa muutospyynnöt esittelijän perusteella", value = "")
    public CompletableFuture<Collection<Muutospyynto>> getAvoimetMuutospyynnot(final HttpServletRequest request) {
        return async(() -> muutospyyntoService.getMuutospyynnot(Muutospyyntotila.AVOIN, false));
    }

    // palauttaa kaikki valmistelussa olevat muutospyynnöt
    @OivaAccess_Esittelija
    @RequestMapping(method = GET, value = "/valmistelussa")
    @ApiOperation(notes = "Palauttaa muutospyynnöt esittelijän perusteella", value = "")
    public CompletableFuture<Collection<Muutospyynto>> getValmistelussaMuutospyynnot(
            @RequestParam(required = false, defaultValue = "false") boolean vainOmat) {
        return async(() -> muutospyyntoService.getMuutospyynnot(Muutospyyntotila.VALMISTELUSSA, vainOmat));
    }

    // palauttaa kaikki päätetyt muutospyynnöt (TARVITAANKO!!!?)
    @OivaAccess_Esittelija
    @RequestMapping(method = GET, value = "/paatetyt")
    @ApiOperation(notes = "Palauttaa muutospyynnöt esittelijän perusteella", value = "")
    public CompletableFuture<Collection<Muutospyynto>> getPaatetytMuutospyynnot(final HttpServletRequest request) {
        return async(() -> muutospyyntoService.getMuutospyynnot(Muutospyyntotila.PAATETTY, false));
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


    /*

    TODO: Commented out because these have missing access rules

    // Vaihda muutospyynnön tilaa -> valmis
    @ApiOperation(notes = "Merkitse muutospyyntö valmiiksi", value = "")
    @RequestMapping(method = POST, value = "/tila/paatetty/{uuid}")
    public HttpEntity<UUID> valmis(final @PathVariable String uuid) {
        return getOr404(service.findMuutospyyntoAndSetTila(uuid, Muutospyyntotila.PAATETTY));
    }

    // Vaihda muutospyynnön tilaa ->  passivoi
    @ApiOperation(notes = "Passivoi muutospyyntö", value = "")
    @RequestMapping(method = POST, value = "/passivoi/{uuid}")
    public HttpEntity<UUID> passivoi(final @PathVariable String uuid) {
        return getOr404(service.findMuutospyyntoAndSetTila(uuid, Muutospyyntotila.PASSIVOITU));
    }

    // Vaihda muutospyynnön tilaa -> palauta täydennettäväksi
    @ApiOperation(notes = "Palauta järjestäjän täydennettäväksi", value = "")
    @RequestMapping(method = POST, value = "/tila/taydennettava/{uuid}")
    public HttpEntity<UUID> taydennettava(final @PathVariable String uuid) {
        return getOr404(service.findMuutospyyntoAndSetTila(uuid, Muutospyyntotila.TAYDENNETTAVA));
    }

 */
}
