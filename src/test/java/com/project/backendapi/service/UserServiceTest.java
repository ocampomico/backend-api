package com.project.backendapi.service;

import com.project.backendapi.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    Authentication authentication;

    @Mock
    SecurityContext securityContext;

    @InjectMocks
    UserServiceImpl userService;

    User user;

    @Before
    public void init() {
        user = new User();
        user.setFirstName("Mico");
        user.setLastName("Ocampo");
        user.setEmail("email@email.com");
    }

    @After
    public void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void getUser() {
        // Given a user with details
        // When getUser is called
        // And security context is set
        // Then it should return a user
        // And has the correct details

        OidcUser oidcUser = mock(OidcUser.class);
        when(authentication.getPrincipal()).thenReturn(oidcUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(oidcUser.getGivenName()).thenReturn(user.getFirstName());
        when(oidcUser.getFamilyName()).thenReturn(user.getLastName());
        when(oidcUser.getEmail()).thenReturn(user.getEmail());

        assertEquals(user, userService.getUser());
    }

    @Test(expected = NullPointerException.class)
    public void getUserNull() {
        // Given a user with details
        // When getUser is called
        // And security context is not set
        // Then a NullPointerException is thrown

        userService.getUser();
    }
}
