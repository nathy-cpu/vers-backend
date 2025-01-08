package org.vers.backend.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import org.vers.backend.entity.DivorceEvent;
import org.vers.backend.repository.DivorceEventRepository;

@ApplicationScoped
public class DivorceEventService {

    @Inject
    DivorceEventRepository divorceEventRepository;

    public Optional<DivorceEvent> findById(Integer id) {
        return divorceEventRepository.findById(id);
    }

    public void registerDivorceEvent(DivorceEvent divorceEvent) {
        divorceEventRepository.persist(divorceEvent);
    }
}
