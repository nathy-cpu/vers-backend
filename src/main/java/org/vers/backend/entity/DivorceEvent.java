package org.vers.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;

@Entity
public class DivorceEvent extends Event {

    @Embedded
    @Column(nullable = false)
    private Name maleSpouseName;

    @Embedded
    @Column(nullable = false)
    private Name femaleSpouseName;

    public DivorceEvent(Name maleSpouseName, Name femaleSpouseName) {
        this.maleSpouseName = maleSpouseName;
        this.femaleSpouseName = femaleSpouseName;
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
}
