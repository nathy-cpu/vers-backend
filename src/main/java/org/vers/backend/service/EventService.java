package org.vers.backend.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.vers.backend.dto.EventRequest;
import org.vers.backend.dto.EventResponse;
import org.vers.backend.entity.BirthEvent;
import org.vers.backend.entity.DeathEvent;
import org.vers.backend.entity.DivorceEvent;
import org.vers.backend.entity.Event;
import org.vers.backend.entity.Location;
import org.vers.backend.entity.MarriageEvent;
import org.vers.backend.entity.Person;
import org.vers.backend.entity.User;
import org.vers.backend.enums.EventStatus;
import org.vers.backend.enums.EventType;
import org.vers.backend.enums.Gender;
import org.vers.backend.exception.ApiException;
import org.vers.backend.repository.EventRepository;
import org.vers.backend.validation.InternationalPhoneValidator;

@ApplicationScoped
public class EventService {

    @Inject
    EventRepository eventRepository;

    @Inject
    LocationService locationService;

    public Optional<Event> findById(Long id) {
        return eventRepository.findByIdOptional(id);
    }

    public Event getById(Long id) {
        return findById(id).orElseThrow(() ->
            ApiException.notFound("Event not found.")
        );
    }

    public void registerEvent(Event event) {
        eventRepository.persist(event);
    }

    public List<Event> findAllEvents() {
        return eventRepository.listAll();
    }

    /** Filters on type/status (both optional) and a case-insensitive name match. */
    public List<Event> search(EventType type, EventStatus status, String name) {
        List<Event> events = eventRepository.search(type, status, null, null);
        if (name == null || name.isBlank()) return events;
        String term = name.trim().toLowerCase();
        return events
            .stream()
            .filter(e -> EventResponse.from(e).name.toLowerCase().contains(term))
            .toList();
    }

    public Event register(EventRequest request, User registrar) {
        EventType type = parseEnum(EventType.class, request.type, "type");
        Event event = switch (type) {
            case BIRTH -> new BirthEvent();
            case DEATH -> new DeathEvent();
            case MARRIAGE -> new MarriageEvent();
            case DIVORCE -> new DivorceEvent();
        };
        apply(event, request);
        event.registrar = registrar;
        event.persist();
        return event;
    }

    /**
     * Edits an event's details. The event goes back to PENDING so the change
     * is reviewed again; registrars can never set the status themselves.
     */
    public Event update(Long id, EventRequest request) {
        Event event = getById(id);
        EventType type = EventResponse.typeOf(event);
        if (
            request.type != null &&
            parseEnum(EventType.class, request.type, "type") != type
        ) {
            throw ApiException.badRequest(
                "An event's type cannot be changed (this is a " + type + " event)."
            );
        }
        apply(event, request);
        event.status = EventStatus.PENDING;
        event.reviewedBy = null;
        event.reviewedAt = null;
        event.rejectionReason = null;
        return event;
    }

    public Event approve(Long id, User reviewer) {
        Event event = startReview(id, reviewer);
        event.status = EventStatus.APPROVED;
        event.rejectionReason = null;
        return event;
    }

    public Event reject(Long id, User reviewer, String reason) {
        if (isBlank(reason)) {
            throw ApiException.badRequest("A reason is required to reject an event.");
        }
        Event event = startReview(id, reviewer);
        event.status = EventStatus.REJECTED;
        event.rejectionReason = reason.trim();
        return event;
    }

    private Event startReview(Long id, User reviewer) {
        Event event = getById(id);
        if (event.status != EventStatus.PENDING) {
            throw ApiException.conflict(
                "Only pending events can be reviewed (this one is " + event.status + ")."
            );
        }
        if (event.registrar.equals(reviewer)) {
            throw ApiException.forbidden("You cannot review an event you registered.");
        }
        event.reviewedBy = reviewer;
        event.reviewedAt = LocalDateTime.now();
        return event;
    }

