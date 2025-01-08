package org.vers.backend.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import org.vers.backend.entity.Location;

@ApplicationScoped
public class LocationRepository implements PanacheRepository<Location> {

    public Optional<Location> findByCompositeKey(
        String region,
        String zone,
        String woreda
    ) {
        return find(
            "region = ?1 and zone = ?2 and woreda = ?3",
            region,
            zone,
            woreda
        ).firstResultOptional();
    }
}
