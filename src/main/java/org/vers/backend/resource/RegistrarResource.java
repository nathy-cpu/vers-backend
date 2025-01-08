package org.vers.backend.resource;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import org.vers.backend.entity.BirthEvent;
import org.vers.backend.entity.DeathEvent;
import org.vers.backend.entity.DivorceEvent;
import org.vers.backend.entity.Event;
import org.vers.backend.entity.Location;
import org.vers.backend.entity.MarriageEvent;
import org.vers.backend.entity.User;
import org.vers.backend.enums.EventStatus;
import org.vers.backend.enums.EventType;
import org.vers.backend.enums.Gender;
import org.vers.backend.repository.BirthEventRepository;
import org.vers.backend.repository.DeathEventRepository;
import org.vers.backend.repository.DivorceEventRepository;
import org.vers.backend.repository.EventRepository;
import org.vers.backend.repository.MarriageEventRepository;
import org.vers.backend.repository.UserRepository;

@Path("/registrar")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RegistrarResource {

    @Inject
    EventRepository eventRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    BirthEventRepository birthEventRepository;

    @Inject
    DeathEventRepository deathEventRepository;

    @Inject
    MarriageEventRepository marriageEventRepository;

    @Inject
    DivorceEventRepository divorceEventRepository;

    @POST
    @Path("/register-event")
    @Transactional
    public Response registerEvent(EventRequest eventRequest) {
        Event event;

        Location location = new Location(
            eventRequest.location.region,
            eventRequest.location.zone,
            eventRequest.location.woreda
        );

        User registrar = userRepository
            .findByUsername(eventRequest.registrar.username)
            .orElse(null);

        switch (eventRequest.type) {
            case "BIRTH":
                event = new BirthEvent(
                    eventRequest.childName,
                    eventRequest.fatherName,
                    eventRequest.motherName,
                    Gender.valueOf(eventRequest.gender),
                    eventRequest.birthWeight,
                    LocalDate.parse(eventRequest.dateOfBirth)
                );
                break;
            case "DEATH":
                event = new DeathEvent(
                    eventRequest.deceasedName,
                    eventRequest.causeOfDeath,
                    eventRequest.certifierName,
                    LocalDate.parse(eventRequest.dateOfDeath)
                );
                break;
            case "MARRIAGE":
                event = new MarriageEvent(
                    eventRequest.maleSpouseName,
                    eventRequest.femaleSpouseName,
                    eventRequest.witnessOneName,
                    eventRequest.witnessTwoName,
                    eventRequest.certifierName,
                    LocalDate.parse(eventRequest.dateOfMarriage)
                );
                break;
            case "DIVORCE":
                event = new DivorceEvent(
                    eventRequest.maleSpouseName,
                    eventRequest.femaleSpouseName,
                    eventRequest.courtName,
                    LocalDate.parse(eventRequest.dateOfDivorce)
                );
                break;
            default:
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid event type."))
                    .build();
        }

        event.location = location;
        event.registrar = registrar;
        event.persist();
        return Response.status(Response.Status.CREATED)
            .entity(new SuccessResponse("Event registered successfully."))
            .build();
    }

    @PUT
    @Path("/update-event")
    @Transactional
    public Response updateEvent(EventRequest eventRequest) {
        Event existingEvent;

        switch (eventRequest.type) {
            case "BIRTH":
                existingEvent = birthEventRepository
                    .findByChildName(eventRequest.childName)
                    .orElse(null);
                break;
            case "DEATH":
                existingEvent = deathEventRepository
                    .findByDeceasedName(eventRequest.deceasedName)
                    .orElse(null);
                break;
            case "MARRIAGE":
                existingEvent = marriageEventRepository
                    .findBySpouseName(eventRequest.maleSpouseName)
                    .getFirst();
                break;
            case "DIVORCE":
                existingEvent = divorceEventRepository
                    .findBySpouseName(eventRequest.maleSpouseName)
                    .getFirst();
                break;
            default:
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid event type."))
                    .build();
        }

        if (existingEvent == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("Event not found."))
                .build();
        }

        // Update common fields
        Location location = new Location(
            eventRequest.location.region,
            eventRequest.location.zone,
            eventRequest.location.woreda
        );

        existingEvent.location = location;
        existingEvent.type = EventType.valueOf(eventRequest.type);
        existingEvent.status = EventStatus.valueOf(eventRequest.status);

        User registrar = userRepository
            .findByUsername(eventRequest.registrar.username)
            .orElse(null);

        existingEvent.registrar = registrar;

        if (existingEvent instanceof BirthEvent birthEvent) {
            birthEvent.childName = eventRequest.childName;
            birthEvent.dateOfBirth = LocalDate.parse(eventRequest.dateOfBirth);
            birthEvent.fatherName = eventRequest.fatherName;
            birthEvent.motherName = eventRequest.motherName;
            birthEvent.birthWeight = eventRequest.birthWeight;
        } else if (existingEvent instanceof DeathEvent deathEvent) {
            deathEvent.deceasedName = eventRequest.deceasedName;
            deathEvent.dateOfDeath = LocalDate.parse(eventRequest.dateOfDeath);
            deathEvent.causeOfDeath = eventRequest.causeOfDeath;
            deathEvent.certifierName = eventRequest.certifierName;
        } else if (existingEvent instanceof MarriageEvent marriageEvent) {
            marriageEvent.maleSpouseName = eventRequest.maleSpouseName;
            marriageEvent.femaleSpouseName = eventRequest.femaleSpouseName;
            marriageEvent.witnessOneName = eventRequest.witnessOneName;
            marriageEvent.witnessTwoName = eventRequest.witnessTwoName;
            marriageEvent.certifierName = eventRequest.certifierName;
            marriageEvent.dateOfMarriage = LocalDate.parse(
                eventRequest.dateOfMarriage
            );
        } else if (existingEvent instanceof DivorceEvent divorceEvent) {
            divorceEvent.maleSpouseName = eventRequest.maleSpouseName;
            divorceEvent.femaleSpouseName = eventRequest.femaleSpouseName;
            divorceEvent.courtName = eventRequest.courtName;
            divorceEvent.dateOfDivorce = LocalDate.parse(
                eventRequest.dateOfDivorce
            );
        }

        existingEvent.persist();

        return Response.ok(
            new SuccessResponse("Event updated successfully.")
        ).build();
    }

    // DTO for Event Request
    public static class EventRequest {

        public String type;
        public String childName;
        public String dateOfBirth;
        public String fatherName;
        public String motherName;
        public String gender;
        public Float birthWeight;
        public String deceasedName;
        public String dateOfDeath;
        public String causeOfDeath;
        public String certifierName;
        public String maleSpouseName;
        public String femaleSpouseName;
        public String witnessOneName;
        public String witnessTwoName;
        public String courtName;
        public String dateOfMarriage;
        public String dateOfDivorce;
        public String dateOfRegistration;
        public LocationDTO location;
        public UserDTO registrar;
        public String status;
    }

    public static class LocationDTO {

        public String region;
        public String zone;
        public String woreda;

        public LocationDTO(String region, String zone, String woreda) {
            this.region = region;
            this.zone = zone;
            this.woreda = woreda;
        }
    }

    public static class UserDTO {

        public String username;
        public String role;

        public UserDTO(String username, String role) {
            this.username = username;
            this.role = role;
        }
    }

    // DTO for Error Response
    public static class ErrorResponse {

        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }

    // DTO for Success Response
    public static class SuccessResponse {

        public String message;

        public SuccessResponse(String message) {
            this.message = message;
        }
    }
}
