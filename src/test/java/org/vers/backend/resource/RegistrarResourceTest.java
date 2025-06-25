package org.vers.backend.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.vers.backend.entity.User;
import org.vers.backend.enums.Role;
import org.vers.backend.repository.UserRepository;
import jakarta.inject.Inject;
import org.vers.backend.security.JwtUtils;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RegistrarResourceTest {

    @Inject
    UserRepository userRepository;

    @Test
    @Transactional
    void testRegisterBirthEvent() {
        // Simulate login as registrar to get a valid JWT
        String registrarToken = given()
            .contentType(ContentType.JSON)
            .body(new LoginResource.LoginRequest("registrar", "registrarpass"))
        .when()
            .post("/login")
        .then()
            .statusCode(200)
            .extract().path("token");

        var eventRequest = new RegistrarResource.EventRequest();
        eventRequest.type = "BIRTH";
        eventRequest.childName = "Test Child Name";
        eventRequest.fatherName = "Father Name";
        eventRequest.motherName = "Mother Name";
        eventRequest.gender = "MALE";
        eventRequest.birthWeight = 3.2f;
        eventRequest.dateOfBirth = "2020-01-01";
        eventRequest.location = new RegistrarResource.LocationDTO("TestRegion", "TestZone", "TestWoreda");
        eventRequest.phoneNumber = "+251911000000";

        given()
            .auth().oauth2(registrarToken)
            .contentType(ContentType.JSON)
            .body(eventRequest)
        .when()
            .post("/registrar/register-event")
        .then()
            .statusCode(201)
            .body("message", equalTo("Event registered successfully."));
    }
} 