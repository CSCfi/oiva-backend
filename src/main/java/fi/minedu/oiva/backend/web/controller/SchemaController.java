package fi.minedu.oiva.backend.web.controller;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.service.SchemaService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.util.AsyncUtil.async;
import static fi.minedu.oiva.backend.util.ControllerUtil.getOr404;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + SchemaController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
public class SchemaController {

    public static final String path = "/schema";

    @Autowired
    private SchemaService service;

    @OivaAccess_Public
    @RequestMapping(value="/oiva/{entityClass:.*}",method = GET)
    @ApiOperation(notes = "Palauttaa oiva-skeeman json-kuvauksen luokkanimen perusteella", value = "")
    public CompletableFuture<HttpEntity<JsonSchema>> getOivaSchema(final @PathVariable String entityClass) {
        return getOr404(async(() -> service.getOivaSchema(entityClass)));
    }

    @OivaAccess_Public
    @RequestMapping(value="/opintopolku/{entityClass:.*}",method = GET)
    @ApiOperation(notes = "Palauttaa opintopolku-skeeman json-kuvauksen luokkanimen perusteella", value = "")
    public CompletableFuture<HttpEntity<JsonSchema>> getOpintopolkuSchema(final @PathVariable String entityClass) {
        return getOr404(async(() -> service.getOpintopolkuSchema(entityClass)));
    }
}