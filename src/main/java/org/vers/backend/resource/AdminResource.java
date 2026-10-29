package org.vers.backend.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.vers.backend.dto.MessageResponse;
import org.vers.backend.dto.UserResponse;
import org.vers.backend.exception.ApiException;
import org.vers.backend.repository.UserRepository;
import org.vers.backend.security.CurrentUser;
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

    @Inject
    CurrentUser currentUser;

    @POST
    @Path("/add-user")
    @Transactional
    public Response addUser(AddUserRequest addUserRequest) {
        if (addUserRequest == null) throw ApiException.badRequest("Request body is required.");
        userService.createUser(
            addUserRequest.username,
            addUserRequest.password,
            addUserRequest.email,
            addUserRequest.role
        );
        return Response.status(Response.Status.CREATED)
            .entity(new MessageResponse("User created successfully."))
            .build();
    }

    /** All users, optionally only those whose username contains {@code search}. */
    @GET
    @Path("/users")
    public List<UserResponse> listUsers(@QueryParam("search") String search) {
        return userRepository
            .searchByUsername(search == null ? "" : search.trim())
            .stream()
            .map(UserResponse::new)
            .toList();
    }

    @DELETE
    @Path("/remove-user")
    @Transactional
    public MessageResponse removeUser(RemoveUserRequest removeUserRequest) {
        if (removeUserRequest == null || removeUserRequest.username == null) {
            throw ApiException.badRequest("username is required.");
        }
        userService.removeUser(removeUserRequest.username, currentUser.get());
        return new MessageResponse("User removed successfully.");
    }

    @POST
    @Path("/search-user")
    public UserResponse searchUser(RemoveUserRequest removeUserRequest) {
        if (removeUserRequest == null || removeUserRequest.username == null) {
            throw ApiException.badRequest("username is required.");
        }
        return userRepository
            .findByUsername(removeUserRequest.username)
            .map(UserResponse::new)
            .orElseThrow(() -> ApiException.notFound("User not found."));
    }

    // DTO for Add User Request
    public static class AddUserRequest {

        public String username;
        public String password;
        public String role;
        public String email;
    }

    // DTO for Remove/Search User Request
    public static class RemoveUserRequest {

        public String username;
    }
}
