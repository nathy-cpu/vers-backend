package org.vers.backend.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import org.vers.backend.entity.Location;

@ApplicationScoped
public class LocationRepository implements PanacheRepository<Location> {

    public Optional<Location> findByCompositeKey(
        Integer regionId,
        String zone,
        String woreda,
        String kebele
    ) {
        return find(
            "region_id = ?1 and zone = ?2 and woreda = ?3 and kebele = ?4",
            regionId,
            zone,
            woreda,
            kebele
        ).firstResultOptional();
    }
}
