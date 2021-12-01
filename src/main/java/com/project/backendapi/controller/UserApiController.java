package com.project.backendapi.controller;

import com.project.backendapi.model.User;
import com.project.backendapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("${openapi.project.base-path:/v1}")
public class UserApiController implements UserApi {

    @Autowired
    private UserService userService;

    @Override
    public ResponseEntity<User> userGet() {
        User user = userService.getUser();
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userService.getUser());
    }
}
