package org.vers.backend.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.vers.backend.entity.Court;
import org.vers.backend.enums.Jurisdiction;

@ApplicationScoped
public class CourtRepository implements PanacheRepository<Court> {

    public List<Court> findByJurisdiction(Jurisdiction jurisdiction) {
        return list("jurisdiction", jurisdiction);
    }
}
