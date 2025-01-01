package org.vers.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "Marriage")
public class MarriageEvent extends Event {

    @JoinColumn(name = "male_spouse_id", nullable = false)
    private Person maleSpouse;

    @JoinColumn(name = "female_spouse_id", nullable = false)
    private Person femaleSpouse;

    @JoinColumn(name = "witness_one_id", nullable = false)
    private String witnessOne;

    @JoinColumn(name = "witness_two_id", nullable = false)
    private Person witnessTwo;

    @JoinColumn(name = "witness_three_id", nullable = false)
    private Person witnessThree;

    public MarriageEvent(
        Person maleSpouse,
        Person femaleSpouse,
        String witnessOne,
        Person witnessTwo,
        Person witnessThree
    ) {
        this.maleSpouse = maleSpouse;
        this.femaleSpouse = femaleSpouse;
        this.witnessOne = witnessOne;
        this.witnessTwo = witnessTwo;
        this.witnessThree = witnessThree;
    }

    public Person getMaleSpouse() {
        return maleSpouse;
    }

    public void setMaleSpouse(Person maleSpouse) {
        this.maleSpouse = maleSpouse;
    }

    public Person getFemaleSpouse() {
        return femaleSpouse;
    }

    public void setFemaleSpouse(Person femaleSpouse) {
        this.femaleSpouse = femaleSpouse;
    }

    public String getWitnessOne() {
        return witnessOne;
    }

    public void setWitnessOne(String witnessOne) {
        this.witnessOne = witnessOne;
    }

    public Person getWitnessTwo() {
        return witnessTwo;
    }

    public void setWitnessTwo(Person witnessTwo) {
        this.witnessTwo = witnessTwo;
    }

    public Person getWitnessThree() {
        return witnessThree;
    }

    public void setWitnessThree(Person witnessThree) {
        this.witnessThree = witnessThree;
    }
}
