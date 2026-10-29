package org.vers.backend.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.vers.backend.dto.EventResponse;
import org.vers.backend.dto.ReportFilterOptions;
import org.vers.backend.dto.ReportResponse;
import org.vers.backend.entity.BirthEvent;
import org.vers.backend.entity.DeathEvent;
import org.vers.backend.entity.Event;
import org.vers.backend.entity.Report;
import org.vers.backend.enums.EventStatus;
import org.vers.backend.enums.EventType;
import org.vers.backend.enums.Gender;
import org.vers.backend.repository.EventRepository;
import org.vers.backend.repository.ReportRepository;

@ApplicationScoped
public class ReportService {

    @Inject
    ReportRepository reportRepository;

    @Inject
    EventRepository eventRepository;

    public Optional<Report> findById(Long id) {
        return reportRepository.findByIdOptional(id);
    }

    public void registerReport(Report report) {
        reportRepository.persist(report);
    }

    public List<Report> findAllReports() {
        return reportRepository.listAll();
    }

    /** Every filter is optional. A gender filter keeps only births and deaths. */
    public ReportResponse summarize(
        EventType type,
        EventStatus status,
        Gender gender,
        String region,
        Integer registrationYear
    ) {
        List<Event> events = eventRepository
            .search(type, status, region, registrationYear)
            .stream()
            .filter(e -> gender == null || gender == genderOf(e))
            .toList();

        ReportResponse report = new ReportResponse();
        report.filters = new LinkedHashMap<>();
        report.filters.put("type", type);
        report.filters.put("status", status);
        report.filters.put("gender", gender);
        report.filters.put("region", region);
        report.filters.put("registrationYear", registrationYear);

        report.totalEvents = events.size();
        report.byType = countByEnum(events, EventType.values(), e ->
            EventResponse.typeOf(e)
        );
        report.byStatus = countByEnum(events, EventStatus.values(), e -> e.status);
        report.byGender = countByEnum(
            events.stream().filter(e -> genderOf(e) != null).toList(),
            Gender.values(),
            ReportService::genderOf
        );
        report.byRegion = events
            .stream()
            .collect(
                Collectors.groupingBy(
                    e -> e.location.region,
                    TreeMap::new,
                    Collectors.counting()
                )
            );
        report.byMonth = events
            .stream()
            .collect(
                Collectors.groupingBy(
                    e -> e.dateOfRegistration.toString().substring(0, 7),
                    TreeMap::new,
                    Collectors.counting()
                )
            );
        OptionalDouble averageWeight = events
            .stream()
            .filter(e -> e instanceof BirthEvent b && b.birthWeight != null)
            .mapToDouble(e -> ((BirthEvent) e).birthWeight)
            .average();
        report.averageBirthWeight = averageWeight.isPresent()
            ? Math.round(averageWeight.getAsDouble() * 100) / 100.0
            : null;
        return report;
    }

    public ReportFilterOptions filterOptions() {
        ReportFilterOptions options = new ReportFilterOptions();
        options.types = names(EventType.values());
        options.statuses = names(EventStatus.values());
        options.genders = names(Gender.values());
        options.regions = eventRepository
            .getEntityManager()
            .createQuery(
                "select distinct e.location.region from Event e order by 1",
                String.class
            )
            .getResultList();
        options.registrationYears = eventRepository
            .getEntityManager()
            .createQuery(
                "select distinct year(e.dateOfRegistration) from Event e order by 1 desc",
                Integer.class
            )
            .getResultList();
        return options;
    }

    private static Gender genderOf(Event event) {
        if (event instanceof BirthEvent birth) return birth.gender;
        if (event instanceof DeathEvent death) return death.gender;
        return null;
    }

    /** Counts per enum constant, keeping zero counts so every key is present. */
    private static <E extends Enum<E>> Map<String, Long> countByEnum(
        List<Event> events,
        E[] keys,
        Function<Event, E> classifier
    ) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (E key : keys) counts.put(key.toString(), 0L);
        for (Event event : events) {
            counts.merge(classifier.apply(event).toString(), 1L, Long::sum);
        }
        return counts;
    }

    private static List<String> names(Enum<?>[] values) {
        return Arrays.stream(values).map(Enum::toString).toList();
    }
}
