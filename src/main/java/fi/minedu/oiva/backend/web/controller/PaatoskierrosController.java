package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.entity.Paatoskierros;
import fi.minedu.oiva.backend.service.PaatoskierrosService;
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
    value = "${api.url.prefix}" + PaatoskierrosController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
public class PaatoskierrosController {

    public static final String path = "/paatoskierrokset";

    @Autowired
    private PaatoskierrosService service;

    @ApiOperation(notes = "Palauttaa kaikki päätöskierrokset", value = "")
    @RequestMapping(method = GET)
    public CompletableFuture<Collection<Paatoskierros>> getAll() {
        return async(() -> service.getAll());
    }
}
