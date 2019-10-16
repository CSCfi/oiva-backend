package fi.minedu.oiva.backend.core.web.controller;

import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Esittelija;
import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Kayttaja;
import fi.minedu.oiva.backend.core.service.MuutospyyntoService;
import fi.minedu.oiva.backend.core.util.RequestUtils;
import fi.minedu.oiva.backend.model.entity.oiva.Liite;
import fi.minedu.oiva.backend.model.entity.oiva.Muutos;
import fi.minedu.oiva.backend.model.entity.oiva.Muutospyynto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.core.service.MuutospyyntoService.Muutospyyntotila;
import static fi.minedu.oiva.backend.core.util.AsyncUtil.async;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.badRequest;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.getOr400;
import static fi.minedu.oiva.backend.model.util.ControllerUtil.getOr404;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@RestController
@RequestMapping(
        value = "${api.url.prefix}" + MuutospyyntoController.path,
        produces = {MediaType.APPLICATION_JSON_VALUE})
@Api(description = "Muutospyyntöjen hallinta")
public class MuutospyyntoController {

    @Value("${templates.base.path}")
    private String templateBasePath;

    public static final String path = "/muutospyynnot";

    private final MuutospyyntoService service;

    @Autowired
    public MuutospyyntoController(MuutospyyntoService service) {
        this.service = service;
    }


    // palauttaa kaikki järjestäjän muutospyynnöt
    @OivaAccess_Kayttaja
    @RequestMapping(method = GET, value = "/{ytunnus}")
    @ApiOperation(notes = "Palauttaa muutospyynnöt järjestäjän ytunnuksen perusteella", value = "")
    public CompletableFuture<Collection<Muutospyynto>> getByYtunnus(final @PathVariable String ytunnus,
                                                                    final HttpServletRequest request) {
        return async(() -> service.getByYtunnus(RequestUtils.getPathVariable(request, ytunnus)));
    }

    // palauttaa kaikki avoimet muutospyynnöt
    @OivaAccess_Esittelija
    @RequestMapping(method = GET, value = "/avoimet")
    @ApiOperation(notes = "Palauttaa muutospyynnöt esittelijän perusteella", value = "")
    public CompletableFuture<Collection<Muutospyynto>> getAvoimetMuutospyynnot(final HttpServletRequest request) {
        return async(() -> service.getMuutospyynnot(Muutospyyntotila.AVOIN));
    }

    // palauttaa kaikki valmistelussa olevat muutospyynnöt
    @OivaAccess_Esittelija
    @RequestMapping(method = GET, value = "/valmistelussa")
    @ApiOperation(notes = "Palauttaa muutospyynnöt esittelijän perusteella", value = "")
    public CompletableFuture<Collection<Muutospyynto>> getValmistelussaMuutospyynnot(final HttpServletRequest request) {
        return async(() -> service.getMuutospyynnot(Muutospyyntotila.VALMISTELUSSA));
    }

    // palauttaa kaikki päätetyt muutospyynnöt (TARVITAANKO!!!?)
    @OivaAccess_Esittelija
    @RequestMapping(method = GET, value = "/paatetyt")
    @ApiOperation(notes = "Palauttaa muutospyynnöt esittelijän perusteella", value = "")
    public CompletableFuture<Collection<Muutospyynto>> getPaatetytMuutospyynnot(final HttpServletRequest request) {
        return async(() -> service.getMuutospyynnot(Muutospyyntotila.PAATETTY));
    }


    // hakee yksittäisen muutospyynnön UUID:llä
    @OivaAccess_Kayttaja
    @RequestMapping(method = GET, value = "/id/{uuid}")
    @ApiOperation(notes = "Palauttaa muutospyynnön uuid:n perusteella", value = "")
    public CompletableFuture<HttpEntity<Muutospyynto>> getById(final @PathVariable String uuid) {
        return getOr404(async(() -> service.getByUuid(uuid)));
    }

    // palauttaa kaikki muutospyynnön muutokset
    @OivaAccess_Kayttaja
    @RequestMapping(method = GET, value = "/muutokset/{muutospyyntoUuid}")
    @ApiOperation(notes = "Palauttaa kaikki muutospyynnön muutokset", value = "")
    public CompletableFuture<Collection<Muutos>> getMuutoksetByMuutospyyntoId(final @PathVariable String muutospyyntoUuid) {
        return async(() -> service.getByMuutospyyntoUuid(muutospyyntoUuid));
    }

