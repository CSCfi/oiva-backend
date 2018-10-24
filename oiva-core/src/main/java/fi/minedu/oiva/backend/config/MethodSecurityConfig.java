package fi.minedu.oiva.backend.config;

import fi.minedu.oiva.backend.security.CustomSecurityExpressionHandler;
import fi.minedu.oiva.backend.security.OivaPermissionChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private OivaPermissionChecker permissionEvaluator;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        final CustomSecurityExpressionHandler expressionHandler = new CustomSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        expressionHandler.setApplicationContext(context);
        return expressionHandler;
    }

}
