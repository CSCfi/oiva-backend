package fi.minedu.oiva.backend.core.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class CustomSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public static final String REDIRECT_ATTRIBUTE = "redirect";

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws ServletException, IOException {
        final HttpSession session = request.getSession();
        final Object redirect = session.getAttribute(REDIRECT_ATTRIBUTE);
        if (redirect instanceof String && StringUtils.isNotBlank((String) redirect)) {
            this.setDefaultTargetUrl((String) redirect);
            this.setAlwaysUseDefaultTargetUrl(true);
            session.removeAttribute(REDIRECT_ATTRIBUTE);
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
