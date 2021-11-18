package com.project.backendapi.config.auth;

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtBearerTokenAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class AuthenticationManagerWrapper {

    private final OAuth2ClientProperties oAuth2ClientProperties;

    AuthenticationManagerWrapper(OAuth2ClientProperties oAuth2ClientProperties) {
        this.oAuth2ClientProperties = oAuth2ClientProperties;
    }

    AuthenticationManagerResolver<HttpServletRequest> customAuthenticationManager() {
        LinkedHashMap<RequestMatcher, AuthenticationManager> authenticationManagers = new LinkedHashMap<>();

        // USE JWT tokens (locally validated) to validate HEAD, GET, and OPTIONS requests
        List<String> readMethod = Arrays.asList("HEAD", "GET", "OPTIONS");
        RequestMatcher readMethodRequestMatcher = request -> readMethod.contains(request.getMethod());
        authenticationManagers.put(readMethodRequestMatcher, jwt());

        // all other requests will use opaque tokens (remotely validated)
        RequestMatchingAuthenticationManagerResolver authenticationManagerResolver
                = new RequestMatchingAuthenticationManagerResolver(authenticationManagers);

        // Use opaque tokens (remotely validated) for all other requests
        authenticationManagerResolver.setDefaultAuthenticationManager(opaque());
        return authenticationManagerResolver;
    }

    private AuthenticationManager jwt() {
        String issuer = oAuth2ClientProperties.getProvider().get("okta").getIssuerUri();
        String jwkSetUri = issuer + "/v1/keys";

        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        // okta recommends validating the `iss` and `aud` claims
        // see: https://developer.okta.com/docs/guides/validate-access-tokens/java/overview/
        List<OAuth2TokenValidator<Jwt>> validators = new ArrayList<>();
        validators.add(new JwtTimestampValidator());
        validators.add(new JwtIssuerValidator(issuer));
        validators.add(token -> {
            Set<String> expectedAudience = new HashSet<>();
            expectedAudience.add("api://default"); // this is the default value, update this accordingly
            return !Collections.disjoint(token.getAudience(), expectedAudience)
                    ? OAuth2TokenValidatorResult.success()
                    : OAuth2TokenValidatorResult.failure(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST,
                    "This aud claim is not equal to the configured audience",
                    "https://tools.ietf.org/html/rfc6750#section-3.1"));
        });
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(validators);
        jwtDecoder.setJwtValidator(validator);

        JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
        authenticationProvider.setJwtAuthenticationConverter(new JwtBearerTokenAuthenticationConverter());
        return authenticationProvider::authenticate;
    }

    private AuthenticationManager opaque() {
        String issuer = oAuth2ClientProperties.getProvider().get("okta").getIssuerUri();
        String introspectionUri = issuer + "/v1/introspect";

        // The default opaque token logic
        OAuth2ClientProperties.Registration oktaRegistration = oAuth2ClientProperties.getRegistration().get("okta");
        OpaqueTokenIntrospector introspectionClient = new NimbusOpaqueTokenIntrospector(
                introspectionUri,
                oktaRegistration.getClientId(),
                oktaRegistration.getClientSecret());
        return new OpaqueTokenAuthenticationProvider(introspectionClient)::authenticate;
    }
}
