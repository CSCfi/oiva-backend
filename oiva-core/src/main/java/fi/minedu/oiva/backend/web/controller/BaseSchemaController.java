package fi.minedu.oiva.backend.web.controller;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.service.SchemaService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.util.AsyncUtil.async;
import static fi.minedu.oiva.backend.util.ControllerUtil.getOr404;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

public abstract class BaseSchemaController {

    public static final String path = "/schema";

    @Autowired
    private SchemaService service;

    @OivaAccess_Public
    @RequestMapping(value="/oiva",method = GET)
    @ApiOperation(notes = "Palauttaa oiva-skeeman luokkanimet", value = "")
    public CompletableFuture<Collection<String>> getOivaSchemaClasses() {
        return async(service::getOivaSchemaClasses);
    }

    @OivaAccess_Public
    @RequestMapping(value="/oiva/{entityClass:.*}",method = GET)
    @ApiOperation(notes = "Palauttaa oiva-skeeman json-kuvauksen luokkanimen perusteella", value = "")
    public CompletableFuture<HttpEntity<JsonSchema>> getOivaSchema(final @PathVariable String entityClass) {
        service.getOivaSchemaClasses();
        return getOr404(async(() -> service.getOivaSchema(entityClass)));
    }

    @OivaAccess_Public
    @RequestMapping(value="/opintopolku",method = GET)
    @ApiOperation(notes = "Palauttaa opintopolku-skeeman luokkanimet", value = "")
    public CompletableFuture<Collection<String>> getOpintopolkuSchemaClasses() {
        return async(service::getOpintopolkuSchemaClasses);
    }

    @OivaAccess_Public
    @RequestMapping(value="/opintopolku/{entityClass:.*}",method = GET)
    @ApiOperation(notes = "Palauttaa opintopolku-skeeman json-kuvauksen luokkanimen perusteella", value = "")
    public CompletableFuture<HttpEntity<JsonSchema>> getOpintopolkuSchema(final @PathVariable String entityClass) {
        return getOr404(async(() -> service.getOpintopolkuSchema(entityClass)));
    }

    @OivaAccess_Public
    @RequestMapping(value="/export",method = GET)
    @ApiOperation(notes = "Palauttaa export-skeeman luokkanimet", value = "")
    public CompletableFuture<Collection<String>> getExportSchemaClasses() {
        return async(service::getExportSchemaClasses);
    }

    @OivaAccess_Public
    @RequestMapping(value="/export/{entityClass:.*}",method = GET)
    @ApiOperation(notes = "Palauttaa export-skeeman json-kuvauksen luokkanimen perusteella", value = "")
    public CompletableFuture<HttpEntity<JsonSchema>> getExportSchema(final @PathVariable String entityClass) {
        return getOr404(async(() -> service.getExportSchema(entityClass)));
    }
}