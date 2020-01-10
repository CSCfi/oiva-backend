package fi.minedu.oiva.backend.core.web.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "${api.url.prefix}" + HealthController.path)
@Api(description = "Palvelun tilatarkastukset")
public class HealthController extends BaseHealthController {}
