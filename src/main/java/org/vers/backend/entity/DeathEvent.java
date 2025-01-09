package org.vers.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import org.vers.backend.enums.Gender;

@Entity
@DiscriminatorValue("DEATH")
public class DeathEvent extends Event {

    @Column(nullable = false)
    public String deceasedName;

    @Column(nullable = false)
    public Gender gender;

    @Column(nullable = false)
    public String causeOfDeath;

    @Column(nullable = false)
    public String certifierName;

    @Column(nullable = false)
    public LocalDate dateOfDeath;

    public DeathEvent() {}

    public DeathEvent(
        String deceasedName,
        Gender gender,
        String causeOfDeath,
        String certifierName,
        LocalDate dateOfDeath
    ) {
        this.deceasedName = deceasedName;
        this.certifierName = certifierName;
        this.dateOfDeath = dateOfDeath;
        this.causeOfDeath = causeOfDeath;
        this.gender = gender;
    }

    @Override
    public String toString() {
        return (
            "DeathEvent{" +
            "deceasedName='" +
            deceasedName +
            '\'' +
            ", causeOfDeath='" +
            causeOfDeath +
            '\'' +
            ", certifierName='" +
            certifierName +
            '\'' +
            ", dateOfDeath=" +
            dateOfDeath +
            '}'
        );
    }
}
