package org.vers.backend.resource;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.vers.backend.entity.User;
import org.vers.backend.utils.PasswordUtils;

@Path("/auth")
public class AuthResource {

    @POST
    @Path("/register")
    @Transactional
    public Response register(User user) {
        if (User.findByUsername(user.username) != null) {
            return Response.status(Response.Status.CONFLICT)
                .entity("Username already exists")
                .build();
        }

        user.passwordHash = PasswordUtils.hashPassword(user.passwordHash); // Hash the password
        user.persist();
        return Response.ok("User registered successfully").build();
    }
}
