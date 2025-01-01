package org.vers.backend.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import org.vers.backend.entity.Court;

@ApplicationScoped
public class CourtRepository implements PanacheRepository<Court> {

    public Optional<Court> findByName(String name) {
        return find("name", name).firstResultOptional();
    }
}
