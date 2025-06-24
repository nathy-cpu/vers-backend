package org.vers.backend.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import org.vers.backend.entity.User;
import org.vers.backend.repository.UserRepository;
import org.vers.backend.utils.PasswordUtils;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    public Optional<User> authenticate(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (
            user.isPresent() &&
            PasswordUtils.verifyPassword(password, user.get().password)
        ) {
            return user;
        }
        return Optional.empty();
    }

    public User registerUser(User user) {
        // Only hash if not already hashed (bcrypt hashes start with $2)
        if (user.password == null || !user.password.startsWith("$2")) {
            user.setPassword(user.password);
        }
        userRepository.persist(user);
        return user;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void deleteUser(String userId) {
        userRepository.delete(userId);
    }
}
