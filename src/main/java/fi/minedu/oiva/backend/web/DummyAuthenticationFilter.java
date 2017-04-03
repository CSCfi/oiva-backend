package fi.minedu.oiva.backend.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Development time Spring Security Filter - which takes information from a cookie and injects that into
 * the SecurityContext for the user without any login.
 *
 */
public class DummyAuthenticationFilter extends
    AbstractAuthenticationProcessingFilter {

    static final List<GrantedAuthority> AUTHORITIES = new ArrayList<GrantedAuthority>();

    private final Logger logger = LoggerFactory.getLogger("DummyAuthenticationFilter");

    static {
        AUTHORITIES.add(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public DummyAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login", "GET"));
        this.setAuthenticationManager(new DummyAuthManager());
        logger.info("Creating dummy filter");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        final Authentication result = new UsernamePasswordAuthenticationToken("testeruser", "testcredentials", AUTHORITIES);
        logger.info("Doing dummy authnetication ");
        return result;
    }
}
