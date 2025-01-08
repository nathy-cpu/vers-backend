package org.vers.backend.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import org.vers.backend.enums.EventStatus;
import org.vers.backend.enums.EventType;
import org.vers.backend.validation.ValidLocation;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class Event extends PanacheEntity {

    @Column(nullable = false, updatable = false)
    public LocalDate dateOfRegistration;

    @Column(nullable = false, insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    public EventType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public EventStatus status;

    @ManyToOne
    @JoinColumn(name = "registrar_username", referencedColumnName = "username", nullable = false)
    public User registrar;

    @ManyToOne
    @ValidLocation
    @JoinColumn(name = "location_id", nullable = false)
    public Location location;

    public Event() {
        this.dateOfRegistration = LocalDate.now();
        this.status = EventStatus.PENDING;
    }
}
