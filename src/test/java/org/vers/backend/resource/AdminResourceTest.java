package org.vers.backend.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.vers.backend.entity.User;
import org.vers.backend.enums.Role;
import org.vers.backend.repository.UserRepository;
import jakarta.inject.Inject;
import org.vers.backend.security.JwtUtils;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminResourceTest {

    @Inject
    UserRepository userRepository;

    String adminToken;

    @BeforeAll
    @Transactional
    void setup() {
        // Ensure test admin user exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User("admin", "$2a$10$7QJ8QJ8QJ8QJ8QJ8QJ8QJ8QJ8QJ8QJ8QJ8QJ8QJ8QJ8QJ8QJ8Q", "admin@example.com", Role.ADMIN);
            admin.persist();
        }
        adminToken = JwtUtils.generateToken("admin", "ADMIN");
    }

    @Test
    @Transactional
    void testAddUser() {
        var request = new AdminResource.AddUserRequest();
        request.username = "testuser";
        request.password = "testpass";
        request.role = "REGISTRAR";
        request.email = "testuser@example.com";

        given()
            .auth().oauth2(adminToken)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/admin/add-user")
        .then()
            .statusCode(201)
            .body("message", equalTo("User created successfully."));
    }

    @Test
    void testSearchUser() {
        var request = new AdminResource.RemoveUserRequest();
        request.username = "testuser";

        given()
            .auth().oauth2(adminToken)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/admin/search-user")
        .then()
            .statusCode(200)
            .body("username", equalTo("testuser"));
    }

    @Test
    @Transactional
    void testRemoveUser() {
        var request = new AdminResource.RemoveUserRequest();
        request.username = "testuser";

        given()
            .auth().oauth2(adminToken)
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .delete("/admin/remove-user")
        .then()
            .statusCode(200)
            .body("message", equalTo("User removed successfully."));
    }
} 