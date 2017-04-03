package fi.minedu.oiva.backend.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class CustomSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        HttpSession ses = request.getSession();
        String redirect = (String) ses.getAttribute("redirect");
        if (redirect != null) {
            this.setDefaultTargetUrl((String) ses.getAttribute("redirect"));
            ses.removeAttribute("redirect");
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
