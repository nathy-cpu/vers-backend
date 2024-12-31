package org.vers.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.vers.backend.enums.Gender;

@Entity
@DiscriminatorValue("BIRTH")
public class BirthEvent extends Event {

    @Embedded
    @Column(nullable = false)
    private Name childName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Embedded
    @Column(nullable = false)
    private Name fatherName;

    @Embedded
    @Column(nullable = false)
    private Name motherName;

    public BirthEvent(
        Name childName,
        Gender gender,
        Name fatherName,
        Name motherName
    ) {
        this.childName = childName;
        this.gender = gender;
        this.fatherName = fatherName;
        this.motherName = motherName;
    }

    public String getChildName() {
        return childName.getFullName();
    }

    public void setChildName(Name childName) {
        this.childName = childName;
    }

    public String getGender() {
        return gender.toString();
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getFatherName() {
        return this.fatherName.getFullName();
    }

    public void setFatherName(Name fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return this.motherName.getFullName();
    }

    public void setMotherName(Name motherName) {
        this.motherName = motherName;
    }
}
