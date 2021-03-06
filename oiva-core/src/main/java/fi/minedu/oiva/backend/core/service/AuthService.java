package fi.minedu.oiva.backend.core.service;

import fi.minedu.oiva.backend.core.security.OivaPermission;
import fi.minedu.oiva.backend.core.security.OivaUserDetails;
import fi.minedu.oiva.backend.core.security.SecurityUtil;
import fi.minedu.oiva.backend.model.security.annotations.OivaAccess;
import fi.minedu.oiva.backend.model.security.annotations.OivaAccess.Type;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static fi.minedu.oiva.backend.model.util.CollectionUtils.mapOf;
import static org.jooq.lambda.tuple.Tuple.tuple;

@Service
public class AuthService {

    public Map<String, Object> getMe() {
        final Optional<OivaUserDetails> userDetails = SecurityUtil.userDetails();
        return mapOf(
                tuple("username", SecurityUtil.userName().orElse(null)),
                tuple("roles", SecurityUtil.userRoles()),
                tuple("oid", userDetails.map(OivaUserDetails::getOrganisationOid).orElse(null)),
                tuple("permissionDecreased", userDetails.map(OivaUserDetails::isPermissionsDecreased).orElse(false))
        );
    }

    /**
     * Get current username.
     *
     * @return Username or empty string if not logged in.
     */
    public String getUsername() {
        return SecurityUtil.userName().orElse("");
    }

    public OivaPermission accessPermission() {
        if (hasAnyRole(OivaAccess.Role_Esittelija)) {
            return new OivaPermission(Type.All);
        } else if (hasAnyRole(OivaAccess.Role_Nimenkirjoittaja, OivaAccess.Role_Kayttaja, OivaAccess.Role_Katselija)) {
            return getOrganizationBasedAccess();
        }
        return new OivaPermission(Type.OnlyPublic);
    }

    public boolean hasAnyRole(final String... roles) {
        return SecurityUtil.userRoles().stream().anyMatch(s -> Arrays.asList(roles).contains(s));
    }

    private OivaPermission getOrganizationBasedAccess() {
        final OivaPermission access = new OivaPermission(Type.OrganizationAndPublic);
        access.oids.add(SecurityUtil.userOrganisationOid());
        return access;
    }

    public String getUserOrganisationOid() {
        return SecurityUtil.userOrganisationOid();
    }
}
