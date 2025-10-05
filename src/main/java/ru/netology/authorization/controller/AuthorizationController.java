package ru.netology.authorization.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.authorization.annotation.Credentials;
import ru.netology.authorization.model.Authorities;
import ru.netology.authorization.model.User;
import ru.netology.authorization.service.AuthorizationService;

import java.util.List;

@RestController
public class AuthorizationController {

    private final AuthorizationService service;

    public AuthorizationController(AuthorizationService service) {
        this.service = service;
    }

    @GetMapping("/authorize")
    public List<Authorities> getAuthorities(@Credentials User user) {
        return service.getAuthorities(user);
    }
}