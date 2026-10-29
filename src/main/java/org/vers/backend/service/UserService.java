package org.vers.backend.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import java.util.regex.Pattern;
import org.vers.backend.entity.User;
import org.vers.backend.enums.Role;
import org.vers.backend.exception.ApiException;
import org.vers.backend.repository.EventRepository;
import org.vers.backend.repository.UserRepository;
import org.vers.backend.utils.PasswordUtils;

@ApplicationScoped
public class UserService {

    public static final int MIN_PASSWORD_LENGTH = 8;

    private static final Pattern EMAIL = Pattern.compile(
        "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"
    );

    @Inject
    UserRepository userRepository;

    @Inject
    EventRepository eventRepository;

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

    /** Validates the input and creates the user; fails with 400 or 409. */
    public User createUser(
        String username,
        String password,
        String email,
        String role
    ) {
        if (username == null || username.trim().length() < 4 || username.trim().length() > 64) {
            throw ApiException.badRequest("Username must be between 4 and 64 characters.");
        }
        checkPasswordStrength(password);
        if (email == null || !EMAIL.matcher(email.trim()).matches()) {
            throw ApiException.badRequest("Invalid email address.");
        }
        Role parsedRole = EventService.parseEnum(Role.class, role, "role");

        username = username.trim();
        email = email.trim();
        if (userRepository.findByUsername(username).isPresent()) {
            throw ApiException.conflict("User already exists.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw ApiException.conflict("A user with this email already exists.");
        }
        return registerUser(new User(username, password, email, parsedRole));
    }

    public static void checkPasswordStrength(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw ApiException.badRequest(
                "Password must be at least " + MIN_PASSWORD_LENGTH + " characters."
            );
        }
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Users who registered or reviewed events are kept, since vital records
     * must keep pointing at the people responsible for them.
     */
    public void removeUser(String username, User requestedBy) {
        User user = userRepository
            .findByUsername(username)
            .orElseThrow(() -> ApiException.notFound("User not found."));
        if (user.equals(requestedBy)) {
            throw ApiException.badRequest("You cannot remove your own account.");
        }
        if (eventRepository.isReferencedBy(user)) {
            throw ApiException.conflict(
                "This user has registered or reviewed events and cannot be removed."
            );
        }
        user.delete();
    }

    public boolean deleteUser(String username) {
        return userRepository.deleteById(username);
    }
}
