package org.vers.backend.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.vers.backend.entity.Event;
import org.vers.backend.repository.EventRepository;

@ApplicationScoped
public class EventService {

    @Inject
    EventRepository eventRepository;

    public Optional<Event> findById(Integer id) {
        return eventRepository.findById(id);
    }

    public void registerEvent(Event event) {
        eventRepository.persist(event);
    }

    public List<Event> findAllEvents() {
        return eventRepository.listAll();
    }
}
