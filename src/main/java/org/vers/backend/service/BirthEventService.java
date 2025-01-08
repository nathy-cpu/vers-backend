package org.vers.backend.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import org.vers.backend.entity.BirthEvent;
import org.vers.backend.repository.BirthEventRepository;

@ApplicationScoped
public class BirthEventService {

    @Inject
    BirthEventRepository birthEventRepository;

    public Optional<BirthEvent> findById(Integer id) {
        return birthEventRepository.findById(id);
    }

    public void registerBirthEvent(BirthEvent birthEvent) {
        birthEventRepository.persist(birthEvent);
    }
}
