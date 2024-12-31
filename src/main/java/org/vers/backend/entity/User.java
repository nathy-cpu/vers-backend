package org.vers.backend.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import org.vers.backend.enums.Gender;
import org.vers.backend.enums.Role;
import org.vers.backend.utils.PasswordUtils;
import org.vers.backend.validation.InternationalPhone;

@Entity
public class User extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank(message = "Username is required")
    @Size(
        min = 4,
        max = 20,
        message = "Username must be between 4 and 20 characters"
    )
    public String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password must be at least 8 characters")
    public String password; // Hashed

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Role role;

    @Embedded
    public Name name;

    @Enumerated(EnumType.STRING)
    public Gender gender;

    public LocalDate dateOfBirth;

    @Email(message = "Invalid email address")
    public String email;

    @InternationalPhone(
        message = "Phone number must be in valid international format"
    )
    public String phoneNumber;

    @Column(nullable = false, updatable = false)
    public LocalDateTime createdAt;

    public boolean isActive;

    public User() {
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    public User(String username, String passwordHash, String email, Role role) {
        this.username = username;
        this.password = passwordHash;
        this.email = email;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    public boolean hasRole(Role role) {
        return this.role == role;
    }

    public void setPassword(String plainPassword) {
        this.password = PasswordUtils.hashPassword(plainPassword);
    }

    public boolean verifyPassword(String plainPassword) {
        return PasswordUtils.verifyPassword(plainPassword, this.password);
    }

    public int getAge() {
        if (dateOfBirth == null) {
            throw new IllegalStateException("Date of birth not set");
        }
        return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
    }

    public static User findByUsername(String username) {
        return find("username", username).firstResult();
    }

    public static List<User> findActiveUsers() {
        return list("isActive", true);
    }

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
            "id=" +
            this.id +
            ", username='" +
            this.username +
            '\'' +
            ", email='" +
            this.email +
            '\'' +
            ", role=" +
            this.role +
            ", isActive=" +
            this.isActive +
            '}'
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return (
            Objects.equals(this.id, user.id) &&
            Objects.equals(this.username, user.username)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, username);
    }

    public String getMaskedEmail() {
        int index = email.indexOf("@");
        return email.substring(0, 2) + "****" + email.substring(index);
    }
}
