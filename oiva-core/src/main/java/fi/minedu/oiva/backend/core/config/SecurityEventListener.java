package fi.minedu.oiva.backend.core.config;

import fi.minedu.oiva.backend.core.security.OivaUserDetails;
import fi.minedu.oiva.backend.core.service.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityEventListener {

    private final LoggingService loggingService;

    @Autowired
    public SecurityEventListener(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @EventListener
    public void onLoginSuccessEvent(AuthenticationSuccessEvent event) {
        UserDetails userDetails = ((CasAuthenticationToken) event.getAuthentication()).getUserDetails();
        loggingService.logAction(userDetails.getUsername(), "Logged in " + userDetails);
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
