package fi.minedu.oiva.backend.core.config;

import fi.minedu.oiva.backend.core.security.BasicAuthWebSecurityAdapter;
import fi.minedu.oiva.backend.core.security.OpintopolkuCasWebSecurityAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
public class WebSecurityConfig {

    @Configuration
    @Order(1)
    public static class BasicAuthWebSecurityConfig extends BasicAuthWebSecurityAdapter {}

    @Configuration
    @Order(2)
    public static class CasWebSecurityConfig extends OpintopolkuCasWebSecurityAdapter {}
}