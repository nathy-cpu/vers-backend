package org.vers.backend.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import org.vers.backend.entity.MarriageEvent;

@ApplicationScoped
public class MarriageEventRepository
    implements PanacheRepository<MarriageEvent> {

    public Optional<MarriageEvent> findById(Integer id) {
        return find("id", id).firstResultOptional();
    }
}
