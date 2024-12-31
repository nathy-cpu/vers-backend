package org.vers.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;

@Entity
public class MarriageEvent extends Event {

    @Embedded
    @NotBlank(message = "Male spouse name is rquired")
    private Name maleSpouseName;

    @Embedded
    @NotBlank(message = "Female spouse name is rquired")
    private Name femaleSpouseName;

    @Embedded
    @Column(nullable = false)
    private Name witnessOne;

    @Embedded
    @Column(nullable = false)
    private Name witnessTwo;

    @Embedded
    @Column(nullable = false)
    private Name witnessThree;

    public MarriageEvent(
        Name maleSpouseName,
        Name femaleSpouseName,
        Name witnessOne,
        Name witnessTwo,
        Name witnessThree
    ) {
        this.maleSpouseName = maleSpouseName;
        this.femaleSpouseName = femaleSpouseName;
        this.witnessOne = witnessOne;
        this.witnessTwo = witnessTwo;
        this.witnessThree = witnessThree;
    }

    public Name getFemaleSpouseName() {
        return this.femaleSpouseName;
    }

    public void setFemaleSpouseName(Name femaleSpouseName) {
        this.femaleSpouseName = femaleSpouseName;
    }

    public Name getMaleSpouseName() {
        return this.maleSpouseName;
    }

    public void setMaleSpouseName(Name maleSpouseName) {
        this.maleSpouseName = maleSpouseName;
    }

    public Name getWitnessOne() {
        return witnessOne;
    }

    public void setWitnessOne(Name witnessOne) {
        this.witnessOne = witnessOne;
    }

    public Name getWitnessTwo() {
        return witnessTwo;
    }

    public void setWitnessTwo(Name witnessTwo) {
        this.witnessTwo = witnessTwo;
    }

    public Name getWitnessThree() {
        return this.witnessThree;
    }

    public void setWitnessThree(Name witnessThree) {
        this.witnessThree = witnessThree;
    }
}
