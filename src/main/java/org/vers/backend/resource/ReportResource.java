package org.vers.backend.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.vers.backend.dto.EventResponse;
import org.vers.backend.dto.ReportFilterOptions;
import org.vers.backend.dto.ReportResponse;
import org.vers.backend.enums.EventStatus;
import org.vers.backend.enums.EventType;
import org.vers.backend.enums.Gender;
import org.vers.backend.security.CurrentUser;
import org.vers.backend.service.EventService;
import org.vers.backend.service.ReportService;

/** Reviewing (approving/rejecting) events and generating reports. */
@Path("/official")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({ "ADMIN", "OFFICIAL" })
public class ReportResource {

    @Inject
    EventService eventService;

    @Inject
    ReportService reportService;

    @Inject
    CurrentUser currentUser;

    /** Events to review; defaults to all statuses when {@code status} is omitted. */
    @GET
    @Path("/events")
    public List<EventResponse> listEvents(
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

    @POST
    @Path("/events/{id}/approve")
    @Consumes(MediaType.WILDCARD) // no body needed
    @Transactional
    public EventResponse approve(@PathParam("id") Long id) {
        return EventResponse.from(eventService.approve(id, currentUser.get()));
    }

    @POST
    @Path("/events/{id}/reject")
    @Transactional
    public EventResponse reject(@PathParam("id") Long id, RejectRequest request) {
        String reason = request == null ? null : request.reason;
        return EventResponse.from(eventService.reject(id, currentUser.get(), reason));
    }

    /** Event statistics. All filters are optional and combine with AND. */
    @GET
    @Path("/reports")
    public ReportResponse report(
        @QueryParam("type") String type,
        @QueryParam("status") String status,
        @QueryParam("gender") String gender,
        @QueryParam("region") String region,
        @QueryParam("registrationYear") Integer registrationYear
    ) {
        return reportService.summarize(
            EventService.parseOptionalEnum(EventType.class, type, "type"),
            EventService.parseOptionalEnum(EventStatus.class, status, "status"),
            EventService.parseOptionalEnum(Gender.class, gender, "gender"),
            region == null || region.isBlank() ? null : region.trim(),
            registrationYear
        );
    }

    @GET
    @Path("/reports/filters")
    public ReportFilterOptions reportFilters() {
        return reportService.filterOptions();
    }

    public static class RejectRequest {

        public String reason;
    }
}
