package org.vers.backend.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.vers.backend.entity.Region;
import org.vers.backend.enums.RegionType;

@ApplicationScoped
public class RegionRepository implements PanacheRepository<Region> {

    public List<Region> findByType(RegionType type) {
        return list("type", type);
    }
}
