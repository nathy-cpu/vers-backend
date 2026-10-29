package org.vers.backend.security;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.vers.backend.entity.User;
import org.vers.backend.exception.ApiException;
import org.vers.backend.repository.UserRepository;

/** The user the current request's token belongs to. */
@RequestScoped
public class CurrentUser {

    @Inject
    SecurityIdentity identity;

    @Inject
    UserRepository userRepository;

    public User get() {
        // A token can outlive its user, e.g. after an admin removes the account
        return userRepository
            .findByUsername(identity.getPrincipal().getName())
            .orElseThrow(() ->
                new ApiException(
                    Response.Status.UNAUTHORIZED,
                    "Your account no longer exists."
                )
            );
    }
}
