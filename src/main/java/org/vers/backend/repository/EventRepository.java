package org.vers.backend.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.vers.backend.entity.Event;
import org.vers.backend.entity.User;
import org.vers.backend.enums.EventStatus;
import org.vers.backend.enums.EventType;

@ApplicationScoped
public class EventRepository implements PanacheRepository<Event> {

    public List<Event> findByStatus(EventStatus status) {
        return list("status", status);
    }

    public List<Event> findByRegistrar(User registrar) {
        return list("registrar", registrar);
    }

    public boolean isReferencedBy(User user) {
        return count("registrar = ?1 or reviewedBy = ?1", user) > 0;
    }

    /** Every argument is optional; a null argument means "don't filter on this". */
    public List<Event> search(
        EventType type,
        EventStatus status,
        String region,
        Integer registrationYear
    ) {
        StringBuilder query = new StringBuilder("1 = 1");
        Parameters params = new Parameters();
        if (type != null) {
            query.append(" and type = :type");
            params.and("type", type);
        }
        if (status != null) {
            query.append(" and status = :status");
            params.and("status", status);
        }
        if (region != null) {
            query.append(" and lower(location.region) = :region");
            params.and("region", region.toLowerCase());
        }
        if (registrationYear != null) {
            query.append(" and year(dateOfRegistration) = :year");
            params.and("year", registrationYear);
        }
        query.append(" order by id desc");
        return list(query.toString(), params);
    }
}
