package org.vers.backend.resource;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.vers.backend.entity.Event;

@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {

    // GET all events
    @GET
    public List<Event> getAllEvents() {
        return Event.listAll();
    }

    // GET a specific event by ID
    @GET
    @Path("/{id}")
    public Event getEventById(@PathParam("id") Long id) {
        return Event.findById(id);
    }

    // POST a new event
    @POST
    @Transactional
    public Response createEvent(Event event) {
        event.persist();
        return Response.ok(event).build();
    }

    // PUT to update an event
    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateEvent(@PathParam("id") Long id, Event updatedEvent) {
        Event event = Event.findById(id);
        if (event == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        event.type = updatedEvent.type;
        event.createdAt = updatedEvent.createdAt;
        event.location = updatedEvent.location;
        event.persist();
        return Response.ok(event).build();
    }

    // DELETE an event
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteEvent(@PathParam("id") Long id) {
        boolean deleted = Event.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
