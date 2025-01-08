package org.vers.backend.resource;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Optional;
import org.vers.backend.entity.User;
import org.vers.backend.repository.UserRepository;

@Path("/login")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

    // DTO for login request
    public static class LoginRequest {

        public String username;
        public String password;

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    // DTO for login response
    public static class LoginResponse {

        public String username;
        public String role;

        public LoginResponse(String username, String role) {
            this.username = username;
            this.role = role;
        }
    }

    // DTO for error response
    public static class ErrorResponse {

        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }

    @Inject
    UserRepository userRepository;

    @POST
    public Response login(LoginRequest loginRequest) {
        Optional<User> user = userRepository.findByUsername(
            loginRequest.username
        );

        if (user.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("Username not found!"))
                .build();
        }

        if (
            !BCrypt.verifyer()
                .verify(
                    loginRequest.password.toCharArray(),
                    user.get().password
                )
                .verified
        ) {
            return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new ErrorResponse("Wrong password!"))
                .build();
        } else {
            return Response.ok(
                new LoginResponse(
                    user.get().username,
                    user.get().role.toString()
                )
            ).build();
        }
    }
}
