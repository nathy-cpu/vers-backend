package org.vers.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Location")
public class Location {

    public static final String country = "Ethiopia";

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    public Region region;

    @Column(nullable = false, name = "zone")
    public String zone;

    @Column(nullable = false, name = "woreda")
    public String woreda;

    @Column(nullable = false, name = "kebele")
    public String kebele;

    public Location(Region region, String zone, String woreda, String kebele) {
        this.region = region;
        this.zone = zone;
        this.kebele = kebele;
        this.woreda = woreda;
    }

    @Override
    public String toString() {
        return String.format(
            "%s, %s, %s, %s, %s",
            country,
            this.region.getName(),
            this.zone,
            this.woreda,
            this.kebele
        );
    }
}
