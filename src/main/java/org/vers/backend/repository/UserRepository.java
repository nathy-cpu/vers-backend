package org.vers.backend.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import org.vers.backend.entity.User;
import org.vers.backend.enums.Role;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<User, String> {

    public Optional<User> findByUsername(String username) {
        return find("username", username).firstResultOptional();
    }

    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public List<User> findByRole(Role role) {
        return list("role", role);
    }

    public List<User> searchByUsername(String term) {
        return list(
            "lower(username) like ?1 order by username",
            "%" + term.toLowerCase() + "%"
        );
    }
}
