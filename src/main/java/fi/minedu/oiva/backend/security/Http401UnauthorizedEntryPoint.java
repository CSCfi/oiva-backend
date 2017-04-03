package fi.minedu.oiva.backend.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class Http401UnauthorizedEntryPoint implements AuthenticationEntryPoint {
    private static final Log logger = LogFactory.getLog(Http401UnauthorizedEntryPoint.class);

    @Autowired
    CasAuthenticationEntryPoint casAuthenticationEntryPoint;

    public Http401UnauthorizedEntryPoint() {
    }

    public static boolean isXHR(HttpServletRequest request) {
        String acceptHeader = request.getHeader("Accept");
        return acceptHeader != null && acceptHeader.startsWith("application/json");
    }

    /**
     * Returns http status 401 when unauthorized and request is for json, otherwise goes to CAS entry point.
     *
     * Note: Form is not a real auth scheme, but here we mock WWW-Authenticate header as 401 feels still more
     * appropriate than 403.
     *
     * For valid schemes:
     * @see <a href="http://www.iana.org/assignments/http-authschemes/http-authschemes.xhtml">schemes</a>
     */
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException excp)
        throws IOException, ServletException {
        if (logger.isDebugEnabled()) {
            logger.debug("Pre-authenticated entry point called. Rejecting access");
        }
        if (isXHR(request)) {
            response.setHeader("WWW-Authenticate", "Form realm=\"opintopolku.fi\"");
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
        }  else {
            casAuthenticationEntryPoint.commence(request, response, excp);
        }

    }

}
