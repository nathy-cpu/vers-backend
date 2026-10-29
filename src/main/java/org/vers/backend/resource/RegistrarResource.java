package org.vers.backend.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.vers.backend.dto.EventRequest;
import org.vers.backend.dto.EventResponse;
import org.vers.backend.enums.EventStatus;
import org.vers.backend.enums.EventType;
import org.vers.backend.exception.ApiException;
import org.vers.backend.security.CurrentUser;
import org.vers.backend.service.EventService;

@Path("/registrar")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({ "REGISTRAR", "ADMIN" })
public class RegistrarResource {

    @Inject
    EventService eventService;

    @Inject
    CurrentUser currentUser;

    @POST
    @Path("/register-event")
    @Transactional
    public Response registerEvent(EventRequest eventRequest) {
        if (eventRequest == null) throw ApiException.badRequest("Request body is required.");
        var event = eventService.register(eventRequest, currentUser.get());
        return Response.status(Response.Status.CREATED)
            .entity(EventResponse.from(event))
            .build();
    }

    /** Search events to edit. {@code name} matches the child, deceased or either spouse. */
    @GET
    @Path("/events")
    public List<EventResponse> searchEvents(
        @QueryParam("type") String type,
        @QueryParam("status") String status,
        @QueryParam("name") String name
    ) {
        return eventService
            .search(
                EventService.parseOptionalEnum(EventType.class, type, "type"),
                EventService.parseOptionalEnum(EventStatus.class, status, "status"),
                name
            )
            .stream()
            .map(EventResponse::from)
            .toList();
    }

    @GET
    @Path("/events/{id}")
    public EventResponse getEvent(@PathParam("id") Long id) {
        return EventResponse.from(eventService.getById(id));
    }

    /** Edits the details; the event returns to PENDING for another review. */
    @PUT
    @Path("/events/{id}")
    @Transactional
    public EventResponse updateEvent(@PathParam("id") Long id, EventRequest eventRequest) {
        if (eventRequest == null) throw ApiException.badRequest("Request body is required.");
        return EventResponse.from(eventService.update(id, eventRequest));
    }
}
