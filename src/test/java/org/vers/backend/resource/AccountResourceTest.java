package org.vers.backend.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.util.Map;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class AccountResourceTest {

    @Test
    void testMe() {
        String username = TestSupport.createUser("OFFICIAL");
        given()
            .auth().oauth2(TestSupport.token(username, "password123"))
        .when()
            .get("/me")
        .then()
            .statusCode(200)
            .body("username", equalTo(username))
            .body("email", equalTo(username + "@example.com"))
            .body("role", equalTo("OFFICIAL"))
            .body("password", nullValue());
    }

    @Test
    void testChangePassword() {
        String username = TestSupport.createUser("REGISTRAR");
        String token = TestSupport.token(username, "password123");

        given()
            .auth().oauth2(token)
            .contentType(ContentType.JSON)
            .body(Map.of("currentPassword", "wrong-password", "newPassword", "newpassword1"))
        .when()
            .put("/me/password")
        .then()
            .statusCode(400)
            .body("error", equalTo("Current password is incorrect."));

        given()
            .auth().oauth2(token)
            .contentType(ContentType.JSON)
            .body(Map.of("currentPassword", "password123", "newPassword", "short"))
        .when()
            .put("/me/password")
        .then()
            .statusCode(400)
            .body("error", containsString("at least 8"));

        given()
            .auth().oauth2(token)
            .contentType(ContentType.JSON)
            .body(Map.of("currentPassword", "password123", "newPassword", "newpassword1"))
        .when()
            .put("/me/password")
        .then()
            .statusCode(200);

        TestSupport.token(username, "newpassword1");
    }
}
