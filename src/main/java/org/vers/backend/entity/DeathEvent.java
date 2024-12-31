package org.vers.backend.entity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;

@Entity
public class DeathEvent extends Event {

    @Embedded
    @NotBlank(message = "Name of the deceases is required")
    private Name deceasedName;

    @NotBlank(message = "Cause of death is required")
    private String causeOfDeath;

    @NotBlank(message = "Certifier details are required")
    private String certifier; // e.g., doctor or institution

    public DeathEvent(
        Name deceasedName,
        String causeOfDeath,
        String certifier
    ) {
        this.deceasedName = deceasedName;
        this.causeOfDeath = causeOfDeath;
        this.certifier = certifier;
    }

    public Name getDeceasedName() {
        return this.deceasedName;
    }

    public void setDeceasedName(Name deceasedName) {
        this.deceasedName = deceasedName;
    }

    public String getCauseOfDeath() {
        return this.causeOfDeath;
    }

    public void setCauseOfDeath(String causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }

    public String getCertifier() {
        return this.certifier;
    }

    public void setCertifier(String certifier) {
        this.certifier = certifier;
    }
}
