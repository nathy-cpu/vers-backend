package org.vers.backend.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.List;
import org.vers.backend.enums.EventStatus;
import org.vers.backend.enums.EventType;
import org.vers.backend.validation.ValidLocation;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "event_type")
public abstract class Event extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "registrar_id")
    public User registeredBy;

    @Enumerated(EnumType.STRING)
    public EventType type;

    @ValidLocation
    @Embedded
    @Column(nullable = false)
    public Location location;

    @Enumerated(EnumType.STRING)
    public EventStatus status;

    @Column(nullable = false, updatable = false)
    public LocalDateTime createdAt;

    public Event() {
        this.createdAt = LocalDateTime.now();
        this.status = EventStatus.PENDING;
    }

    public void approve() {
        this.status = EventStatus.APPROVED;
    }

    public void reject() {
        this.status = EventStatus.REJECTED;
    }

    public boolean isType(EventType type) {
        return this.type == type;
    }

    public static List<Event> findByRegistrar(Long registrarId) {
        return list("registrar.id", registrarId);
    }

    public static List<Event> findByStatus(EventStatus status) {
        return list("status", status);
    }
}
