package org.vers.backend.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.util.Map;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class AdminResourceTest {

    private static Map<String, String> user(String username, String password, String email, String role) {
        return Map.of("username", username, "password", password, "email", email, "role", role);
    }

    private static io.restassured.response.ValidatableResponse addUser(Map<String, String> body) {
        return given()
            .auth().oauth2(TestSupport.adminToken())
            .contentType(ContentType.JSON)
            .body(body)
        .when()
            .post("/admin/add-user")
        .then();
    }

    private static io.restassured.response.ValidatableResponse removeUser(String token, String username) {
        return given()
            .auth().oauth2(token)
            .contentType(ContentType.JSON)
            .body(Map.of("username", username))
        .when()
            .delete("/admin/remove-user")
        .then();
    }

    @Test
    void testAddSearchAndRemoveUser() {
        String username = TestSupport.unique("added");
        addUser(user(username, "password123", username + "@example.com", "REGISTRAR"))
            .statusCode(201)
            .body("message", equalTo("User created successfully."));

        given()
            .auth().oauth2(TestSupport.adminToken())
            .contentType(ContentType.JSON)
            .body(Map.of("username", username))
        .when()
            .post("/admin/search-user")
        .then()
            .statusCode(200)
            .body("username", equalTo(username))
            .body("role", equalTo("REGISTRAR"));

        given()
            .auth().oauth2(TestSupport.adminToken())
        .when()
            .get("/admin/users?search=" + username.substring(2).toUpperCase())
        .then()
            .statusCode(200)
            .body("username", hasItem(username));

        removeUser(TestSupport.adminToken(), username)
            .statusCode(200)
            .body("message", equalTo("User removed successfully."));
        removeUser(TestSupport.adminToken(), username).statusCode(404);
    }

    @Test
    void testRoleIsCaseInsensitive() {
        String username = TestSupport.unique("lower");
        addUser(user(username, "password123", username + "@example.com", "official"))
            .statusCode(201);
    }

    @Test
    void testInvalidInputIsRejectedWith400() {
        String username = TestSupport.unique("bad");
        addUser(user(username, "password123", username + "@example.com", "SUPERUSER"))
            .statusCode(400).body("error", containsString("Invalid role"));
        addUser(user(username, "short", username + "@example.com", "REGISTRAR"))
            .statusCode(400).body("error", containsString("at least 8"));
        addUser(user(username, "password123", "not-an-email", "REGISTRAR"))
            .statusCode(400).body("error", equalTo("Invalid email address."));
        addUser(user("abc", "password123", username + "@example.com", "REGISTRAR"))
            .statusCode(400).body("error", containsString("Username"));
    }

    @Test
    void testDuplicatesAreRejectedWith409() {
        String username = TestSupport.createUser("REGISTRAR");
        addUser(user(username, "password123", "other-" + username + "@example.com", "REGISTRAR"))
            .statusCode(409).body("error", equalTo("User already exists."));
        addUser(user(TestSupport.unique("dup"), "password123", username + "@example.com", "REGISTRAR"))
            .statusCode(409).body("error", containsString("email"));
    }

    @Test
    void testUserWithEventsCannotBeRemoved() {
        String username = TestSupport.createUser("REGISTRAR");
        TestSupport.register(TestSupport.token(username, "password123"), TestSupport.birth("Afar"));
        removeUser(TestSupport.adminToken(), username)
            .statusCode(409)
            .body("error", containsString("cannot be removed"));
    }

    @Test
    void testAdminCannotRemoveSelf() {
        removeUser(TestSupport.adminToken(), "admin").statusCode(400);
    }

    @Test
    void testNonAdminsAreForbidden() {
        given().auth().oauth2(TestSupport.registrarToken())
            .when().get("/admin/users").then().statusCode(403);
        given().auth().oauth2(TestSupport.officialToken())
            .when().get("/admin/users").then().statusCode(403);
    }
}
