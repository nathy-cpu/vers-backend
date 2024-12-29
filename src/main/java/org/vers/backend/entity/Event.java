package org.vers.backend.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

enum EventType {
    Death,
    Birth,
    Marriage,
    Divorce,
}

enum Status {
    Pending,
    Approved,
    Rejected,
}

@Entity
public class Event extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public Long userID;

    public EventType type;
    public String location;
    public Status status;
    public LocalDateTime createdAt;
}
