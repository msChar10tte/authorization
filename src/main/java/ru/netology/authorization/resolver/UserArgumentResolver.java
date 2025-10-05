package ru.netology.authorization.resolver;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import ru.netology.authorization.annotation.Credentials;
import ru.netology.authorization.exception.InvalidCredentials;
import ru.netology.authorization.model.User;

import java.util.Set;
import java.util.stream.Collectors;

public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private final Validator validator;

    public UserArgumentResolver(Validator validator) {
        this.validator = validator;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Credentials.class) && parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String username = webRequest.getParameter("user");
        String password = webRequest.getParameter("password");

        User user = new User(username, password);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                    .map(violation -> violation.getPropertyPath().toString() + ": " + violation.getMessage())
                    .collect(Collectors.joining("; "));

            throw new InvalidCredentials(errorMessage);
        }

        return user;
    }
}