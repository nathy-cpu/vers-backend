package org.vers.backend.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import org.vers.backend.entity.Event;
import org.vers.backend.entity.User;
import org.vers.backend.enums.EventStatus;

@ApplicationScoped
public class EventRepository implements PanacheRepository<Event> {

    public List<Event> findByStatus(EventStatus status) {
        return list("status", status.toString());
    }

    public Optional<Event> findById(Integer id) {
        return find("id", id).firstResultOptional();
    }

    public List<Event> findByRegistrar(User registrar) {
        return list("registrar_username", registrar.username);
    }
}
