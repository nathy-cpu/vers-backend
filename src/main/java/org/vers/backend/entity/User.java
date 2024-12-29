package org.vers.backend.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

enum Role {
    Admin,
    Registrar,
    Citizen,
}

@Entity
public class User extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String firstName;
    public String middleName;
    public String lastName;
    public String password; // hashed
    public Role role;
    public LocalDateTime createdAt;

    public static User findByUsername(String username) {
        return find("username", username).firstResult();
    }
}
