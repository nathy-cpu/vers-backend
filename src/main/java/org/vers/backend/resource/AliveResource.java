package org.vers.backend.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/test-alive")
public class AliveResource {
    @GET
    public String alive() {
        return "alive";
    }
} 