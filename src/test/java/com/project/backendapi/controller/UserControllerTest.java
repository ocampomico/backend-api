package com.project.backendapi.controller;

import com.project.backendapi.model.User;
import com.project.backendapi.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(
        value = UserApiController.class,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = UserApiController.class
                )})
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    UserService userService;

    private MockMvc mockMvc;

    User user;

    @Before
    public void init() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        user = new User();
        user.setFirstName("Mico");
        user.setLastName("Ocampo");
        user.setEmail("email@email.com");
    }

    @Test
    public void getUser() throws Exception {
        // Given a user route
        // When the GET endpoint gets called
        // Then I expect to receive an OK status code
        // And the user details

        when(userService.getUser()).thenReturn(user);
        MvcResult result = mockMvc.perform(get("/v1/user")
                        .with(oidcLogin()))
                .andExpect(status().isOk())
                .andReturn();


        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains(user.getFirstName()));
        assertTrue(content.contains(user.getLastName()));
        assertTrue(content.contains(user.getEmail()));
    }

    @Test
    public void getUserBadRequest() throws Exception {
        // Given a user route
        // When the GET endpoint gets called
        // And the user does not exist
        // Then I expect to receive a BAD REQUEST status

        when(userService.getUser()).thenReturn(null);
        mockMvc.perform(get("/v1/user")
                        .with(oidcLogin()))
                .andExpect(status().isNotFound());
    }


    @Test
    public void getUserRedirect() throws Exception {
        // Given a user route
        // When the GET endpoint gets called
        // And the user is not logged in
        // Then I expect a REDIRECT status

        when(userService.getUser()).thenReturn(user);
        mockMvc.perform(get("/v1/user"))
                .andExpect(status().is3xxRedirection());
    }
}
