package org.vers.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDate;
import org.vers.backend.enums.Gender;

@Entity
@DiscriminatorValue("BIRTH")
public class BirthEvent extends Event {

    @Column(nullable = false)
    public String childName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public Gender gender;

    @Column(nullable = false)
    public String fatherName;

    @Column(nullable = false)
    public String motherName;

    @Column
    public Float birthWeight;

    @Column(nullable = false)
    public LocalDate dateOfBirth;

    public BirthEvent() {}

    public BirthEvent(
        String childName,
        String fatherName,
        String motherName,
        Gender gender,
        Float birthWeight,
        LocalDate dateOfBirth
    ) {
        this.childName = childName;
        this.fatherName = fatherName;
        this.motherName = motherName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        return (
            "BirthEvent{" +
            "childName='" +
            this.childName +
            '\'' +
            ", fatherName='" +
            this.fatherName +
            '\'' +
            ", motherName='" +
            this.motherName +
            '\'' +
            ", birthWeight=" +
            this.birthWeight +
            ", dateOfBirth=" +
            this.dateOfBirth.toString() +
            "}"
        );
    }
}
