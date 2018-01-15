package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.security.annotations.OivaAccess_Application;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Public;
import fi.minedu.oiva.backend.service.AuthService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static fi.minedu.oiva.backend.util.AsyncUtil.async;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(value = "${api.url.prefix}" + AuthController.path)
public class AuthController {

    public static final String path = "/auth";

    @Autowired
    private AuthService service;

    @Value("${cas.baseUrl}${cas.url.prefix}${cas.url.login}")
    private String casLoginUrl;

    @Value("${oiva.baseUrl}${cas.service.url}")
    private String casServiceUrl;

    @OivaAccess_Application
    @RequestMapping(value = "/me", method = GET)
    @ApiOperation(notes = "Palauttaa aktiivisen käyttäjän tiedot ja roolit", value = "", authorizations = @Authorization(value = "CAS"))
    public CompletableFuture<Map<String, Object>> getMe() {
        return async(service::getMe);
    }

    @OivaAccess_Public
    @RequestMapping(value = "/login", method = GET)
    @ApiOperation(notes = "CAS-sisäänkirjautuminen", value = "")
    public void login(final HttpServletRequest request, final HttpServletResponse response, final @RequestParam String redirect) throws IOException {
        request.getSession().setAttribute("redirect", redirect);
        response.sendRedirect(casLoginUrl + "?service=" + casServiceUrl);
    }
}
