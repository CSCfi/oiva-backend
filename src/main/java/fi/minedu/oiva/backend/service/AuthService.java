package fi.minedu.oiva.backend.service;

import fi.minedu.oiva.backend.security.OivaPermission;
import fi.minedu.oiva.backend.security.SecurityUtil;
import fi.minedu.oiva.backend.security.annotations.OivaAccess;
import fi.minedu.oiva.backend.security.annotations.OivaAccess.Type;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Application;
import fi.minedu.oiva.backend.security.annotations.OivaAccess_Public;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

import static fi.minedu.oiva.backend.util.CollectionUtils.mapOf;
import static org.jooq.lambda.tuple.Tuple.tuple;

@Service
public class AuthService {

    @OivaAccess_Application
    public Map<String, Object> getMe() {
        return mapOf(
            tuple("oid", SecurityUtil.userName()),
            tuple("roles", SecurityUtil.userRoles())
        );
    }

    @OivaAccess_Public
    public OivaPermission lupaAccessPermission() {
        if(hasAnyRole(OivaAccess.Role_Esittelija)) {
            return new OivaPermission(Type.All);

        } else if(hasAnyRole(OivaAccess.Role_Kayttaja)) {
            final OivaPermission access = new OivaPermission(Type.OrganizationAndPublic);
            SecurityUtil.roleOids(OivaAccess.Role_Kayttaja).stream().forEach(access.oids::add);
            return access;

        } else {
            return new OivaPermission(Type.Public);
        }
    }

    @OivaAccess_Public
    public boolean hasAnyRole(final String ...roles) {
        return SecurityUtil.userRoles().stream().anyMatch(s -> Arrays.asList(roles).contains(s));
    }
}
