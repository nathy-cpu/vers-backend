package org.vers.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("MARRIAGE")
public class MarriageEvent extends Event {

    @Column(nullable = false)
    public String maleSpouseName;

    @Column(nullable = false)
    public String femaleSpouseName;

    @Column(nullable = false)
    public String witnessOneName;

    @Column(nullable = false)
    public String witnessTwoName;

    @Column(nullable = false)
    public String certifierName;

    @Column(nullable = false)
    public LocalDate dateOfMarriage;

    public MarriageEvent() {}

    public MarriageEvent(
        String maleSpouseName,
        String femaleSpouseName,
        String witnessOneName,
        String witnessTwoName,
        String certifierName,
        LocalDate dateOfMarriage
    ) {
        this.certifierName = certifierName;
        this.dateOfMarriage = dateOfMarriage;
        this.femaleSpouseName = femaleSpouseName;
        this.maleSpouseName = maleSpouseName;
        this.witnessOneName = witnessOneName;
        this.witnessTwoName = witnessTwoName;
    }

    @Override
    public String toString() {
        return (
            "MarriageEvent{" +
            "maleSpouseName='" +
            this.maleSpouseName +
            '\'' +
            ", femaleSpouseName='" +
            this.femaleSpouseName +
            '\'' +
            ", witnessOneName='" +
            this.witnessOneName +
            '\'' +
            ", witnessTwoName='" +
            this.witnessTwoName +
            '\'' +
            ", certifierName='" +
            this.certifierName +
            '\'' +
            ", dateOfMarriage=" +
            this.dateOfMarriage +
            '}'
        );
    }
}
