package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.core.web.controller.BaseAuthController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "${api.url.prefix}" + AuthController.path)
@Api(description = "Autentikointi")
public class AuthController extends BaseAuthController {}
