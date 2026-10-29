package org.vers.backend.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.vers.backend.entity.Report;
import org.vers.backend.repository.ReportRepository;
import org.vers.backend.repository.UserRepository;

@QuarkusTest
public class ReportResourceTest {

    @Inject
    ReportRepository reportRepository;

    @Inject
    UserRepository userRepository;

    private static ValidatableResponse approve(String token, long id) {
        return given().auth().oauth2(token)
            .when().post("/official/events/" + id + "/approve").then();
    }

    private static ValidatableResponse reject(String token, long id, Map<String, String> body) {
        return given().auth().oauth2(token).contentType(ContentType.JSON).body(body)
            .when().post("/official/events/" + id + "/reject").then();
    }

    @Test
    void testOfficialApprovesPendingEvent() {
        long id = TestSupport.register(TestSupport.registrarToken(), TestSupport.birth("Harari"));

        given().auth().oauth2(TestSupport.officialToken())
            .when().get("/official/events?status=pending")
            .then().statusCode(200).body("id", hasItem((int) id));

        approve(TestSupport.officialToken(), id)
            .statusCode(200)
            .body("status", equalTo("APPROVED"))
            .body("reviewedBy", equalTo("official"))
            .body("reviewedAt", notNullValue());

        approve(TestSupport.officialToken(), id)
            .statusCode(409)
            .body("error", containsString("Only pending events"));
    }

    @Test
    void testAdminCanApproveAndReject() {
        long first = TestSupport.register(TestSupport.registrarToken(), TestSupport.death("Harari"));
        approve(TestSupport.adminToken(), first).statusCode(200).body("reviewedBy", equalTo("admin"));

        long second = TestSupport.register(TestSupport.registrarToken(), TestSupport.death("Harari"));
        reject(TestSupport.adminToken(), second, Map.of())
            .statusCode(400)
            .body("error", containsString("reason is required"));
        reject(TestSupport.adminToken(), second, Map.of("reason", "Certifier name is misspelled"))
            .statusCode(200)
            .body("status", equalTo("REJECTED"))
            .body("rejectionReason", equalTo("Certifier name is misspelled"));
    }

    @Test
    void testEditingAReviewedEventSendsItBackForReview() {
        var request = TestSupport.marriage("Harari");
        long id = TestSupport.register(TestSupport.registrarToken(), request);
        reject(TestSupport.officialToken(), id, Map.of("reason", "Wrong date"));

        request.dateOfMarriage = "2024-03-02";
        given().auth().oauth2(TestSupport.registrarToken()).contentType(ContentType.JSON).body(request)
            .when().put("/registrar/events/" + id)
            .then()
            .statusCode(200)
            .body("status", equalTo("PENDING"))
            .body("reviewedBy", nullValue())
            .body("rejectionReason", nullValue());
    }

    @Test
    void testNobodyReviewsTheirOwnEvent() {
        long id = TestSupport.register(TestSupport.adminToken(), TestSupport.birth("Harari"));
        approve(TestSupport.adminToken(), id)
            .statusCode(403)
            .body("error", equalTo("You cannot review an event you registered."));
        approve(TestSupport.officialToken(), id).statusCode(200);
    }

    @Test
    void testUnknownEventIs404() {
        approve(TestSupport.officialToken(), 999999999L).statusCode(404);
    }

    @Test
    void testReportFilters() {
        String region = TestSupport.unique("Report");
        String registrar = TestSupport.registrarToken();
        long birth = TestSupport.register(registrar, TestSupport.birth(region));
        var femaleBirth = TestSupport.birth(region);
        femaleBirth.gender = "FEMALE";
        femaleBirth.birthWeight = 2.8f;
        TestSupport.register(registrar, femaleBirth);
        TestSupport.register(registrar, TestSupport.death(region));
        TestSupport.register(registrar, TestSupport.marriage(region));
        approve(TestSupport.officialToken(), birth);
        int year = LocalDate.now().getYear();
        String month = LocalDate.now().toString().substring(0, 7);

        given().auth().oauth2(TestSupport.officialToken())
            .when().get("/official/reports?region=" + region.toLowerCase())
            .then()
            .statusCode(200)
            .body("totalEvents", equalTo(4))
            .body("byType.BIRTH", equalTo(2))
            .body("byType.DEATH", equalTo(1))
            .body("byType.MARRIAGE", equalTo(1))
            .body("byType.DIVORCE", equalTo(0))
            .body("byStatus.APPROVED", equalTo(1))
            .body("byStatus.PENDING", equalTo(3))
            .body("byGender.MALE", equalTo(1))
            .body("byGender.FEMALE", equalTo(2))
            .body("byRegion." + region, equalTo(4))
            .body("byMonth.'" + month + "'", equalTo(4))
            .body("averageBirthWeight", equalTo(3.0f));

        given().auth().oauth2(TestSupport.adminToken())
            .when().get("/official/reports?region=" + region + "&type=BIRTH&gender=female&registrationYear=" + year)
            .then()
            .statusCode(200)
            .body("totalEvents", equalTo(1))
            .body("filters.gender", equalTo("FEMALE"))
            .body("averageBirthWeight", equalTo(2.8f));

        given().auth().oauth2(TestSupport.officialToken())
            .when().get("/official/reports?region=" + region + "&status=approved&type=")
            .then()
            .statusCode(200)
            .body("totalEvents", equalTo(1));

        given().auth().oauth2(TestSupport.officialToken())
            .when().get("/official/reports?region=" + region + "&registrationYear=" + (year - 1))
            .then()
            .statusCode(200)
            .body("totalEvents", equalTo(0))
            .body("averageBirthWeight", nullValue());

        given().auth().oauth2(TestSupport.officialToken())
            .when().get("/official/reports?gender=unknown")
            .then()
            .statusCode(400);
    }

    @Test
    void testReportFilterOptions() {
        String region = TestSupport.unique("Options");
        TestSupport.register(TestSupport.registrarToken(), TestSupport.divorce(region));
        given().auth().oauth2(TestSupport.officialToken())
            .when().get("/official/reports/filters")
            .then()
            .statusCode(200)
            .body("types", contains("DEATH", "BIRTH", "MARRIAGE", "DIVORCE"))
            .body("statuses", contains("PENDING", "APPROVED", "REJECTED"))
            .body("genders", contains("MALE", "FEMALE"))
            .body("regions", hasItem(region))
            .body("registrationYears", hasItem(LocalDate.now().getYear()));
    }

    @Test
    @Transactional
    void testReportEntityStoresJson() {
        Report report = new Report();
        report.generatedBy = userRepository.findByUsername("official").orElseThrow();
        report.data = "{\"totalEvents\": 4}";
        reportRepository.persist(report);
        reportRepository.flush();
        reportRepository.getEntityManager().clear();
        org.junit.jupiter.api.Assertions.assertTrue(
            reportRepository.findById(report.id).data.contains("\"totalEvents\"")
        );
    }
}
