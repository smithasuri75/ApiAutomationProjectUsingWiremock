package com.api.tests.PrescriptionApi.SendPrescriptionApi;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

/**
 * Mock API Test class for POST /api/prescriptions
 * Tests prescription submission scenarios
 */
@Epic("Prescription API Testing")
@Feature("Send Prescription - Mock Tests")
@Story("Prescription Submission")
public class SendPrescriptionApiMockTest {

    private WireMockServer wireMockServer;
    private int dynamicPort;

    @BeforeClass
    public void setup() {
        // Create WireMock server on dynamic port (randomly assigned available port)
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        
        // Start the WireMock server
        wireMockServer.start();
        
        // Get the dynamically assigned port
        dynamicPort = wireMockServer.port();

        System.out.println("============================ WireMock Server Started ============================");
 
    }

    @AfterClass
    public void teardown() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
            System.out.println("=== WireMock Server Stopped ===");
        }
    }

    @Test(description = "Test successful prescription submission")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that prescription can be successfully submitted with valid data")
    @Step("Test Successful Prescription Submission")
    public void testSendPrescription() {

        // Setup mock stub for successful prescription submission
        wireMockServer.stubFor(post(urlEqualTo("/api/prescriptions"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"status\": \"success\", \"prescriptionId\": \"RX123456789\", \"message\": \"Prescription received successfully\"}")));

        String payload = "{\n" +
                "  \"prescriptionId\": \"RX123456789\",\n" +
                "  \"patient\": { \"patientId\": \"P12345\", \"firstName\": \"John\", \"lastName\": \"Doe\" },\n" +
                "  \"prescriber\": { \"prescriberId\": \"DR98765\", \"firstName\": \"Smitha\", \"lastName\": \"Suri\" },\n" +
                "  \"medication\": [{ \"drugCode\": \"AMOX500\", \"name\": \"Amoxicillin\", \"form\": \"capsule\", \"strength\": \"500mg\", \"dose\": \"1 capsule\", \"route\": \"oral\", \"frequency\": \"TID\", \"duration\": \"7 days\", \"quantity\": 21 }],\n" +
                "  \"dispensePharmacy\": { \"pharmacyId\": \"DS123\", \"name\": \"DoesSpot Pharmacy\" },\n" +
                "  \"deliveryMethod\": \"pickup\",\n" +
                "  \"priority\": \"standard\"\n" +
                "}";

        Response response = given()
            .baseUri("http://localhost:" + dynamicPort)
            .header("Content-Type", "application/json")
            .body(payload)
        .when()
            .post("/api/prescriptions")
        .then()
            .statusCode(200)
            .body("status", is("success"))
            .body("prescriptionId", is("RX123456789"))
            .body("message", containsString("received"))
            .extract()
            .response();

        // Print response details
        System.out.println("=== Mock Response Details ===");
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asPrettyString());
    }
}