package org.vers.backend.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    uniqueConstraints = @UniqueConstraint(
        columnNames = { "region", "zone", "woreda" }
    )
)
public class Location extends PanacheEntity {

    @Column(nullable = false, columnDefinition = "varchar default 'Ethiopia'")
    public String country;

    @Column(nullable = false)
    public String region;

    @Column(nullable = false)
    public String zone;

    @Column(nullable = false)
    public String woreda;

    public Location() {
        this.country = "Ethiopia";
    }

    public Location(String region, String zone, String woreda) {
        this.region = region;
        this.zone = zone;
        this.woreda = woreda;
        this.country = "Ethiopia";
    }

    @Override
    public String toString() {
        return (
            "Location{" +
            "id=" +
            id +
            ", country='" +
            country +
            '\'' +
            ", region=" +
            region +
            ", zone='" +
            zone +
            '\'' +
            ", woreda='" +
            woreda +
            '\'' +
            '}'
        );
    }
}
