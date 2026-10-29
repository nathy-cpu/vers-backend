package org.vers.backend.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.vers.backend.entity.DivorceEvent;

@ApplicationScoped
public class DivorceEventRepository implements PanacheRepository<DivorceEvent> {

    public List<DivorceEvent> findBySpouseName(String spouseName) {
        return find(
            "maleSpouseName = ?1 or femaleSpouseName = ?1",
            spouseName
        ).list();
    }
}
