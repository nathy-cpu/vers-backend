package org.vers.backend.dto;

import org.vers.backend.entity.User;

public class UserResponse {

    public String username;
    public String email;
    public String role;
    public String createdAt;

    public UserResponse(User user) {
        this.username = user.username;
        this.email = user.email;
        this.role = user.role.toString();
        this.createdAt = user.createdAt.toString();
    }
}
