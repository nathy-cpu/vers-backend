package org.vers.backend.resource;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import java.util.Map;
import java.util.UUID;
import org.vers.backend.dto.EventRequest;
import org.vers.backend.dto.LocationDTO;

/** Shared helpers. Tests share one database, so names are made unique. */
final class TestSupport {

    private TestSupport() {}

    static String token(String username, String password) {
        return given()
            .contentType(ContentType.JSON)
            .body(new LoginResource.LoginRequest(username, password))
            .when()
            .post("/login")
            .then()
            .statusCode(200)
            .extract()
            .path("token");
    }

    static String adminToken() {
        return token("admin", "adminpass");
    }

    static String registrarToken() {
        return token("registrar", "registrarpass");
    }

    static String officialToken() {
        return token("official", "officialpass");
    }

    static String unique(String prefix) {
        return prefix + UUID.randomUUID().toString().substring(0, 8);
    }

    /** Creates a user with password "password123" and returns its username. */
    static String createUser(String role) {
        String username = unique("user");
        given()
            .auth()
            .oauth2(adminToken())
            .contentType(ContentType.JSON)
            .body(
                Map.of(
                    "username", username,
                    "password", "password123",
                    "email", username + "@example.com",
                    "role", role
                )
            )
            .when()
            .post("/admin/add-user")
            .then()
            .statusCode(201);
        return username;
    }

    static EventRequest birth(String region) {
        EventRequest r = new EventRequest();
        r.type = "BIRTH";
        r.childName = "Abel Kebede Alemu";
        r.fatherName = "Kebede Alemu";
        r.motherName = "Almaz Tesfaye";
        r.gender = "MALE";
        r.birthWeight = 3.2f;
        r.dateOfBirth = "2024-01-01";
        r.location = new LocationDTO(region, "Zone 1", "Woreda 1");
        return r;
    }

    static EventRequest death(String region) {
        EventRequest r = new EventRequest();
        r.type = "DEATH";
        r.deceasedName = "Tadesse Bekele Haile";
        r.gender = "FEMALE";
        r.causeOfDeath = "Natural causes";
        r.certifierName = "Dr. Hana Girma";
        r.dateOfDeath = "2024-02-01";
        r.placeOfDeath = "Black Lion Hospital";
        r.location = new LocationDTO(region, "Zone 1", "Woreda 1");
        return r;
    }

    static EventRequest marriage(String region) {
        EventRequest r = new EventRequest();
        r.type = "MARRIAGE";
        r.maleSpouseName = "Dawit Mekonnen";
        r.femaleSpouseName = "Selam Assefa";
        r.witnessOneName = "Yonas Tilahun";
        r.witnessTwoName = "Meron Desta";
        r.certifierName = "Kebele Officer";
        r.dateOfMarriage = "2024-03-01";
        r.location = new LocationDTO(region, "Zone 1", "Woreda 1");
        return r;
    }

    static EventRequest divorce(String region) {
        EventRequest r = new EventRequest();
        r.type = "DIVORCE";
        r.maleSpouseName = "Dawit Mekonnen";
        r.femaleSpouseName = "Selam Assefa";
        r.courtName = "Federal First Instance Court";
        r.dateOfDivorce = "2024-04-01";
        r.location = new LocationDTO(region, "Zone 1", "Woreda 1");
        return r;
    }

    /** Registers the event and returns its id. */
    static long register(String token, EventRequest request) {
        return given()
            .auth()
            .oauth2(token)
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/registrar/register-event")
            .then()
            .statusCode(201)
            .extract()
            .jsonPath()
            .getLong("id");
    }
}
