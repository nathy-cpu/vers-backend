package org.vers.backend.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import org.vers.backend.entity.DeathEvent;
import org.vers.backend.repository.DeathEventRepository;

@ApplicationScoped
public class DeathEventService {

    @Inject
    DeathEventRepository deathEventRepository;

    public Optional<DeathEvent> findById(Long id) {
        return Optional.of(deathEventRepository.findById(id));
    }

    public void registerDeathEvent(DeathEvent deathEvent) {
        deathEventRepository.persist(deathEvent);
    }
}
