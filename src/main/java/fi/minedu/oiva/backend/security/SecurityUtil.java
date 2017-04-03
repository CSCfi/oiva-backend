package fi.minedu.oiva.backend.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SecurityUtil {

    public static final Pattern ROLE_FORMAT = Pattern.compile("^([A-Za-z_]+)(?:_((?:\\d+\\.?)+)?)?$");

    public static List<String> roleOIDs(Authentication auth, String role) {
        return auth.getAuthorities()
            .stream().map(a -> ROLE_FORMAT.matcher(a.getAuthority()))
            .filter(Matcher::matches)
            .filter(m -> m.group(1).equalsIgnoreCase(role)
                && m.groupCount() == 2 && StringUtils.isNotEmpty(m.group(2)))
            .map(m -> m.group(2))
            .collect(Collectors.toList());
    }

    public static List<String> roleOIDs(Authentication auth, List<String> roles) {
        return roles.stream()
            .flatMap(r -> roleOIDs(auth, r).stream())
            .distinct()
            .collect(Collectors.toList());
    }

    public static List<String> roleOIDs(String ...roles) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return SecurityUtil.roleOIDs(auth, Arrays.asList(roles));
    }

    public static List<String> roleOIDs(List<String> roles) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return SecurityUtil.roleOIDs(auth, roles);
    }

    public static String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

       return (principal != null && principal instanceof UserDetails) ?
            ((CustomUserDetailsMapper.UserDetailsWrapper)principal).getOrigUsername() : "anonymous";
    }

    public static boolean hasAnyAuthority(String ...authorities) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(a -> a != null)
            .anyMatch(s -> Arrays.asList(authorities).contains(s));
    }
}
