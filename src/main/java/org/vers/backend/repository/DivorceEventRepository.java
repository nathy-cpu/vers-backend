package org.vers.backend.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import org.vers.backend.entity.DivorceEvent;

@ApplicationScoped
public class DivorceEventRepository implements PanacheRepository<DivorceEvent> {

    public Optional<DivorceEvent> findById(Integer id) {
        return find("id", id).firstResultOptional();
    }
}
