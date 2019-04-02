package fi.minedu.oiva.backend.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class OivaUserDetails extends User {

    private final String organisationOid;
    private final boolean permissionsDecreased;

    public OivaUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
                           String organisationOid, boolean permissionDecreased) {
        super(username, password, authorities);
        this.organisationOid = organisationOid;
        this.permissionsDecreased = permissionDecreased;
    }

    public String getOrganisationOid() {
        return organisationOid;
    }

    public boolean isPermissionsDecreased() {
        return permissionsDecreased;
    }
}
