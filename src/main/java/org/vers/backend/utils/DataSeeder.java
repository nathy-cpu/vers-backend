package org.vers.backend.utils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import io.quarkus.runtime.StartupEvent;
import org.vers.backend.entity.User;
import org.vers.backend.enums.Role;
import org.vers.backend.service.UserService;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DataSeeder {

    @Inject
    UserService userService;

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        addUserIfNotExists("admin", "adminpass", "admin@example.com", Role.ADMIN);
        addUserIfNotExists("registrar", "registrarpass", "registrar@example.com", Role.REGISTRAR);
        addUserIfNotExists("official", "officialpass", "official@example.com", Role.OFFICIAL);
    }

    private void addUserIfNotExists(String username, String password, String email, Role role) {
        if (userService.findByUsername(username).isEmpty()) {
            User user = new User(username, password, email, role);
            userService.registerUser(user);
        }
    }
}