    /** Validates the request and copies its fields onto the event. */
    private void apply(Event event, EventRequest r) {
        if (r.location == null) throw ApiException.badRequest("location is required.");
        String region = required(r.location.region, "location.region");
        String zone = required(r.location.zone, "location.zone");
        String woreda = required(r.location.woreda, "location.woreda");

        if (event instanceof BirthEvent birth) {
            String childName = required(r.childName, "childName");
            String[] nameParts = childName.split("\\s+");
            if (nameParts.length != 3) {
                throw ApiException.badRequest(
                    "childName must have first, middle, and last name."
                );
            }
            if (r.birthWeight != null && (r.birthWeight <= 0 || r.birthWeight > 10)) {
                throw ApiException.badRequest("birthWeight must be between 0 and 10 kg.");
            }
            String phone = isBlank(r.phoneNumber) ? null : r.phoneNumber.trim();
            if (phone != null && !new InternationalPhoneValidator().isValid(phone, null)) {
                throw ApiException.badRequest("phoneNumber is not a valid phone number.");
            }
            birth.childName = childName;
            birth.gender = parseEnum(Gender.class, r.gender, "gender");
            birth.fatherName = required(r.fatherName, "fatherName");
            birth.motherName = required(r.motherName, "motherName");
            birth.birthWeight = r.birthWeight;
            birth.dateOfBirth = parsePastDate(r.dateOfBirth, "dateOfBirth");

            Person child = birth.child != null ? birth.child : new Person();
            child.firstName = nameParts[0];
            child.middleName = nameParts[1];
            child.lastName = nameParts[2];
            child.gender = birth.gender;
            child.dateOfBirth = birth.dateOfBirth;
            child.phoneNumber = phone;
            birth.child = child;
        } else if (event instanceof DeathEvent death) {
            death.deceasedName = required(r.deceasedName, "deceasedName");
            death.gender = parseEnum(Gender.class, r.gender, "gender");
            death.causeOfDeath = required(r.causeOfDeath, "causeOfDeath");
            death.certifierName = required(r.certifierName, "certifierName");
            death.dateOfDeath = parsePastDate(r.dateOfDeath, "dateOfDeath");
            death.placeOfDeath = required(r.placeOfDeath, "placeOfDeath");
        } else if (event instanceof MarriageEvent marriage) {
            marriage.maleSpouseName = required(r.maleSpouseName, "maleSpouseName");
            marriage.femaleSpouseName = required(r.femaleSpouseName, "femaleSpouseName");
            marriage.witnessOneName = required(r.witnessOneName, "witnessOneName");
            marriage.witnessTwoName = required(r.witnessTwoName, "witnessTwoName");
            marriage.certifierName = required(r.certifierName, "certifierName");
            marriage.dateOfMarriage = parsePastDate(r.dateOfMarriage, "dateOfMarriage");
        } else if (event instanceof DivorceEvent divorce) {
            divorce.maleSpouseName = required(r.maleSpouseName, "maleSpouseName");
            divorce.femaleSpouseName = required(r.femaleSpouseName, "femaleSpouseName");
            divorce.courtName = required(r.courtName, "courtName");
            divorce.dateOfDivorce = parsePastDate(r.dateOfDivorce, "dateOfDivorce");
        }

        // Only create the location once everything else is known to be valid
        event.location = locationService.createOrUpdateLocation(
            new Location(region, zone, woreda)
        );
    }

    /** Like {@link #parseEnum}, but a null or blank value means "not given". */
    public static <E extends Enum<E>> E parseOptionalEnum(
        Class<E> enumType,
        String value,
        String field
    ) {
        return isBlank(value) ? null : parseEnum(enumType, value, field);
    }

    /** Case-insensitive enum lookup; a null or blank value is rejected. */
    public static <E extends Enum<E>> E parseEnum(
        Class<E> enumType,
        String value,
        String field
    ) {
        String allowed = Arrays.toString(enumType.getEnumConstants());
        if (isBlank(value)) {
            throw ApiException.badRequest(field + " is required, one of " + allowed + ".");
        }
        try {
            return Enum.valueOf(enumType, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw ApiException.badRequest(
                "Invalid " + field + " '" + value + "', expected one of " + allowed + "."
            );
        }
    }

    private static LocalDate parsePastDate(String value, String field) {
        LocalDate date;
        try {
            date = LocalDate.parse(required(value, field));
        } catch (DateTimeParseException e) {
            throw ApiException.badRequest(field + " must be a date in YYYY-MM-DD format.");
        }
        if (date.isAfter(LocalDate.now())) {
            throw ApiException.badRequest(field + " cannot be in the future.");
        }
        return date;
    }

    private static String required(String value, String field) {
        if (isBlank(value)) throw ApiException.badRequest(field + " is required.");
        return value.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
