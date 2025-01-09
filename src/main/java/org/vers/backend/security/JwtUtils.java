package org.vers.backend.security;

import io.smallrye.jwt.build.Jwt;
import java.time.Duration;
import java.util.Set;

public class JwtUtils {

    public static String generateToken(String username, String role) {
        return Jwt.issuer("http://localhost:8080")
            .upn(username)
            .groups(Set.of(role))
            .expiresIn(Duration.ofHours(2))
            .sign();
    }
}
