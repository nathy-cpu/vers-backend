package org.vers.backend.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class AliveResourceTest {
    @Test
    void testAlive() {
        given()
        .when()
            .get("/test-alive")
        .then()
            .statusCode(200)
            .body(equalTo("alive"));
    }
} 