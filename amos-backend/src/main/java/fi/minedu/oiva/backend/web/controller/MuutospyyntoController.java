package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.oiva.Muutospyynto;
import fi.minedu.oiva.backend.entity.oiva.Muutos;

import fi.minedu.oiva.backend.security.annotations.OivaAccess_Public;

import fi.minedu.oiva.backend.service.MuutospyyntoService;
import fi.minedu.oiva.backend.util.RequestUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.util.AsyncUtil.async;
import static fi.minedu.oiva.backend.util.ControllerUtil.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static fi.minedu.oiva.backend.service.MuutospyyntoService.Muutospyyntotila;


@RestController
@RequestMapping(
        value = "${api.url.prefix}" + MuutospyyntoController.path,
        produces = { MediaType.APPLICATION_JSON_VALUE })
@Api(description = "Muutospyyntöjen hallinta")
public class MuutospyyntoController {

    @Value("${templates.base.path}")
    private String templateBasePath;

    public static final String path = "/muutospyynnot";

    @Autowired
    private MuutospyyntoService service;


    // palauttaa kaikki järjestäjän muutospyynnöt
    @OivaAccess_Public
    @RequestMapping(method = GET, value = "/{ytunnus}")
    @ApiOperation(notes = "Palauttaa muutospyynnöt järjestäjän ytunnuksen perusteella", value = "")
    public CompletableFuture<Collection<Muutospyynto>> getByYtunnus(final @PathVariable String ytunnus,
                                                                    final HttpServletRequest request) {
        return async(() -> service.getByYtunnus(RequestUtils.getPathVariable(request, ytunnus)));
    }

    // palauttaa kaikki avoimet muutospyynnöt
    @OivaAccess_Public
    @RequestMapping(method = GET, value = "/avoimet")
    @ApiOperation(notes = "Palauttaa muutospyynnöt esittelijän perusteella", value = "")
    public CompletableFuture<Collection<Muutospyynto>> getAvoimetMuutospyynnot(final HttpServletRequest request) {
        return async(() -> service.getMuutospyynnot(Muutospyyntotila.AVOIN));
    }

    // palauttaa kaikki valmistelussa olevat muutospyynnöt
    @OivaAccess_Public
    @RequestMapping(method = GET, value = "/valmistelussa")
    @ApiOperation(notes = "Palauttaa muutospyynnöt esittelijän perusteella", value = "")
    public CompletableFuture<Collection<Muutospyynto>> getValmistelussaMuutospyynnot(final HttpServletRequest request) {
        return async(() -> service.getMuutospyynnot(Muutospyyntotila.VALMISTELUSSA));
    }

    // palauttaa kaikki päätetyt muutospyynnöt (TARVITAANKO!!!?)
    @OivaAccess_Public
    @RequestMapping(method = GET, value = "/paatetyt")
    @ApiOperation(notes = "Palauttaa muutospyynnöt esittelijän perusteella", value = "")
    public CompletableFuture<Collection<Muutospyynto>> getPaatetytMuutospyynnot(final HttpServletRequest request) {
        return async(() -> service.getMuutospyynnot(Muutospyyntotila.PAATETTY));
    }


    // hakee yksittäisen muutospyynnön UUID:llä
    @OivaAccess_Public
    @RequestMapping(method = GET, value = "/id/{uuid}")
    @ApiOperation(notes = "Palauttaa muutospyynnön uuid:n perusteella", value = "") // TODO UUID
    public CompletableFuture<HttpEntity<Muutospyynto>> getById(final @PathVariable String uuid) {
        return getOr404(async(() -> service.getByUuid(uuid)));
    }

    // palauttaa kaikki muutospyynnön muutokset
    @OivaAccess_Public
    @RequestMapping(method = GET, value = "/muutokset/{muutospyyntoUuid}")
    @ApiOperation(notes = "Palauttaa kaikki muutospyynnön muutokset", value = "")
    public CompletableFuture<Collection<Muutos>> getMuutoksetByMuutospyyntoId(final @PathVariable String muutospyyntoUuid) {
        return async(() -> service.getByMuutospyyntoUuid(muutospyyntoUuid));
    }

    // tallentaa muutospyynnön
    @OivaAccess_Public
    @ApiOperation(notes = "Tallentaa muutospyynnön", value = "")
    @RequestMapping(method = PUT, value = "/tallenna")
    public HttpEntity<UUID> save(@RequestBody Muutospyynto muutospyynto) {

        if(null == muutospyynto) {
            return badRequest();

        }
        else if(!service.validate(muutospyynto)) {
            return badRequest();
        }

        System.out.println("create or update: " + muutospyynto.getUuid());
        return getOr400(service.save(muutospyynto));
    }

    // hakee yksittäisen muutoksen (jos tarvii)
    @OivaAccess_Public
    @RequestMapping(method = GET, value = "/muutos/id/{uuid}")
    @ApiOperation(notes = "Palauttaa muutoksen tietokantatunnuksen perusteella", value = "") // TODO UUID
    public CompletableFuture<HttpEntity<Muutos>> getMuutosByUuid(final @PathVariable String uuid) {
        return getOr404(async(() -> service.getMuutosByUuId(uuid)));
    }


    // Vaihda muutospyynnön tilaa ->  passivoi
    @OivaAccess_Public
    @ApiOperation(notes = "Passivoi muutospyyntö", value = "")
    @RequestMapping(method = POST, value = "/passivoi/{uuid}")
    public HttpEntity<UUID> passivoi(final @PathVariable String uuid) {
        return getOr404(service.changeTila(uuid, Muutospyyntotila.PASSIVOITU));
    }


    // Vaihda muutospyynnön tilaa -> valmiina käsittelyyn
    @OivaAccess_Public
    @ApiOperation(notes = "Vie muutospyyntö esittelijän käsittelyyn", value = "")
    @RequestMapping(method = POST, value = "/tila/avoin/{uuid}")
    public HttpEntity<UUID> vieKasittelyyn(final @PathVariable String uuid) {
        return getOr404(service.changeTila(uuid, Muutospyyntotila.AVOIN));
    }

    // Vaihda muutospyynnön tilaa -> ota esittelijänä käsittelyyn
    @OivaAccess_Public
    @ApiOperation(notes = "Ota muutospyyntö esittelijän käsittelyyn", value = "")
    @RequestMapping(method = POST, value = "/tila/valmistelussa/{uuid}")
    public HttpEntity<UUID> kasittelyssa(final @PathVariable String uuid) {
        return getOr404(service.changeTila(uuid, Muutospyyntotila.VALMISTELUSSA));
    }

    // Vaihda muutospyynnön tilaa -> palauta täydennettäväksi
    @OivaAccess_Public
    @ApiOperation(notes = "Palauta järjestäjän täydennettäväksi", value = "")
    @RequestMapping(method = POST, value = "/tila/taydennettava/{uuid}")
    public HttpEntity<UUID> taydennettava(final @PathVariable String uuid) {
        return getOr404(service.changeTila(uuid, Muutospyyntotila.TAYDENNETTAVA));
    }

    // Vaihda muutospyynnön tilaa -> valmis
    @OivaAccess_Public
    @ApiOperation(notes = "Merkitse muutospyyntö valmiiksi", value = "")
    @RequestMapping(method = POST, value = "/tila/paatetty/{uuid}")
    public HttpEntity<UUID> valmis(final @PathVariable String uuid) {
        return getOr404(service.changeTila(uuid, Muutospyyntotila.PAATETTY));
    }

}
