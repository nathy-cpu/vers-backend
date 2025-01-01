package org.vers.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;
import org.vers.backend.utils.PasswordUtils;

@Entity
@Table(name = "User")
public class User {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id; // Links to Person's national_id

    @NotBlank(message = "Username is required")
    @Size(
        min = 4,
        max = 20,
        message = "Username must be between 4 and 20 characters"
    )
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Column(name = "password", nullable = false)
    private String password; // Hashed password

    @NotBlank(message = "Role is required")
    @Column(name = "role", nullable = false)
    private String role; // Using a String for roles to match the database schema

    @Email(message = "Invalid email address")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    public User(
        String id,
        String username,
        String passwordHash,
        String email,
        String role
    ) {
        this.id = id;
        this.username = username;
        this.password = passwordHash;
        this.email = email;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String plainPassword) {
        this.password = PasswordUtils.hashPassword(plainPassword);
    }

    public boolean verifyPassword(String plainPassword) {
        return PasswordUtils.verifyPassword(plainPassword, this.password);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Utility Methods
    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    @Override
    public String toString() {
        return (
            "User{" +
            "id='" +
            id +
            '\'' +
            ", username='" +
            username +
            '\'' +
            ", email='" +
            email +
            '\'' +
            ", role='" +
            role +
            '\'' +
            ", isActive=" +
            isActive +
            '}'
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return (
            Objects.equals(id, user.id) &&
            Objects.equals(username, user.username)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}
