package fi.minedu.oiva.backend.web.controller;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import fi.minedu.oiva.backend.service.SchemaService;
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
    value = "${api.url.prefix}" + SchemaController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
public class SchemaController {

    public static final String path = "/schema";

    @Autowired
    private SchemaService service;

    @RequestMapping(value="/oiva/{entityClass:.*}",method = GET)
    public HttpEntity<JsonSchema> getOivaSchema(@PathVariable final String entityClass) {
        return getOr404(service.getOivaSchema(entityClass));
    }

    @RequestMapping(value="/opintopolku/{entityClass:.*}",method = GET)
    public HttpEntity<JsonSchema> getOpintopolkuSchema(@PathVariable final String entityClass) {
        return getOr404(service.getOpintopolkuSchema(entityClass));
    }
}
