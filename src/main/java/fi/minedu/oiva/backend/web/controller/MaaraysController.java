package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.Maarays;
import fi.minedu.oiva.backend.entity.Maaraystyyppi;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.service.MaaraysService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.util.AsyncUtil.async;
import static fi.minedu.oiva.backend.util.ControllerUtil.options;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + MaaraysController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
public class MaaraysController {

    public static final String path = "/maaraykset";

    @Autowired
    private MaaraysService service;

    @OivaAccess_Public
    @RequestMapping(value = "/lupa/{lupaId:[0-9]+}/{kohdeTunniste}", method = GET)
    @ApiOperation(notes = "Palauttaa kaikki luvan määräykset joilla on tietty kohde", value = "")
    public CompletableFuture<Collection<Maarays>> getAllByLupaAndKohde(
        final @PathVariable Long lupaId, final @PathVariable String kohdeTunniste,
        final @RequestParam(value = "with", required = false) String with) {
        return async(() -> service.getByLupaAndKohde(lupaId, kohdeTunniste, options(with)));
    }


    @OivaAccess_Public
    @RequestMapping(method = GET, value="/maaraystyypit")
    @ApiOperation(notes = "Palauttaa kaikki määäykset", value = "")
    public CompletableFuture<Collection<Maaraystyyppi>> getAllMaaraystyyppi() {
        return async(service::getAllMaaraystyyppi);
    }

}
