package org.vers.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.vers.backend.enums.CourtType;
import org.vers.backend.enums.Jurisdiction;
import org.vers.backend.validation.ValidLocation;

@Entity
@Table(name = "Court")
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "type")
    private CourtType type;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    @ValidLocation
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "jurisdiction")
    private Jurisdiction jurisdiction;

    public Court(
        String name,
        CourtType type,
        Location location,
        Jurisdiction jurisdiction
    ) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.jurisdiction = jurisdiction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CourtType getType() {
        return this.type;
    }

    public void setType(CourtType type) {
        this.type = type;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Jurisdiction getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(Jurisdiction jurisdiction) {
        this.jurisdiction = jurisdiction;
    }
}
