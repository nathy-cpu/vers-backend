package org.vers.backend.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import org.vers.backend.entity.Report;

@ApplicationScoped
public class ReportRepository implements PanacheRepository<Report> {

    public List<Report> findByUser(String username) {
        return list("generatedBy.username", username);
    }
}
