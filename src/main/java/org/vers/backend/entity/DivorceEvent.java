package org.vers.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("DIVORCE")
public class DivorceEvent extends Event {

    @Column(nullable = false)
    public String maleSpouseName;

    @Column(nullable = false)
    public String femaleSpouseName;

    @Column(nullable = false)
    public String courtName;

    @Column(nullable = false)
    public LocalDate dateOfDivorce;

    public DivorceEvent() {
    }

    public DivorceEvent(
            String maleSpouseName,
            String femaleSpouseName,
            String courtName,
            LocalDate dateOfDivorce) {
        this.maleSpouseName = maleSpouseName;
        this.femaleSpouseName = femaleSpouseName;
        this.courtName = courtName;
        this.dateOfDivorce = dateOfDivorce;
    }

    @Override
    public String toString() {
        return ("DivorceEvent{" +
                "maleSpouseName='" +
                this.maleSpouseName +
                '\'' +
                ", femaleSpouseName='" +
                this.femaleSpouseName +
                '\'' +
                ", courtName='" +
                this.courtName +
                '\'' +
                ", dateOfDivorce=" +
                this.dateOfDivorce.toString() +
                '}');
    }
}
