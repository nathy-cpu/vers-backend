package org.vers.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "DeathEvent")
public class DeathEvent extends Event {

    @JoinColumn(name = "deceased_id", nullable = false)
    private Person deceasedID;

    @NotBlank(message = "Cause of death is required")
    @Column(name = "cause_of_death", nullable = false)
    private String causeOfDeath;

    @NotBlank(message = "Certifier details are required")
    @JoinColumn(name = "certifier_id", nullable = false)
    private String certifier; // e.g., doctor or institution

    public DeathEvent(
        Person deceasedID,
        String causeOfDeath,
        String certifier
    ) {
        this.deceasedID = deceasedID;
        this.causeOfDeath = causeOfDeath;
        this.certifier = certifier;
    }

    public Person getDeceasedID() {
        return deceasedID;
    }

    public void setDeceasedID(Person deceasedID) {
        this.deceasedID = deceasedID;
    }

    public String getCauseOfDeath() {
        return causeOfDeath;
    }

    public void setCauseOfDeath(String causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }

    public String getCertifier() {
        return certifier;
    }

    public void setCertifier(String certifier) {
        this.certifier = certifier;
    }
}
