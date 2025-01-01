package org.vers.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;
import org.vers.backend.enums.Gender;

@Entity
@Table(name = "Person")
public class Person {

    @Id
    @Column(name = "national_id", nullable = false, unique = true)
    @NotBlank(message = "National ID is required")
    private String nationalId;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Middle name is required")
    @Size(max = 50, message = "Middle name cannot exceed 50 characters")
    @Column(name = "middle_name")
    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "Gender is required")
    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender; // Could be validated against predefined constants or enums

    @Past(message = "Date of birth must be in the past")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    // Constructors
    public Person() {}

    public Person(
        String nationalId,
        String firstName,
        String middleName,
        String lastName,
        Gender gender,
        LocalDate dateOfBirth
    ) {
        this.nationalId = nationalId;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    // Getters and Setters
    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    // Utility Methods
    public String getFullName() {
        return firstName + " " + middleName + " " + lastName;
    }

    @Override
    public String toString() {
        return (
            "Person{" +
            "nationalId='" +
            nationalId +
            '\'' +
            ", firstName='" +
            firstName +
            '\'' +
            ", middleName='" +
            middleName +
            '\'' +
            ", lastName='" +
            lastName +
            '\'' +
            ", gender='" +
            gender +
            '\'' +
            ", dateOfBirth=" +
            dateOfBirth +
            '}'
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return Objects.equals(nationalId, person.nationalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nationalId);
    }
}
