package org.vers.backend.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;
import org.vers.backend.entity.Report;

@ApplicationScoped
public class ReportRepository implements PanacheRepository<Report> {

    public Optional<Report> findById(Integer id) {
        return find("id", id).firstResultOptional();
    }
}
