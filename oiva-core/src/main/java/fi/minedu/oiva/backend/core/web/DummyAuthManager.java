package fi.minedu.oiva.backend.core.web;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class DummyAuthManager implements AuthenticationManager {

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        return authentication;
    }
}
