package com.project.backendapi.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2ClientProperties oAuth2ClientProperties;

    @Autowired
    ClientRegistrationRepository clientRegistrationRepository;

    OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        successHandler.setPostLogoutRedirectUri("/");
        return successHandler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        AuthenticationManagerWrapper authenticationManager = new AuthenticationManagerWrapper(oAuth2ClientProperties);

        http.authorizeRequests()
                .antMatchers("/actuator/healthz").permitAll()
                .anyRequest().authenticated().and()
                .logout().logoutSuccessHandler(oidcLogoutSuccessHandler()).and()
                .cors().and().csrf()
                .disable().headers().frameOptions().sameOrigin().and()
                .oauth2ResourceServer().authenticationManagerResolver(authenticationManager.customAuthenticationManager());
    }
}
