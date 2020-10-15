package fi.minedu.oiva.backend.core.web.controller;

import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.core.service.KohdeService;
import fi.minedu.oiva.backend.model.entity.oiva.Kohde;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.core.util.AsyncUtil.async;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(
        value = "${api.url.prefix}" + KohdeController.path,
        produces = {MediaType.APPLICATION_JSON_VALUE})
@Api(description = "Kohteiden hallinta")
public class KohdeController {

    public static final String path = "/kohteet";

    @Autowired
    private KohdeService service;

    @OivaAccess_Public
    @RequestMapping(method = GET)
    @ApiOperation(notes = "Palauttaa kaikki määräyksen kohteet koulutustyypittäin.", value = "")
    public CompletableFuture<Collection<Kohde>> getAll(@RequestParam(required = false) String koulutustyyppi) {
        return async(() -> service.getAll(koulutustyyppi));
    }
}
