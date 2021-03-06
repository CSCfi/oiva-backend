package fi.minedu.oiva.backend.core.web.controller;

import fi.minedu.oiva.backend.model.entity.oiva.Esitysmalli;
import fi.minedu.oiva.backend.core.security.annotations.OivaAccess_Application;
import fi.minedu.oiva.backend.core.service.EsitysmalliService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.core.util.AsyncUtil.async;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + EsitysmalliController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
@Api(description = "Esitysmallien hallinta")
public class EsitysmalliController {

    public static final String path = "/esitysmallit";

    @Autowired
    private EsitysmalliService service;

    @OivaAccess_Application
    @RequestMapping(method = GET)
    @ApiOperation(notes = "Palauttaa kaikki esitysmallit", value = "")
    public CompletableFuture<Collection<Esitysmalli>> getAll() {
        return async(service::getAll);
    }
}