package fi.minedu.oiva.backend.web.controller;

import fi.minedu.oiva.backend.security.CustomUserDetailsMapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fi.minedu.oiva.backend.util.CollectionUtils.mapOf;
import static org.jooq.lambda.tuple.Tuple.tuple;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(value = "${api.url.prefix}" + AuthController.path)
public class AuthController {

    public static final String path = "/auth";

    @Value("${cas.baseUrl}${cas.url.prefix}${cas.url.login}")
    private String casLoginUrl;

    @Value("${oiva.baseUrl}${cas.service.url}")
    private String casServiceUrl;

    @ApiOperation(notes = "Palauttaa aktiivisen käyttäjän tiedot ja roolit", value = "")
    @RequestMapping(value = "/me", method = GET)
    @PreAuthorize("isSignedIn()")
    public Map<String, Object> getMe() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final CustomUserDetailsMapper.UserDetailsWrapper principal = (CustomUserDetailsMapper.UserDetailsWrapper) auth.getPrincipal();
        final List<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return mapOf(
            tuple("oid", principal.getUsername()),
            tuple("roles", roles)
        );
    }

    @ApiOperation(notes = "CAS redirect", value = "")
    @RequestMapping("/login")
    public void login(final HttpServletRequest request, final HttpServletResponse response, @RequestParam final String redirect) throws IOException {
        request.getSession().setAttribute("redirect", redirect);
        response.sendRedirect(casLoginUrl + "?service=" + casServiceUrl);
    }
}
