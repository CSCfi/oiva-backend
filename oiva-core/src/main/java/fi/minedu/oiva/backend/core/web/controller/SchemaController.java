package fi.minedu.oiva.backend.core.web.controller;

import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + SchemaController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
@Api(description = "Tietomallien kuvaukset")
public class SchemaController extends BaseSchemaController {}