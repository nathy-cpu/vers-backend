package org.vers.backend.security;

import io.smallrye.jwt.build.Jwt;
import java.util.Set;

public class JwtUtils {

    // Issuer and lifespan come from smallrye.jwt.new-token.* in application.properties,
    // so signing and verification can never disagree on the issuer.
    public static String generateToken(String username, String role) {
        return Jwt.upn(username).groups(Set.of(role)).sign();
    }
}
