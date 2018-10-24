package fi.minedu.oiva.backend.security;

import fi.minedu.oiva.backend.security.annotations.OivaAccess;
import fi.minedu.oiva.backend.web.controller.BaseExportController;
import fi.minedu.oiva.backend.web.controller.BaseImportController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

public class BasicAuthWebSecurityAdapter extends WebSecurityConfigurerAdapter {

    @Value("${api.url.prefix}" + BaseExportController.path + "/**")
    private String oivaExportPath;

    @Value("${api.url.prefix}" + BaseImportController.path + "/**")
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
        http.httpBasic().and().requestMatchers().antMatchers (oivaExportPath, oivaImportPath).and()
            .authorizeRequests().anyRequest().hasRole(OivaAccess.Role_Application).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}