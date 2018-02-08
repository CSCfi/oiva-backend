package fi.minedu.oiva.backend.security;

import fi.minedu.oiva.backend.entity.opintopolku.KayttajaKayttooikeus;
import fi.minedu.oiva.backend.security.annotations.OivaAccess;
import fi.minedu.oiva.backend.service.OpintopolkuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by aheikkinen on 05/02/2018.
 */

@Component
public class OivaUserDetailsService implements UserDetailsService {

    private final static Logger logger = LoggerFactory.getLogger(OivaUserDetailsService.class);

    @Autowired
    private OpintopolkuService opintopolku;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

        logger.debug("Authenticating {}", username);

        final KayttajaKayttooikeus kayttooikeudet = opintopolku.getKayttajaKayttooikeus(username)
            .orElseThrow(() -> new UsernameNotFoundException("No such user: " + username));

        final List<String> oikeudet = kayttooikeudet.getOivaOikeudet()
            .orElseThrow(() -> new InsufficientAuthenticationException("No Oiva permissions for user: " + username));

        if(logger.isDebugEnabled()) {
            logger.debug("Authorities: {}", oikeudet);
        }

        final Collection<GrantedAuthority> grantedAuthorities = oikeudet.stream()
            .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return new User(username, "", grantedAuthorities);
    }
}