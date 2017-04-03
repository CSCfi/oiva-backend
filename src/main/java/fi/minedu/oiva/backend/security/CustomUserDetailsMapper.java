package fi.minedu.oiva.backend.security;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

import java.util.Collection;

/**
 *
 * Classes for Opintopolku CAS integration
 *
 * Copied from:
 * https://github.com/Opetushallitus/generic/blob/master/generic-common/src/main/java/fi/vm/sade/security/CustomUserDetailsMapper.java
 */
public class CustomUserDetailsMapper extends LdapUserDetailsMapper {

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        String oid = ctx.getStringAttribute("employeeNumber");
        if (oid == null) {
            oid = ctx.getStringAttribute("uid");
        }
        String lang = ctx.getStringAttribute("preferredLanguage");

        UserDetails userDetails = super.mapUserFromContext(ctx, oid, authorities);

        /* do we need these? */
        String origUsername = ctx.getStringAttribute("uid");
        String firstname = ctx.getStringAttribute("cn");
        String lastname = ctx.getStringAttribute("sn");

        UserDetailsWrapper wrapper = new UserDetailsWrapper(userDetails, lang, origUsername, firstname, lastname);

        return wrapper;
    }

    /**
     * Copied from:
     * https://github.com/Opetushallitus/generic/blob/master/generic-common/src/main/java/fi/vm/sade/security/SadeUserDetailsWrapper.java
     */
    public static class UserDetailsWrapper implements UserDetails {

        private UserDetails details;
        private String lang;

        private String origUsername;
        private String firstname;
        private String lastname;

        @Override
        public String toString() {
            return getUsername();
        }

        public UserDetailsWrapper(UserDetails details) {
            this.details = details;
        }

        public UserDetailsWrapper(UserDetails details, String lang, String origUsername, String firstname, String lastname) {
            this.details = details;
            this.lang = lang;
            this.origUsername = origUsername;
            this.firstname = firstname;
            this.lastname = lastname;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return details.getAuthorities();
        }

        @Override
        public String getPassword() {
            return details.getPassword();
        }

        @Override
        public String getUsername() {
            return details.getUsername();
        }

        @Override
        public boolean isAccountNonExpired() {
            return details.isAccountNonExpired();
        }

        @Override
        public boolean isAccountNonLocked() {
            return details.isAccountNonLocked();
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return details.isCredentialsNonExpired();
        }

        @Override
        public boolean isEnabled() {
            return details.isEnabled();
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public UserDetails getDetails() {
            return details;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public String getOrigUsername() {
            return origUsername;
        }

        public void setOrigUsername(String origUsername) {
            this.origUsername = origUsername;
        }
    }
}
