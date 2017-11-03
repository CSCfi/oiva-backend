package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.security.CustomUserDetailsMapper;
import fi.minedu.oiva.backend.security.annotations.OivaAccess;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Application;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static fi.minedu.oiva.backend.util.CollectionUtils.mapOf;
import static org.jooq.lambda.tuple.Tuple.tuple;

@Service
public class AuthService {

    @OivaAccess_Application
    public Map<String, Object> getMe() {
        return mapOf(
            tuple("oid", userName()),
            tuple("roles", userRoles())
        );
    }

    protected Optional<Authentication> userAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    protected Optional<CustomUserDetailsMapper.UserDetailsWrapper> userPrincipal() {
        final Optional<Authentication> authOpt = userAuthentication();
        return Optional.ofNullable(authOpt.isPresent() ? (CustomUserDetailsMapper.UserDetailsWrapper) authOpt.get().getPrincipal() : null);
    }

    protected boolean isOivaUser() {
        return userRoles().contains(OivaAccess.Role_Application);
    }

    protected boolean onlyPublic() {
        return !isOivaUser();
    }

    protected Optional<String> userName() {
        final Optional<CustomUserDetailsMapper.UserDetailsWrapper> principalOpt = userPrincipal();
        return Optional.ofNullable(principalOpt.isPresent() ? principalOpt.get().getUsername() : null);
    }

    protected List<String> userRoles() {
        final List<String> userRoles = new ArrayList<>();
        userAuthentication().ifPresent(auth -> auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).forEach(userRoles::add));
        return userRoles;
    }
}
