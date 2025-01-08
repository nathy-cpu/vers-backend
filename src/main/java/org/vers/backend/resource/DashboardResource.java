package org.vers.backend.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * REST endpoint to serve the Svelte dashboard page.
 */
@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DashboardResource {

    /**
     * Handles GET requests to the dashboard.
     *
     * @return A welcome message for unauthenticated users.
     */
    @GET
    public Response getDashboard() {
        String welcomeMessage =
            "Welcome to the Vital Events Registration System! Please log in or register to access services.";
        return Response.ok(welcomeMessage).build();
    }
}
