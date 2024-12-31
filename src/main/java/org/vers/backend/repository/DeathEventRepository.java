package org.vers.backend.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.vers.backend.entity.DeathEvent;

@ApplicationScoped
public class DeathEventRepository implements PanacheRepository<DeathEvent> {}
