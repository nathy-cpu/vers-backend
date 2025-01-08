package org.vers.backend.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import org.vers.backend.entity.Report;
import org.vers.backend.repository.ReportRepository;

@ApplicationScoped
public class ReportService {

    @Inject
    ReportRepository reportRepository;

    public Optional<Report> findById(Integer id) {
        return reportRepository.findById(id);
    }

    public void registerReport(Report report) {
        reportRepository.persist(report);
    }

    public List<Report> findAllReports() {
        return reportRepository.listAll();
    }
}
