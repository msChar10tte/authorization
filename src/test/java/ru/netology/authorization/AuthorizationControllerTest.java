package ru.netology.authorization;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.authorization.controller.AuthorizationController;
import ru.netology.authorization.model.Authorities;
import ru.netology.authorization.service.AuthorizationService;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorizationController.class)
public class AuthorizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorizationService authorizationService;

    private final String VALID_JOHN_PASSWORD = "password";

    @Test
    void testSuccessfulAuthorization() throws Exception {
        when(authorizationService.getAuthorities(new ru.netology.authorization.model.User("john", VALID_JOHN_PASSWORD)))
                .thenReturn(Arrays.asList(Authorities.READ, Authorities.WRITE));

        mockMvc.perform(get("/authorize")
                        .param("user", "john")
                        .param("password", VALID_JOHN_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"READ\",\"WRITE\"]"));
    }

    @Test
    void testUnauthorizedUser() throws Exception {
        when(authorizationService.getAuthorities(new ru.netology.authorization.model.User("unknown", VALID_JOHN_PASSWORD)))
                .thenThrow(new ru.netology.authorization.exception.UnauthorizedUser("Unknown user unknown"));

        mockMvc.perform(get("/authorize")
                        .param("user", "unknown")
                        .param("password", VALID_JOHN_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unknown user unknown"));
    }

    @Test
    void testWrongPassword() throws Exception {

        String wrongButValidLengthPassword = "wrongpassword";
        when(authorizationService.getAuthorities(new ru.netology.authorization.model.User("john", wrongButValidLengthPassword)))
                .thenThrow(new ru.netology.authorization.exception.UnauthorizedUser("Unknown user john"));

        mockMvc.perform(get("/authorize")
                        .param("user", "john")
                        .param("password", wrongButValidLengthPassword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Unknown user john"));
    }

    @Test
    void testInvalidUsernameLength() throws Exception {
        mockMvc.perform(get("/authorize")
                        .param("user", "jo")
                        .param("password", VALID_JOHN_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("user: Username must be between 3 and 20 characters"));
    }

    @Test
    void testEmptyPassword() throws Exception {
        mockMvc.perform(get("/authorize")
                        .param("user", "john")
                        .param("password", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())

                .andExpect(content().string("password: Password must be between 6 and 30 characters; password: Password cannot be empty"));
    }

    @Test
    void testTooLongUsername() throws Exception {
        mockMvc.perform(get("/authorize")
                        .param("user", "thisusernameiswaytoolongforthisapplication")
                        .param("password", VALID_JOHN_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("user: Username must be between 3 and 20 characters"));
    }

    @Test
    void testPasswordTooShort() throws Exception {
        mockMvc.perform(get("/authorize")
                        .param("user", "john")
                        .param("password", "short")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("password: Password must be between 6 and 30 characters"));
    }

    @Test
    void testPasswordTooLong() throws Exception {
        mockMvc.perform(get("/authorize")
                        .param("user", "john")
                        .param("password", "thispasswordiswaytoolongtobevalidforthisapplication")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("password: Password must be between 6 and 30 characters"));
    }

    @Test
    void testNullUsername() throws Exception {
        mockMvc.perform(get("/authorize")
                        .param("password", VALID_JOHN_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("user: Username cannot be empty"));
    }
}