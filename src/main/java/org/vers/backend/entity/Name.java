package org.vers.backend.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Embeddable
public class Name {

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be 50 characters or less")
    private String firstName;

    @NotBlank(message = "Middle name is required")
    @Size(max = 50, message = "Middle name must be 50 characters or less")
    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be 50 characters or less")
    private String lastName;

    public Name(String firstName, String middleName, String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", firstName, middleName, lastName);
    }

    public String getFullName() {
        return String.format("%s %s %s", firstName, middleName, lastName);
    }
}
