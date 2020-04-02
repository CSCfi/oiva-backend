package fi.minedu.oiva.backend.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {

    private final AuthService authService;
    private final Logger logger = LoggerFactory.getLogger(LoggingService.class);

    @Autowired
    public LoggingService(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Log action prepending user name
     * @param message message to be logged using toString method
     */
    public final void logUserAction(Object message) {
        this.logAction(authService.getUsername(), message);
    }

    /**
     * Log action prepending user name
     * @param obj message to be logged using toString method
     */
    public final void logUserAction(String action, Object obj) {
        logger.info("{}, " + action + " {}", authService.getUsername(), obj);
    }

    /**
     * Log action prepending given username
     * @param obj message to be logged using toString method
     */
    public final void logAction(String username, Object obj) {
        logger.info("{}, {}", username, obj);
    }
}
