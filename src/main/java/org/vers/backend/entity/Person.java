package org.vers.backend.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.time.Period;
import org.vers.backend.enums.Gender;
import org.vers.backend.validation.InternationalPhone;

@Entity
public class Person extends PanacheEntity {

    @NotBlank
    @Column(nullable = false)
    public String firstName;

    @NotBlank
    @Column(nullable = false)
    public String middleName;

    @NotBlank
    @Column(nullable = false)
    public String lastName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public Gender gender;

    @Past
    @NotNull
    @Column(nullable = false)
    public LocalDate dateOfBirth;

    @NotNull
    @InternationalPhone
    @Column(nullable = false)
    public String phoneNumber;

    public Person() {}

    public Person(
        String firstName,
        String middleName,
        String lastName,
        Gender gender,
        LocalDate dateOfBirth,
        String phoneNumber
    ) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return String.format(
            "%s %s %s",
            this.firstName,
            this.middleName,
            this.lastName
        );
    }

    public void setFullName(
        String firstName,
        String middleName,
        String lastName
    ) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

    public Integer getAge() {
        return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
    }

    public boolean isEmpty() {
        // TODO: properly implement this method
        return (
            this.firstName.isBlank() &&
            this.middleName.isBlank() &&
            this.lastName.isBlank()
        );
    }
}
