package org.vers.backend.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.vers.backend.entity.User;
import org.vers.backend.enums.Role;
import org.vers.backend.repository.UserRepository;

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
        given()
            .contentType(ContentType.JSON)
            .body(new LoginResource.LoginRequest("loginuser", "testpass"))
        .when()
            .post("/login")
        .then()
            .statusCode(200)
            .body("token", notNullValue())
            .body("role", equalTo("REGISTRAR"))
            .body("username", equalTo("loginuser"));
    }

    @Test
    void testLoginWrongPassword() {
        given()
            .contentType(ContentType.JSON)
            .body(new LoginResource.LoginRequest("loginuser", "wrongpass"))
        .when()
            .post("/login")
        .then()
            .statusCode(401)
            .body("error", equalTo("Invalid username or password."));
    }

    @Test
    void testLoginUserNotFoundLooksLikeWrongPassword() {
        given()
            .contentType(ContentType.JSON)
            .body(new LoginResource.LoginRequest("nouser", "testpass"))
        .when()
            .post("/login")
        .then()
            .statusCode(401)
            .body("error", equalTo("Invalid username or password."));
    }

    @Test
    void testLoginEmptyBody() {
        given()
            .contentType(ContentType.JSON)
            .body("{}")
        .when()
            .post("/login")
        .then()
            .statusCode(401);
    }

    @Test
    void testSeededUsersCanLogInAndUseTheirToken() {
        // Signing and verification must agree on key and issuer
        given().auth().oauth2(TestSupport.adminToken()).when().get("/me")
            .then().statusCode(200).body("role", equalTo("ADMIN"));
        given().auth().oauth2(TestSupport.registrarToken()).when().get("/me")
            .then().statusCode(200).body("role", equalTo("REGISTRAR"));
        given().auth().oauth2(TestSupport.officialToken()).when().get("/me")
            .then().statusCode(200).body("role", equalTo("OFFICIAL"));
    }

    @Test
    void testProtectedEndpointWithoutTokenIs401() {
        given().when().get("/me").then().statusCode(401);
    }
}
