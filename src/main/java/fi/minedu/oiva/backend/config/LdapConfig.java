package fi.minedu.oiva.backend.config;

import fi.minedu.oiva.backend.security.CustomUserDetailsMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapUserDetailsService;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

@Configuration
public class LdapConfig {
    @Value("${ldap.url}")
    private String ldapUrl;

    @Value("${ldap.base-prefix}")
    private String ldapBasePrefix;

    @Value("${ldap.manager.dn}")
    private String ldapManagerDN;

    @Value("${ldap.manager.password}")
    private String ldapManagerPassword;

    @Value("${ldap.user-search-base}")
    private String ldapUserSearchBase;

    @Value("${ldap.cas.user-search-filter}")
    private String ldapUserSearchFilter;

    @Value("${ldap.cas.group-search-base}")
    private String ldapGroupSearchBase;

    @Value("${ldap.cas.group-search-filter}")
    private String ldapGroupSearchFilter;

    @Value("${ldap.cas.group-role-attribute}")
    private String ldapGroupRoleAttribute;

    @Bean
    //@ConfigurationProperties(prefix="ldap.contextSource")
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();

        // TODO: replace with @ConfigurationProperties (?)
        contextSource.setUrl(ldapUrl);
        contextSource.setBase(ldapBasePrefix);
        contextSource.setUserDn(ldapManagerDN);
        contextSource.setPassword(ldapManagerPassword);

        return contextSource;
    }

    @Bean
    public LdapUserSearch ldapUserSearch(LdapContextSource contextSource) {
        return new FilterBasedLdapUserSearch(ldapUserSearchBase, ldapUserSearchFilter, contextSource);
    }

    @Bean
    public LdapAuthoritiesPopulator ldapAuthoritiesPopulator(LdapContextSource contextSource) {
        DefaultLdapAuthoritiesPopulator populator;
        populator = new DefaultLdapAuthoritiesPopulator(contextSource, ldapGroupSearchBase);

        populator.setGroupSearchFilter(ldapGroupSearchFilter);
        populator.setGroupRoleAttribute(ldapGroupRoleAttribute);
        populator.setRolePrefix("");

        return populator;
    }

    @Bean
    public UserDetailsContextMapper contextMapper() {
        return new CustomUserDetailsMapper();
    }

    @Bean
    public UserDetailsService userDetailsService(LdapUserSearch userSearch,
                                                 LdapAuthoritiesPopulator ldapAuthoritiesPopulator,
                                                 UserDetailsContextMapper contextMapper) {

        LdapUserDetailsService service = new LdapUserDetailsService(userSearch, ldapAuthoritiesPopulator);
        service.setUserDetailsMapper(contextMapper);
        return service;
    }

    @Bean
    public LdapTemplate ldapTemplate(LdapContextSource contextSource) {
        return new LdapTemplate(contextSource);
    }
}
