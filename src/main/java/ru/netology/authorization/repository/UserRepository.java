package ru.netology.authorization.repository;

import ru.netology.authorization.model.Authorities;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserRepository {

    private final Map<String, Map<String, List<Authorities>>> users = new ConcurrentHashMap<>();

    public UserRepository() {

        Map<String, List<Authorities>> johnCredentials = new ConcurrentHashMap<>();
        johnCredentials.put("password", Arrays.asList(Authorities.READ, Authorities.WRITE));
        users.put("john", johnCredentials);

        Map<String, List<Authorities>> adminCredentials = new ConcurrentHashMap<>();
        adminCredentials.put("adminpass", Arrays.asList(Authorities.READ, Authorities.WRITE, Authorities.DELETE));
        users.put("admin", adminCredentials);

        Map<String, List<Authorities>> guestCredentials = new ConcurrentHashMap<>();
        guestCredentials.put("guestpass", Collections.singletonList(Authorities.READ));
        users.put("guest", guestCredentials);
    }

    public List<Authorities> getUserAuthorities(String user, String password) {
        if (users.containsKey(user)) {
            Map<String, List<Authorities>> userCredentials = users.get(user);
            if (userCredentials.containsKey(password)) {
                return userCredentials.get(password);
            }
        }
        return Collections.emptyList();
    }
}