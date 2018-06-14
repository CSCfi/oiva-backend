package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.Kohde;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Application;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.service.KohdeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.util.AsyncUtil.async;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + KohdeController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
public class KohdeController {

    public static final String path = "/kohteet";

    @Autowired
    private KohdeService service;

    @OivaAccess_Public
    @RequestMapping(method = GET)
    @ApiOperation(notes = "Palauttaa kaikki määräyksen kohteet", value = "")
    public CompletableFuture<Collection<Kohde>> getAll() {
        return async(service::getAll);
    }
}
