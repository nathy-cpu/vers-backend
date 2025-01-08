package org.vers.backend.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/")
public class RootResource {

    @GET
    public Response redirectToLogin() {
        return Response.temporaryRedirect(URI.create("/login")).build();
    }
}
