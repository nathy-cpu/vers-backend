package org.vers.backend.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import jakarta.inject.Inject;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.vers.backend.dto.EventRequest;
import org.vers.backend.repository.LocationRepository;

@QuarkusTest
public class RegistrarResourceTest {

    @Inject
    LocationRepository locationRepository;

    private static ValidatableResponse post(EventRequest request) {
        return given()
            .auth().oauth2(TestSupport.registrarToken())
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post("/registrar/register-event")
        .then();
    }

    private static ValidatableResponse put(long id, EventRequest request) {
        return given()
            .auth().oauth2(TestSupport.registrarToken())
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .put("/registrar/events/" + id)
        .then();
    }

    @Test
    void testRegisterBirthEvent() {
        EventRequest request = TestSupport.birth("Amhara");
        request.phoneNumber = "+251911000000";
        post(request)
            .statusCode(201)
            .body("id", notNullValue())
            .body("type", equalTo("BIRTH"))
            .body("status", equalTo("PENDING"))
            .body("registrar", equalTo("registrar"))
            .body("childName", equalTo("Abel Kebede Alemu"))
            .body("birthWeight", equalTo(3.2f))
            .body("phoneNumber", equalTo("+251911000000"))
            .body("location.region", equalTo("Amhara"));
    }

    @Test
    void testNewbornBornTodayWithoutPhoneNumber() {
        EventRequest request = TestSupport.birth("Amhara");
        request.dateOfBirth = LocalDate.now().toString();
        request.phoneNumber = null;
        request.gender = "female";
        post(request).statusCode(201).body("gender", equalTo("FEMALE"));
    }

    @Test
    void testInvalidBirthInput() {
        EventRequest request = TestSupport.birth("Amhara");
        request.phoneNumber = "12";
        post(request).statusCode(400).body("error", containsString("phoneNumber"));

        request = TestSupport.birth("Amhara");
        request.dateOfBirth = LocalDate.now().plusDays(1).toString();
        post(request).statusCode(400).body("error", equalTo("dateOfBirth cannot be in the future."));

        request = TestSupport.birth("Amhara");
        request.gender = "other";
        post(request).statusCode(400).body("error", containsString("Invalid gender"));

        request = TestSupport.birth("Amhara");
        request.fatherName = " ";
        post(request).statusCode(400).body("error", equalTo("fatherName is required."));

        request = TestSupport.birth("Amhara");
        request.location = null;
        post(request).statusCode(400).body("error", equalTo("location is required."));

        request = TestSupport.birth("Amhara");
        request.type = "ADOPTION";
        post(request).statusCode(400).body("error", containsString("Invalid type"));
    }

    @Test
    void testRejectedRequestDoesNotLeaveALocationBehind() {
        String region = TestSupport.unique("Region");
        EventRequest request = TestSupport.birth(region);
        request.childName = "Only Two";
        post(request)
            .statusCode(400)
            .body("error", equalTo("childName must have first, middle, and last name."));
        assertEquals(0, locationRepository.count("region", region));
    }

    @Test
    void testRegisterOtherEventTypes() {
        post(TestSupport.death("Oromia"))
            .statusCode(201)
            .body("placeOfDeath", equalTo("Black Lion Hospital"))
            .body("certifierName", equalTo("Dr. Hana Girma"))
            .body("gender", equalTo("FEMALE"));
        post(TestSupport.marriage("Oromia"))
            .statusCode(201)
            .body("name", equalTo("Dawit Mekonnen & Selam Assefa"))
            .body("witnessTwoName", equalTo("Meron Desta"));
        post(TestSupport.divorce("Oromia"))
            .statusCode(201)
            .body("courtName", equalTo("Federal First Instance Court"));

        EventRequest noPlace = TestSupport.death("Oromia");
        noPlace.placeOfDeath = null;
        post(noPlace).statusCode(400).body("error", equalTo("placeOfDeath is required."));
    }

    @Test
    void testSearchAndUpdateById() {
        EventRequest request = TestSupport.birth("Tigray");
        String name = TestSupport.unique("Liya") + " Haile Gebre";
        request.childName = name;
        long id = TestSupport.register(TestSupport.registrarToken(), request);

        given()
            .auth().oauth2(TestSupport.registrarToken())
        .when()
            .get("/registrar/events?type=birth&name=" + name.substring(0, 8).toLowerCase())
        .then()
            .statusCode(200)
            .body("id", contains((int) id));

        // Renaming works because the event is addressed by id, not by name
        request.childName = "Renamed Haile Gebre";
        request.birthWeight = 2.9f;
        put(id, request)
            .statusCode(200)
            .body("childName", equalTo("Renamed Haile Gebre"))
            .body("birthWeight", equalTo(2.9f))
            .body("status", equalTo("PENDING"));

        given()
            .auth().oauth2(TestSupport.registrarToken())
        .when()
            .get("/registrar/events/" + id)
        .then()
            .statusCode(200)
            .body("childName", equalTo("Renamed Haile Gebre"));

        EventRequest asDeath = TestSupport.death("Tigray");
        put(id, asDeath).statusCode(400).body("error", containsString("type cannot be changed"));
        put(999999999L, request).statusCode(404);
    }

    @Test
    void testRegistrarCannotReviewOrSeeReports() {
        long id = TestSupport.register(TestSupport.registrarToken(), TestSupport.birth("Sidama"));
        given().auth().oauth2(TestSupport.registrarToken())
            .when().post("/official/events/" + id + "/approve").then().statusCode(403);
        given().auth().oauth2(TestSupport.registrarToken())
            .when().get("/official/reports").then().statusCode(403);
    }

    @Test
    void testOfficialCannotRegister() {
        given()
            .auth().oauth2(TestSupport.officialToken())
            .contentType(ContentType.JSON)
            .body(TestSupport.birth("Sidama"))
        .when()
            .post("/registrar/register-event")
        .then()
            .statusCode(403);
    }

    @Test
    void testLocationsAreReused() {
        String region = TestSupport.unique("Shared");
        TestSupport.register(TestSupport.registrarToken(), TestSupport.birth(region));
        TestSupport.register(TestSupport.registrarToken(), TestSupport.death(region));
        assertEquals(1, locationRepository.count("region", region));
    }
}
