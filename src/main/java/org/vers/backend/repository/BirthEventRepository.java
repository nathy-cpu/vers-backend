package org.vers.backend.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import org.vers.backend.entity.BirthEvent;

@ApplicationScoped
public class BirthEventRepository implements PanacheRepository<BirthEvent> {

    public Optional<BirthEvent> findById(Integer id) {
        return find("id", id).firstResultOptional();
    }
}
