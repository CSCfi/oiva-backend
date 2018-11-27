package fi.minedu.oiva.backend.security;

import fi.minedu.oiva.backend.security.annotations.OivaAccess;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class SecurityUtil {

    protected static Optional<Authentication> userAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    public static Optional<String> userName() {
        final Optional<Authentication> authOpt = userAuthentication();
        return Optional.ofNullable(authOpt.isPresent() ? authOpt.get().getName() : null);
    }

    public static List<String> userRoles() {
        final List<String> userRoles = new ArrayList<>();
        userAuthentication().ifPresent(auth -> auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority).filter(Objects::nonNull).forEach(userRoles::add));
        return userRoles;
    }

    public static List<String> roleOids(final String ...roles) {
        return roleOids(Arrays.asList(roles));
    }

    public static List<String> roleOids(final List<String> roles) {
        final List<String> roleOids = new ArrayList<>();
        userAuthentication().ifPresent(auth -> roleOids(auth, roles).forEach(roleOids::add));
        return roleOids;
    }

    public static List<String> roleOids(final Authentication authentication, final List<String> roles) {
        return roles.stream().flatMap(role -> roleOids(authentication, role).stream()).distinct().collect(Collectors.toList());
    }

    /**
     * Return user role related oids
     *
     * @param authentication Authentication token
     * @param role target role
     * @return user role related oids
     */
    public static List<String> roleOids(final Authentication authentication, final String role) {
        final Function<Matcher, Boolean> roleMatcher = (matcher) -> StringUtils.equalsIgnoreCase(matcher.group(1), role) &&
            matcher.groupCount() == 2 && StringUtils.isNotBlank(matcher.group(2));
        return authentication.getAuthorities()
            .stream().map(auth -> OivaAccess.Format_Role.matcher(auth.getAuthority()))
            .filter(Matcher::matches)
            .filter(roleMatcher::apply)
            .map(matcher -> matcher.group(2))
            .collect(Collectors.toList());
    }
}
