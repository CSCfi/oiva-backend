package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.Muutospyynto;
import fi.minedu.oiva.backend.jooq.tables.pojos.Muutos;

import fi.minedu.oiva.backend.security.annotations.OivaAccess_Kayttaja;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.service.MuutospyyntoService;
import fi.minedu.oiva.backend.util.RequestUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.util.AsyncUtil.async;
import static fi.minedu.oiva.backend.util.ControllerUtil.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(
        value = "${api.url.prefix}" + MuutospyyntoController.path,
        produces = { MediaType.APPLICATION_JSON_VALUE })
public class MuutospyyntoController {

    @Value("${templates.base.path}")
    private String templateBasePath;

    public static final String path = "/muutospyynnot";

    @Autowired
    private MuutospyyntoService service;


    // palauttaa kaikki järjestäjän muutospyynnöt
    //@OivaAccess_Kayttaja // TODO testataan kirjautumista
    @OivaAccess_Public
    @RequestMapping(method = GET, value = "/{ytunnus}")
    @ApiOperation(notes = "Palauttaa muutospyynnöt järjestäjän ytunnuksen perusteella", value = "")
    public CompletableFuture<Collection<Muutospyynto>> getByYtunnus(final @PathVariable String ytunnus,
                                                                    final HttpServletRequest request) {
        return async(() -> service.getByYtunnus(RequestUtils.getPathVariable(request, ytunnus)));
    }

    // hakee yksittäisen muutospyynnön ID:llä (refaktoroidaan UUID:lle)
    //@OivaAccess_Kayttaja // TODO testataan kirjautumista
    @OivaAccess_Public
    @RequestMapping(method = GET, value = "/id/{muutospyyntoId:[0-9]+}")
    @ApiOperation(notes = "Palauttaa muutospyynnön tietokantatunnuksen perusteella", value = "") // TODO UUID
    public CompletableFuture<HttpEntity<Muutospyynto>> getById(final @PathVariable long muutospyyntoId) {
        return getOr404(async(() -> service.getById(muutospyyntoId)));
    }

    // palauttaa kaikki muutospyynnön muutokset
    //@OivaAccess_Kayttaja // TODO testataan kirjautumista
    @OivaAccess_Public
    @RequestMapping(method = GET, value = "/muutokset/{muutospyyntoId}")
    @ApiOperation(notes = "Palauttaa kaikki muutospyynnön muutokset", value = "")
    public CompletableFuture<Collection<Muutos>> getMuutoksetByMuutospyyntoId(final @PathVariable long muutospyyntoId,
                                                                    final HttpServletRequest request) {
        return async(() -> service.getByMuutospyyntoId(muutospyyntoId));
    }

    // muokkaa muutospyyntöä
    //@OivaAccess_Kayttaja // TODO testataan kirjautumista
    @OivaAccess_Public
    @ApiOperation(notes = "Muokkaa muutospyyntöä", value = "")
    @RequestMapping(method = POST, value = "/update")
    public HttpEntity<Long> update(@RequestBody Muutospyynto muutospyynto) {

        if(null == muutospyynto) {
            return badRequest();
        }
        else if(muutospyynto.getId() < 1) {
            return badRequest();
        }
        else if(!service.validate(muutospyynto)) {
            return badRequest();
        }

        return getOr400(service.update(muutospyynto));
    }

    // tallentaa uuden muutospyynnön
    //@OivaAccess_Kayttaja // TODO testataan kirjautumista
    @OivaAccess_Public
    @ApiOperation(notes = "Tallentaa uuden muutospyynnön", value = "")
    @RequestMapping(method = PUT, value = "/create")
    public HttpEntity<Long> create(@RequestBody Muutospyynto muutospyynto) {

        if(null == muutospyynto) {
            return badRequest();

        }
        else if(!service.validate(muutospyynto)) {
            return badRequest();
        }

        return getOr400(service.create(muutospyynto));
    }

    // passivoi muutospyyntö
    //@OivaAccess_Kayttaja // TODO testataan kirjautumista
    @OivaAccess_Public
    @ApiOperation(notes = "Passivoi muutospyyntö", value = "")
    @RequestMapping(method = POST, value = "/passivoi/{muutospyyntoId:[0-9]+}")
    public HttpEntity<Long> passivoi(final @PathVariable long muutospyyntoId) {
        return getOr404(service.passivoi(muutospyyntoId));
    }

    // hakee yksittäisen muutoksen (jos tarvii)
    //@OivaAccess_Kayttaja // TODO testataan kirjautumista
    @OivaAccess_Public
    @RequestMapping(method = GET, value = "/muutos/id/{muutosId:[0-9]+}")
    @ApiOperation(notes = "Palauttaa muutoksen tietokantatunnuksen perusteella", value = "") // TODO UUID
    public CompletableFuture<HttpEntity<Muutos>> getMuutosById(final @PathVariable long muutosId) {
        return getOr404(async(() -> service.getMuutosById(muutosId)));
    }

    // tallentaa uuden muutoksen
    //@OivaAccess_Kayttaja // TODO testataan kirjautumista
    @OivaAccess_Public
    @ApiOperation(notes = "Tallentaa uuden muutoksen", value = "")
    @RequestMapping(method = PUT, value = "/muutos/create")
    public HttpEntity<Long> createMuutos(@RequestBody Muutos muutos) {

        if(null == muutos) {
            return badRequest();

        }
        else if(!service.validate(muutos)) {
            return badRequest();
        }

        return getOr400(service.createMuutos(muutos));
    }

    // muokkaa muutosta
    // @OivaAccess_Kayttaja // TODO testataan kirjautumista
    @OivaAccess_Public
    @ApiOperation(notes = "Muokkaa muutosta", value = "")
    @RequestMapping(method = POST, value = "/muutos/update")
    public HttpEntity<Long> updateMuutos(@RequestBody Muutos muutos) {

        if(null == muutos) {
            return badRequest();
        }
        else if(muutos.getId() < 1) {
            return badRequest();
        }
        else if(!service.validate(muutos)) {
            return badRequest();
        }

        return getOr400(service.updateMuutos(muutos));
    }

}
