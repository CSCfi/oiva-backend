package fi.minedu.oiva.backend.core.config;

import fi.minedu.oiva.backend.core.service.LoggingService;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RestApiInterceptor extends HandlerInterceptorAdapter {

    private final LoggingService loggingService;

    public RestApiInterceptor(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (DispatcherType.REQUEST == request.getDispatcherType()) {
            loggingService.logUserAction(request.getMethod() + " " + request.getRequestURI());
        }
        return true;
    }
}
