package fi.minedu.oiva.backend.web.controller;

import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    value = "${api.url.prefix}" + LocalizationController.path,
    produces = { MediaType.APPLICATION_JSON_VALUE })
@Api(description = "Kielikäännökset")
public class LocalizationController extends BaseLocalizationController {}
