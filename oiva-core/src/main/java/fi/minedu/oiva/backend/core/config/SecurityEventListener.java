package fi.minedu.oiva.backend.core.config;

import fi.minedu.oiva.backend.core.security.OivaUserDetails;
import fi.minedu.oiva.backend.core.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class SecurityEventListener {

    private final LoggingService loggingService;

    @Autowired
    public SecurityEventListener(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @EventListener
    public void onLoginSuccessEvent(AuthenticationSuccessEvent event) {
        if (event.getAuthentication() instanceof CasAuthenticationToken) {
            UserDetails userDetails = ((CasAuthenticationToken) event.getAuthentication()).getUserDetails();
            loggingService.logAction(userDetails.getUsername(), "Logged in " + userDetails);
        }
        else {
            loggingService.logAction(event.getAuthentication().getName(), "Logged in using basic auth: " + event.getAuthentication());
        }
    }

    @EventListener
    public void onSessionDestroyedEvent(SessionDestroyedEvent event) {
        String username = event.getSecurityContexts().stream()
                .findFirst()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof OivaUserDetails)
                .map(OivaUserDetails.class::cast)
                .map(OivaUserDetails::getUsername)
                .orElse("");
        loggingService.logAction(username, "Logged out");
    }

}
