package org.vers.backend.resource;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.vers.backend.dto.MessageResponse;
import org.vers.backend.dto.UserResponse;
import org.vers.backend.entity.User;
import org.vers.backend.exception.ApiException;
import org.vers.backend.security.CurrentUser;
import org.vers.backend.service.UserService;

/** The logged-in user's own account, for every role. */
@Path("/me")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class AccountResource {

    @Inject
    CurrentUser currentUser;

    @GET
    public UserResponse me() {
        return new UserResponse(currentUser.get());
    }

    @PUT
    @Path("/password")
    @Transactional
    public MessageResponse changePassword(ChangePasswordRequest request) {
        User user = currentUser.get();
        if (
            request == null ||
            request.currentPassword == null ||
            !user.verifyPassword(request.currentPassword)
        ) {
            throw ApiException.badRequest("Current password is incorrect.");
        }
        UserService.checkPasswordStrength(request.newPassword);
        user.setPassword(request.newPassword);
        return new MessageResponse("Password changed successfully.");
    }

    public static class ChangePasswordRequest {

        public String currentPassword;
        public String newPassword;
    }
}
