package com.project.backendapi.service;

import com.project.backendapi.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public User getUser() {
        User user = new User();
        Object principal =  SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) principal;
            user.setFirstName(oidcUser.getGivenName());
            user.lastName(oidcUser.getFamilyName());
            user.email(oidcUser.getEmail());
        }
        return user;
    }
}
