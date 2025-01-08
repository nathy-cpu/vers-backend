package org.vers.backend.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;
import org.vers.backend.enums.Role;
import org.vers.backend.utils.PasswordUtils;

@Entity
@Table(name = "\"User\"")
public class User extends PanacheEntityBase {

    @Id
    @Column(unique = true, nullable = false)
    @Size(
        min = 4,
        max = 64,
        message = "Username must be between 4 and 64 characters"
    )
    public String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    public String password; // Hashed password

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    public Role role; // Using a String for roles to match the database schema

    @Email(message = "Invalid email address")
    @Column(nullable = false, unique = true)
    public String email;

    @PastOrPresent
    @Column(nullable = false, updatable = false)
    public LocalDateTime createdAt;

    public User() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructors
    public User(@NotBlank(message = "Role is required") Role role) {
        this.createdAt = LocalDateTime.now();
        this.role = role;
    }

    public User(String username, Role role) {
        this.createdAt = LocalDateTime.now();
        this.username = username;
        this.role = role;
    }

    public User(
        String username,
        String plainPassword,
        String email,
        @NotBlank(message = "Role is required") Role role
    ) {
        this.username = username;
        this.setPassword(plainPassword);
        this.email = email;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return (
            "User{" +
            "username='" +
            username +
            '\'' +
            ", email='" +
            email +
            '\'' +
            ", role='" +
            role +
            '\'' +
            '}'
        );
    }

    public boolean verifyPassword(String plainPassword) {
        return PasswordUtils.verifyPassword(plainPassword, this.password);
    }

    public void setPassword(String plainPassword) {
        this.password = PasswordUtils.hashPassword(plainPassword);
    }

    public boolean hasRole(Role role) {
        return this.role == role;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return (Objects.equals(this.username, user.username));
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
