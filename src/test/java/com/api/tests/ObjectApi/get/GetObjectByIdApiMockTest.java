package com.api.tests.ObjectApi.get;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.qameta.allure.*;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.is;

/**
 * Mock API Test class using WireMock
 * Tests error scenarios like 500 Internal Server Error
 */
@Epic("Object API Testing")
@Feature("Get Object By ID - Mock Tests")
@Story("Error Response Testing")
public class GetObjectByIdApiMockTest {

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
        
        // Configure RestAssured to use WireMock server
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = dynamicPort;
        
        // Add Allure RestAssured filter for request/response logging
        RestAssured.filters(new AllureRestAssured());

        System.out.println("=== WireMock Server Started ===");
        System.out.println("Running on dynamic port: " + dynamicPort);
        System.out.println("Base URL: http://localhost:" + dynamicPort);
        
    }

    @AfterClass
    public void teardown() {
        // Stop the WireMock server after all tests
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
            System.out.println("=== WireMock Server Stopped ===");
        }
    }

    @Test(description = "Test 500 Internal Server Error mock response")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the API returns 500 Internal Server Error with appropriate error message")
    @Step("Test 500 Internal Server Error Response")
    public void testInternalServerError500() {
        // Setup mock stub for 500 Internal Server Error
        wireMockServer.stubFor(get(urlEqualTo("/objects/7"))
            .willReturn(aResponse()
                .withStatus(500)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\": \"Internal Server Error\", \"message\": \"Something went wrong on the server\"}")));

        // Make request and verify 500 response
        Response response = given()
            .baseUri("http://localhost:" + dynamicPort)
            .header("Content-Type", "application/json")
        .when()
            .get("/objects/7")
        .then()
            .statusCode(500)
            .contentType("application/json")
            .body("error", is("Internal Server Error"))
            .body("message", is("Something went wrong on the server")).extract() .response();
            
         

        // Print response details
        System.out.println("=== Mock Response Details ===");
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asPrettyString());
    }

        @Test(description = "Test 404 Resource Not Found mock response")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the API returns 404 Resource Not Found for non-existent resources")
    @Step("Test 404 Resource Not Found Response")
    public void testResourceNotFound404() {
        // Setup mock stub for 404 Resource Not Found
        wireMockServer.stubFor(get(urlEqualTo("/objects/7"))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\": \"Resource Not Found\", \"message\": \"The requested resource was not found\"}")));

        // Make request and verify 404 response
        Response response = given()
          .baseUri("http://localhost:" + dynamicPort)
            .header("Content-Type", "application/json")
        .when()
            .get("/objects/7")
        .then()
            .statusCode(404)
            .contentType("application/json")
            .body("error", is("Resource Not Found"))
            .body("message", is("The requested resource was not found"))
            .extract()
            .response();

        // Print response details
        System.out.println("=== Mock Response Details ===");
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asPrettyString());
    }

    @Test(description = "Test 403 Forbidden mock response")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the API returns 403 Forbidden when user lacks permissions")
    @Step("Test 403 Forbidden Response")
    public void testForbidden403() {
        // Setup mock stub for 403 Forbidden
        wireMockServer.stubFor(get(urlEqualTo("/objects/7"))
            .willReturn(aResponse()
                .withStatus(403)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\": \"Forbidden\", \"message\": \"You do not have permission to access this resource\"}")));

        // Make request and verify 403 response
        Response response = given()
            .baseUri("http://localhost:" + dynamicPort)
        .when()
            .get("/objects/7")
        .then()
            .statusCode(403)
            .contentType("application/json")
            .body("error", is("Forbidden"))
            .body("message", is("You do not have permission to access this resource"))
            .extract()
            .response();

        // Print response details
        System.out.println("=== Mock Response Details (403) ===");
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asPrettyString());
    }

    
     @Test(description = "Test 401 Unauthrorised response")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that the API returns 401 Unauthorized for unauthenticated requests")
    @Step("Test 401 Unauthorized Response")
    public void testUnauthorized401() {
        // Setup mock stub for 401 Unauthrorised
        wireMockServer.stubFor(get(urlEqualTo("/objects/7"))
            .willReturn(aResponse()
                .withStatus(401)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\": \"Unauthorized\", \"message\": \"You are not authorized to access this resource\"}")));

        // Make request and verify 401 response
        Response response = given()
            .baseUri("http://localhost:" + dynamicPort)
            .header("Content-Type", "application/json")
        .when()
            .get("/objects/7")
        .then()
            .statusCode(401)
            .contentType("application/json")
            .body("error", is("Unauthorized"))
            .body("message", is("You are not authorized to access this resource"))
            .extract()
            .response();

        // Print response details
        System.out.println("=== Mock Response Details (401) ===");
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asPrettyString());
    }
}
