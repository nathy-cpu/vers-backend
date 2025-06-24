package org.vers.backend.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.vers.backend.entity.User;
import org.vers.backend.enums.Role;
import org.vers.backend.repository.UserRepository;
import org.vers.backend.service.UserService;

@Path("/admin")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({ "ADMIN" })
public class AdminResource {

    @Inject
    UserRepository userRepository;

    @Inject
    UserService userService;

    @POST
    @Path("/add-user")
    @Transactional
    public Response addUser(AddUserRequest addUserRequest) {
        // Check if the user already exists
        if (
            userRepository.findByUsername(addUserRequest.username).isPresent()
        ) {
            return Response.status(Response.Status.CONFLICT)
                .entity(new ErrorResponse("User already exists."))
                .build();
        }

        // Create a new user
        User user = new User(
            addUserRequest.username,
            addUserRequest.password,
            addUserRequest.email,
            Role.valueOf(addUserRequest.role)
        );

        userService.registerUser(user);

        return Response.status(Response.Status.CREATED)
            .entity(new SuccessResponse("User created successfully."))
            .build();
    }

    @DELETE
    @Path("/remove-user")
    @Transactional
    public Response removeUser(RemoveUserRequest removeUserRequest) {
        User user = userRepository
            .findByUsername(removeUserRequest.username)
            .orElse(null);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("User not found."))
                .build();
        } else {
            user.delete();

            return Response.ok(
                new SuccessResponse("User removed successfully.")
            ).build();
        }
    }

    @POST
    @Path("/search-user")
    public Response searchUser(RemoveUserRequest removeUserRequest) {
        User user = userRepository
            .findByUsername(removeUserRequest.username)
            .orElse(null);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("User not found."))
                .build();
        } else {
            return Response.ok(new FoundUserResponse(user)).build();
        }
    }

    // DTO for Add User Request
    public static class AddUserRequest {

        public String username;
        public String password;
        public String role;
        public String email;
    }

    // DTO for search User Response
    public static class FoundUserResponse {

        public String username;
        public String email;
        public String role;
        public String createdAt;

        public FoundUserResponse(User foundUser) {
            this.username = foundUser.username;
            this.email = foundUser.email;
            this.role = foundUser.role.toString();
            this.createdAt = foundUser.createdAt.toString();
        }
    }

    // DTO for Remove User Request
    public static class RemoveUserRequest {

        public String username;
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
