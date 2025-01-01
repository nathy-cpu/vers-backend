package org.vers.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "DivorceEvent")
public class DivorceEvent extends Event {

    @ManyToOne
    @JoinColumn(name = "male_spouse_id", nullable = false)
    private Person maleSpouse;

    @ManyToOne
    @JoinColumn(name = "female_spouse_id", nullable = false)
    private Person femaleSpouse;

    @ManyToOne
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    // Constructors
    public DivorceEvent() {}

    public DivorceEvent(Person maleSpouse, Person femaleSpouse, Court court) {
        this.maleSpouse = maleSpouse;
        this.femaleSpouse = femaleSpouse;
        this.court = court;
    }

    // Getters and Setters
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

    public Court getCourt() {
        return court;
    }

    public void setCourt(Court court) {
        this.court = court;
    }

    // Utility Methods
    public String getMaleSpouseFullName() {
        return maleSpouse.getFullName();
    }

    public String getFemaleSpouseFullName() {
        return femaleSpouse.getFullName();
    }

    @Override
    public String toString() {
        return (
            "DivorceEvent{" +
            "maleSpouse=" +
            maleSpouse.getFullName() +
            ", femaleSpouse=" +
            femaleSpouse.getFullName() +
            ", court=" +
            court.getName() +
            '}'
        );
    }
}
