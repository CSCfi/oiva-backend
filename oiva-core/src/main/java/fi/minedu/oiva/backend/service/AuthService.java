package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.security.OivaPermission;
import fi.minedu.oiva.backend.security.SecurityUtil;
import fi.minedu.oiva.backend.security.annotations.OivaAccess;
import fi.minedu.oiva.backend.security.annotations.OivaAccess.Type;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import static fi.minedu.oiva.backend.util.CollectionUtils.mapOf;
import static org.jooq.lambda.tuple.Tuple.tuple;

@Service
public class AuthService {

    public Map<String, Object> getMe() {
        return mapOf(
            tuple("username", SecurityUtil.userName().orElse(null)),
            tuple("roles", SecurityUtil.userRoles())
        );
    }

    public OivaPermission lupaAccessPermission() {
        final Function<String, OivaPermission> organizationBasedAccess = role -> {
            final OivaPermission access = new OivaPermission(Type.OrganizationAndPublic);
            SecurityUtil.roleOids(role).stream().forEach(access.oids::add);
            return access;
        };

        if(hasAnyRole(OivaAccess.Role_Esittelija)) {
            return new OivaPermission(Type.All);
        } else if(hasAnyRole(OivaAccess.Role_Nimenkirjoittaja)) {
            return organizationBasedAccess.apply(OivaAccess.Role_Nimenkirjoittaja);
        } else if(hasAnyRole(OivaAccess.Role_Kayttaja)) {
            return organizationBasedAccess.apply(OivaAccess.Role_Kayttaja);
        } else if(hasAnyRole(OivaAccess.Role_Katselija)) {
            return organizationBasedAccess.apply(OivaAccess.Role_Katselija);
        } else {
            return new OivaPermission(Type.OnlyPublic);
        }
    }

    public boolean hasAnyRole(final String ...roles) {
        return SecurityUtil.userRoles().stream().anyMatch(s -> Arrays.asList(roles).contains(s));
    }
}
