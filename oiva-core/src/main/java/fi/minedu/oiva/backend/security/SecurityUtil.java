package fi.minedu.oiva.backend.security;

import fi.minedu.oiva.backend.security.annotations.OivaAccess;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class SecurityUtil {

    private static Optional<Authentication> userAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    public static Optional<OivaUserDetails> userDetails() {
        return userAuthentication().map(a -> (OivaUserDetails) a.getPrincipal());
    }

    public static Optional<String> userName() {
        final Optional<Authentication> authOpt = userAuthentication();
        return authOpt.map(Principal::getName);
    }

    public static List<String> userRoles() {
        return userAuthentication().map(auth -> auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()))
                .orElse(Collections.emptyList()
        );
    }

    public static boolean isAdmin() {
        return userRoles().stream().anyMatch(OivaAccess.Role_Yllapitaja::equals);
    }

    /**
     * Return authenticated user's organisation oid.
     *
     * @return user's organisation oid or empty string
     */
    public static String userOrganisationOid() {
        return userDetails().map(OivaUserDetails::getOrganisationOid)
                .orElse("");
    }
}
