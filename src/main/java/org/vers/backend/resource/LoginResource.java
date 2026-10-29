package org.vers.backend.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;
import java.util.Optional;
import org.vers.backend.dto.ErrorResponse;
import org.vers.backend.entity.User;
import org.vers.backend.security.JwtUtils;
import org.vers.backend.service.UserService;

@Path("/login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

    // DTO for login request
    public static class LoginRequest {

        public String username;
        public String password;

        public LoginRequest() {}

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    @Inject
    UserService userService;

    @POST
    public Response login(LoginRequest loginRequest) {
        Optional<User> user = Optional.empty();
        if (
            loginRequest != null &&
            loginRequest.username != null &&
            loginRequest.password != null
        ) {
            user = userService.authenticate(
                loginRequest.username,
                loginRequest.password
            );
        }

        // Same answer for unknown user and wrong password, so usernames can't be probed
        if (user.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse("Invalid username or password."))
                .build();
        }

        String role = user.get().role.toString();
        String token = JwtUtils.generateToken(user.get().username, role);
        return Response.ok(
            Map.of("token", token, "role", role, "username", user.get().username)
        ).build();
    }
}
