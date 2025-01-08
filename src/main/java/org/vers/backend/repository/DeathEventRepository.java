package org.vers.backend.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import org.vers.backend.entity.DeathEvent;

@ApplicationScoped
public class DeathEventRepository implements PanacheRepository<DeathEvent> {

    public Optional<DeathEvent> findById(Integer id) {
        return find("id", id).firstResultOptional();
    }

    public Optional<DeathEvent> findByDeceasedName(String deceasedName) {
        return find("deceasedName", deceasedName).firstResultOptional();
    }

    public List<DeathEvent> findByCauseOfDeath(String causeOfDeath) {
        return find("causeOfDeath", causeOfDeath).list();
    }

    public List<DeathEvent> findByCertifierName(String certifierName) {
        return find("certifierName", certifierName).list();
    }
}
