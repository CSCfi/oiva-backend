package fi.minedu.oiva.backend.web.controller;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import fi.minedu.oiva.backend.service.JsonSchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static fi.minedu.oiva.backend.util.ControllerUtil.getOr404;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + JsonSchemaController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
public class JsonSchemaController {
    public static final String path = "/jsonschema";

    @Autowired
    private JsonSchemaService service;

    @RequestMapping(value="/{entityClass:.*}",method = GET)
    public HttpEntity<JsonSchema> get(@PathVariable String entityClass) {
        return getOr404(service.getSchema(entityClass));
    }

}
