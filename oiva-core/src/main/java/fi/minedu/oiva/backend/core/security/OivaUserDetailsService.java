package fi.minedu.oiva.backend.core.security;

import fi.minedu.oiva.backend.model.entity.opintopolku.KayttajaKayttooikeus;
import fi.minedu.oiva.backend.model.security.annotations.OivaAccess;
import fi.minedu.oiva.backend.core.service.OpintopolkuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by aheikkinen on 05/02/2018.
 */

@Component
public class OivaUserDetailsService implements UserDetailsService {

    private final static Logger logger = LoggerFactory.getLogger(OivaUserDetailsService.class);

    private final OpintopolkuService opintopolku;
    private final SessionRegistry sessionRegistry;

    private final String[] editorRoles = {OivaAccess.Role_Esittelija, OivaAccess.Role_Nimenkirjoittaja,
            OivaAccess.Role_Kayttaja};

    @Autowired
    public OivaUserDetailsService(OpintopolkuService opintopolku, SessionRegistry sessionRegistry) {
        this.opintopolku = opintopolku;
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

        logger.debug("Authenticating {}", username);

        final KayttajaKayttooikeus kayttooikeudet = opintopolku.getKayttajaKayttooikeus(username)
                .orElseThrow(() -> new UsernameNotFoundException("No such user: " + username));

        final String oid = kayttooikeudet.getOivaOrganisaatioOid().orElse(null);
        List<String> oikeudet = kayttooikeudet.getOivaOikeudet(oid)
                .orElseThrow(() -> new InsufficientAuthenticationException("No Oiva permissions for user: " + username));

        if (logger.isDebugEnabled()) {
            logger.debug("Authorities: {}", oikeudet);
        }

        final boolean permissionsDecreased = organisationHasOtherEditorLoggedIn(oid, username) &&
                Stream.of(editorRoles).anyMatch(oikeudet::contains);

        if (permissionsDecreased) {
            // Organisation has already user logged in with editing rights.
            oikeudet = Arrays.asList(OivaAccess.Role_Application, OivaAccess.Role_Katselija);
        }

        final Collection<GrantedAuthority> grantedAuthorities = oikeudet.stream()
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return new OivaUserDetails(username, "", grantedAuthorities, oid, permissionsDecreased);
    }

    private boolean organisationHasOtherEditorLoggedIn(String oid, final String username) {
        final List<String> roleList = Arrays.asList(editorRoles);
        return sessionRegistry.getAllPrincipals().stream()
                .filter(o -> o instanceof OivaUserDetails)
                .map(o -> (OivaUserDetails) o)
                .filter(u -> u.getOrganisationOid().equals(oid))
                .filter(u -> !u.getUsername().equals(username))
                .flatMap(u -> u.getAuthorities().stream())
                .map(GrantedAuthority::getAuthority)
                .anyMatch(roleList::contains);
    }
}