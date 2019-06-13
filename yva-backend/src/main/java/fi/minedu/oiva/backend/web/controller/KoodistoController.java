package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.core.web.controller.BaseKoodistoController;
import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + KoodistoController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
@Api(description = "Opintopolun koodistot")
public class KoodistoController extends BaseKoodistoController {}
