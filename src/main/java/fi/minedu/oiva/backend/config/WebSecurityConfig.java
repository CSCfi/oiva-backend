package fi.minedu.oiva.backend.config;

import fi.minedu.oiva.backend.security.CustomSuccessHandler;
import fi.minedu.oiva.backend.security.Http401UnauthorizedEntryPoint;
import fi.minedu.oiva.backend.security.annotations.OivaAccess;
import fi.minedu.oiva.backend.web.controller.AuthController;
import fi.minedu.oiva.backend.web.controller.ExportController;
import fi.minedu.oiva.backend.web.controller.ImportController;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;

@EnableWebSecurity
public class WebSecurityConfig {

    @Configuration
    @Order(1)
    public static class BasicAuthWebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Value("${api.url.prefix}" + ExportController.path + "/**")
        private String oivaExportPath;

        @Value("${api.url.prefix}" + ImportController.path + "/**")
        private String oivaImportPath;

        @Value("${api.basicauth.username}")
        private String oivaBasicAuthUsername;

        @Value("${api.basicauth.password}")
        private String oivaBasicAuthPassword;

        @Override
        public void configure(final AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                .withUser(oivaBasicAuthUsername)
                .password(oivaBasicAuthPassword)
                .roles(OivaAccess.Role_Application);
        }

        @Override
        protected void configure(final HttpSecurity http) throws Exception {
            http.csrf().disable();
            http.httpBasic().and().requestMatchers().antMatchers(oivaExportPath, oivaImportPath).and()
                .authorizeRequests().anyRequest().hasRole(OivaAccess.Role_Application).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }
    }

    @Configuration
    @Order(2)
    public static class CasWebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Value("${cas.baseUrl}${cas.url.prefix}")
        private String casUrlPrefix;

        @Value("${oiva.baseUrl}")
        private String oivaBaseUrl;

        @Value("${oiva.baseUrl}${cas.service.url}")
        private String casServiceUrl;

        @Value("${cas.url.login}")
        private String casUrlLogin;

        @Value("${cas.url.logout}")
        private String casUrlLogout;

        @Value("${api.url.prefix}")
        private String oivaApiPath;

        @Autowired
        private Http401UnauthorizedEntryPoint http401UnauthorizedEntryPoint;

        @Autowired
        private UserDetailsService userDetailsService;

        protected void configure(final HttpSecurity http) throws Exception {
            http.csrf().disable();
            http.exceptionHandling()
                .authenticationEntryPoint(http401UnauthorizedEntryPoint)
                .and()
                .addFilter(casAuthenticationFilter())
                .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class);

            http.headers().frameOptions().disable();

            http.logout()
                .logoutUrl(oivaApiPath + AuthController.path + "/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "SESSION")
                .logoutSuccessUrl(casUrlPrefix + casUrlLogout + "?service=" + oivaBaseUrl);

            SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        }

        @Override
        public void configure(final WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/swagger/**");
        }

        @Bean
        public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
            final CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
            casAuthenticationFilter.setAuthenticationManager(authenticationManager());
            casAuthenticationFilter.setAuthenticationSuccessHandler(new CustomSuccessHandler());
            return casAuthenticationFilter;
        }

        @Bean
        public SingleSignOutFilter singleSignOutFilter() {
            final SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
            singleSignOutFilter.setCasServerUrlPrefix(casUrlPrefix);
            return singleSignOutFilter;
        }

        @Bean
        @SuppressWarnings("unchecked")
        public CasAuthenticationProvider casAuthenticationProvider() {
            final CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
            casAuthenticationProvider.setAuthenticationUserDetailsService(new UserDetailsByNameServiceWrapper(userDetailsService));
            casAuthenticationProvider.setServiceProperties(casServiceProperties());
            casAuthenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
            casAuthenticationProvider.setKey("opintopolku");
            return casAuthenticationProvider;
        }

        @Bean
        public ServiceProperties casServiceProperties() {
            final ServiceProperties serviceProperties = new ServiceProperties();
            serviceProperties.setService(casServiceUrl);
            serviceProperties.setSendRenew(false);
            return serviceProperties;
        }

        @Bean
        public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
            final CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
            casAuthenticationEntryPoint.setLoginUrl(casUrlPrefix + casUrlLogin);
            casAuthenticationEntryPoint.setServiceProperties(casServiceProperties());
            return casAuthenticationEntryPoint;
        }

        @Bean
        public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
            return new Cas20ServiceTicketValidator(casUrlPrefix);
        }

        @Autowired
        public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(casAuthenticationProvider());
        }
    }
}