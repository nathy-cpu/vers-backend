package org.vers.backend.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import org.vers.backend.entity.MarriageEvent;
import org.vers.backend.repository.MarriageEventRepository;

@ApplicationScoped
public class MarriageEventService {

    @Inject
    MarriageEventRepository marriageEventRepository;

    public Optional<MarriageEvent> findById(Long id) {
        return marriageEventRepository.findByIdOptional(id);
    }

    public void registerMarriageEvent(MarriageEvent marriageEvent) {
        marriageEventRepository.persist(marriageEvent);
    }
}
