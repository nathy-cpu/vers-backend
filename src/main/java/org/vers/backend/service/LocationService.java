package org.vers.backend.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import org.vers.backend.entity.Location;
import org.vers.backend.repository.LocationRepository;

@ApplicationScoped
public class LocationService {

    @Inject
    LocationRepository locationRepository;

    public Optional<Location> findByCompositeKey(
        String region,
        String zone,
        String woreda
    ) {
        return locationRepository.findByCompositeKey(region, zone, woreda);
    }

    public Location createOrUpdateLocation(Location location) {
        Optional<Location> existingLocation = findByCompositeKey(
            location.region,
            location.zone,
            location.woreda
        );

        if (existingLocation.isEmpty()) {
            locationRepository.persist(location);
            return location;
        } else {
            return existingLocation.get();
        }
    }
}
