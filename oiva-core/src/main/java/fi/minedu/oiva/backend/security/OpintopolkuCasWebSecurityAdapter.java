package fi.minedu.oiva.backend.security;

import fi.minedu.oiva.backend.web.controller.BaseAuthController;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;

public class OpintopolkuCasWebSecurityAdapter extends WebSecurityConfigurerAdapter {

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
            .logoutUrl(oivaApiPath + BaseAuthController.path + "/logout")
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