package org.vers.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Embeddable
public class Location {

    public static final String country = "Ethiopia";

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    public Region region;

    @Column(nullable = false)
    public String zone;

    @Column(nullable = false)
    public String woreda;

    @Column(nullable = false)
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
