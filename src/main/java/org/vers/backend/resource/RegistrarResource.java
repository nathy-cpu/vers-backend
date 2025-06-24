package org.vers.backend.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.time.LocalDate;
import java.util.StringTokenizer;
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
import org.vers.backend.repository.BirthEventRepository;
import org.vers.backend.repository.DeathEventRepository;
import org.vers.backend.repository.DivorceEventRepository;
import org.vers.backend.repository.EventRepository;
import org.vers.backend.repository.MarriageEventRepository;
import org.vers.backend.repository.LocationRepository;
import org.vers.backend.repository.UserRepository;

@Path("/registrar")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({ "REGISTRAR", "ADMIN" })
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

    @Inject
    LocationRepository locationRepository;

    @POST
    @Path("/register-event")
    @Transactional
    public Response registerEvent(
        EventRequest eventRequest,
        @Context SecurityContext securityContext
    ) {
        Event event;

        // Check for existing location
        Location location = locationRepository
            .findByCompositeKey(
                eventRequest.location.region,
                eventRequest.location.zone,
                eventRequest.location.woreda
            )
            .orElseGet(() -> {
                Location newLoc = new Location(
            eventRequest.location.region,
            eventRequest.location.zone,
            eventRequest.location.woreda
        );
                newLoc.persist();
                return newLoc;
            });

        User registrar = userRepository
            .findByUsername(securityContext.getUserPrincipal().getName())
            .get();
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

                Person child = new Person();
                String[] nameParts = eventRequest.childName.trim().split("\\s+");
                if (nameParts.length != 3) {
                    return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Child name must have first, middle, and last name."))
                        .build();
                }
                child.firstName = nameParts[0];
                child.middleName = nameParts[1];
                child.lastName = nameParts[2];
                child.dateOfBirth = LocalDate.parse(eventRequest.dateOfBirth);
                child.gender = Gender.valueOf(eventRequest.gender);
                child.phoneNumber = eventRequest.phoneNumber;
                child.persist();

                break;
            case "DEATH":
                event = new DeathEvent(
                    eventRequest.deceasedName,
                    Gender.valueOf(eventRequest.gender),
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

        // Check for existing location
        Location location = locationRepository
            .findByCompositeKey(
                eventRequest.location.region,
                eventRequest.location.zone,
                eventRequest.location.woreda
            )
            .orElseGet(() -> {
                Location newLoc = new Location(
                    eventRequest.location.region,
                    eventRequest.location.zone,
                    eventRequest.location.woreda
                );
                newLoc.persist();
                return newLoc;
            });

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
                    .stream().findFirst().orElse(null);
                break;
            case "DIVORCE":
                existingEvent = divorceEventRepository
                    .findBySpouseName(eventRequest.maleSpouseName)
                    .stream().findFirst().orElse(null);
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
        public String phoneNumber;
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
