package org.vers.backend.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class DashboardResourceTest {
    @Test
    void testGetDashboard() {
        given()
        .when()
            .get("/dashboard")
        .then()
            .statusCode(200)
            .body(containsString("Welcome to the Vital Events Registration System!"));
    }
} 