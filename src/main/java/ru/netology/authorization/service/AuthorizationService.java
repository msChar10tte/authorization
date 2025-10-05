package ru.netology.authorization.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.netology.authorization.exception.UnauthorizedUser;
import ru.netology.authorization.model.Authorities;
import ru.netology.authorization.model.User;
import ru.netology.authorization.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorizationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class); // Создаем логгер

    private final UserRepository userRepository;

    public AuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Authorities> getAuthorities(User user) {
        logger.info("Attempting to get authorities for user: {}", user.getUser()); // Логируем попытку
        List<Authorities> userAuthorities = userRepository.getUserAuthorities(user.getUser(), user.getPassword());

        if (isEmpty(userAuthorities)) {
            logger.warn("Unauthorized access attempt for user: {}", user.getUser()); // Логируем предупреждение
            throw new UnauthorizedUser("Unknown user " + user.getUser());
        }
        logger.info("Successfully granted authorities for user: {}", user.getUser()); // Логируем успех
        return userAuthorities;
    }

    private boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }
}