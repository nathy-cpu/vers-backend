package org.vers.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "BirthEvent")
public class BirthEvent extends Event {

    @ManyToOne
    @JoinColumn(name = "child_id", nullable = false)
    private Person child;

    @ManyToOne
    @JoinColumn(name = "father_id", nullable = false)
    private Person father;

    @ManyToOne
    @JoinColumn(name = "mother_id", nullable = false)
    private Person mother;

    @Column(name = "birth_weight", nullable = false)
    private Float birthWeight;

    public BirthEvent(
        Person child,
        Person father,
        Person mother,
        Float birthWeight
    ) {
        this.child = child;
        this.father = father;
        this.mother = mother;
        this.birthWeight = birthWeight;
    }

    public Person getChild() {
        return child;
    }

    public void setChild(Person child) {
        this.child = child;
    }

    public Person getFather() {
        return father;
    }

    public void setFather(Person father) {
        this.father = father;
    }

    public Person getMother() {
        return mother;
    }

    public void setMother(Person mother) {
        this.mother = mother;
    }

    public Float getBirthWeight() {
        return birthWeight;
    }

    public void setBirthWeight(Float birthWeight) {
        this.birthWeight = birthWeight;
    }
}
