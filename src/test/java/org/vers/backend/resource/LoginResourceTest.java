package org.vers.backend.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.vers.backend.entity.User;
import org.vers.backend.enums.Role;
import org.vers.backend.repository.UserRepository;
import jakarta.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginResourceTest {

    @Inject
    UserRepository userRepository;

    @BeforeAll
    @Transactional
    void setup() {
        if (userRepository.findByUsername("loginuser").isEmpty()) {
            User user = new User("loginuser", "testpass", "loginuser@example.com", Role.REGISTRAR);
            user.persist();
        }
    }

    @Test
    void testLoginSuccess() {
        var request = new LoginResource.LoginRequest("loginuser", "testpass");
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/login")
        .then()
            .statusCode(200)
            .body("token", notNullValue())
            .body("role", equalTo("REGISTRAR"));
    }

    @Test
    void testLoginWrongPassword() {
        var request = new LoginResource.LoginRequest("loginuser", "wrongpass");
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/login")
        .then()
            .statusCode(401)
            .body("error", equalTo("Wrong password!"));
    }

    @Test
    void testLoginUserNotFound() {
        var request = new LoginResource.LoginRequest("nouser", "testpass");
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/login")
        .then()
            .statusCode(404)
            .body("error", equalTo("Username not found!"));
    }

    @Test
    void testAdminLoginSuccess() {
        var request = new LoginResource.LoginRequest("admin", "adminpass");
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/login")
        .then()
            .statusCode(200)
            .body("token", notNullValue())
            .body("role", equalTo("ADMIN"));
    }

    @Test
    void testRegistrarLoginSuccess() {
        var request = new LoginResource.LoginRequest("registrar", "registrarpass");
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/login")
        .then()
            .statusCode(200)
            .body("token", notNullValue())
            .body("role", equalTo("REGISTRAR"));
    }

    @Test
    void testOfficialLoginSuccess() {
        var request = new LoginResource.LoginRequest("official", "officialpass");
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/login")
        .then()
            .statusCode(200)
            .body("token", notNullValue())
            .body("role", equalTo("OFFICIAL"));
    }
} 