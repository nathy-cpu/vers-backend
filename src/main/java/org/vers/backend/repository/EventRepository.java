package org.vers.backend.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import org.vers.backend.entity.Event;

@ApplicationScoped
public class EventRepository implements PanacheRepository<Event> {

    public Optional<Event> findById(Integer id) {
        return find("id", id).firstResultOptional();
    }
}