    @OivaAccess_Kayttaja
    @ApiOperation(notes = "Tallentaa muutospyynnön", value = "")
    @RequestMapping(method = POST, value = "/tallenna", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpEntity<Muutospyynto> save(@RequestPart(value = "muutospyynto") Muutospyynto muutospyynto,
                                         MultipartHttpServletRequest request) {
        if (inValid(muutospyynto)) {
            return badRequest();
        }
        Optional<Muutospyynto> result;
        if (muutospyynto.getUuid() != null) {
            result = service.update(muutospyynto, request.getFileMap());
        } else {
            result = service.save(muutospyynto, request.getFileMap());
        }
        // Load response freshly from the db
        return getOr400(service.getById(result.map(Muutospyynto::getId).orElse(null)));
    }

    @OivaAccess_Kayttaja
    @RequestMapping(method = GET, value = "{muutospyyntoUuid}/liitteet/")
    @ApiOperation(notes = "Palauttaa kaikki muutospyynnön liitteet", value = "")
    public CompletableFuture<HttpEntity<Collection<Liite>>> getLiitteetByMuutospyyntoUuid(final @PathVariable String muutospyyntoUuid) {
        return getOr404(async(() -> service.getLiitteetByUuid(muutospyyntoUuid)));
    }

    // hakee yksittäisen muutoksen (jos tarvii)
    @OivaAccess_Kayttaja
    @RequestMapping(method = GET, value = "/muutos/id/{uuid}")
    @ApiOperation(notes = "Palauttaa muutoksen tietokantatunnuksen perusteella", value = "")
    public CompletableFuture<HttpEntity<Muutos>> getMuutosByUuid(final @PathVariable String uuid) {
        return getOr404(async(() -> service.getMuutosByUuId(uuid)));
    }


    // Vaihda muutospyynnön tilaa ->  passivoi
    @OivaAccess_Kayttaja
    @ApiOperation(notes = "Passivoi muutospyyntö", value = "")
    @RequestMapping(method = POST, value = "/passivoi/{uuid}")
    public HttpEntity<UUID> passivoi(final @PathVariable String uuid) {
        return getOr404(service.changeTila(uuid, Muutospyyntotila.PASSIVOITU));
    }


    // Vaihda muutospyynnön tilaa -> valmiina käsittelyyn
    @OivaAccess_Kayttaja
    @ApiOperation(notes = "Vie muutospyyntö esittelijän käsittelyyn", value = "")
    @RequestMapping(method = POST, value = "/tila/avoin/{uuid}")
    public HttpEntity<UUID> vieKasittelyyn(final @PathVariable String uuid) {
        return getOr404(service.changeTila(uuid, Muutospyyntotila.AVOIN));
    }

    // Vaihda muutospyynnön tilaa -> ota esittelijänä käsittelyyn
    @OivaAccess_Esittelija
    @ApiOperation(notes = "Ota muutospyyntö esittelijän käsittelyyn", value = "")
    @RequestMapping(method = POST, value = "/tila/valmistelussa/{uuid}")
    public HttpEntity<UUID> kasittelyssa(final @PathVariable String uuid) {
        return getOr404(service.changeTila(uuid, Muutospyyntotila.VALMISTELUSSA));
    }

    // Vaihda muutospyynnön tilaa -> palauta täydennettäväksi
    @OivaAccess_Esittelija
    @ApiOperation(notes = "Palauta järjestäjän täydennettäväksi", value = "")
    @RequestMapping(method = POST, value = "/tila/taydennettava/{uuid}")
    public HttpEntity<UUID> taydennettava(final @PathVariable String uuid) {
        return getOr404(service.changeTila(uuid, Muutospyyntotila.TAYDENNETTAVA));
    }

    // Vaihda muutospyynnön tilaa -> valmis
    @OivaAccess_Kayttaja
    @ApiOperation(notes = "Merkitse muutospyyntö valmiiksi", value = "")
    @RequestMapping(method = POST, value = "/tila/paatetty/{uuid}")
    public HttpEntity<UUID> valmis(final @PathVariable String uuid) {
        return getOr404(service.changeTila(uuid, Muutospyyntotila.PAATETTY));
    }

    private boolean inValid(Muutospyynto muutospyynto) {
        if (null == muutospyynto) {
            return true;
        }
        return !service.validate(muutospyynto);
    }

}
