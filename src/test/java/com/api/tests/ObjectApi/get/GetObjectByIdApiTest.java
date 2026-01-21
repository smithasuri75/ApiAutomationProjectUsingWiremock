package com.api.tests.ObjectApi.get;

import io.qameta.allure.*;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * API Test class for testing https://api.restful-api.dev/objects/7
 */
@Epic("Object API Testing")
@Feature("Get Object By ID - Live API Tests")
@Story("Real API Integration Testing")
public class GetObjectByIdApiTest {

    private static final String BASE_URL = "https://api.restful-api.dev";
    private static final String OBJECTS_ENDPOINT = "/objects/7";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        
        // Add Allure RestAssured filter for request/response logging
        RestAssured.filters(new AllureRestAssured());
        
        System.out.println("=== API Test Setup Complete ===");
        System.out.println("Base URL: " + BASE_URL);
        System.out.println("Testing endpoint: " + OBJECTS_ENDPOINT);
    }

    @Test(description = "Verify GET /objects/7 API response")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify that the real API endpoint returns object with ID 7 and expected data structure")
    @Step("Test Real API GET /objects/7")
    public void testGetObject7Api() {
        Response response = given()
            .header("Content-Type", "application/json")
        .when()
            .get(OBJECTS_ENDPOINT)
        .then()
            .statusCode(200)
            .contentType("application/json")
            .time(lessThan(3000L))
            .body("id", notNullValue())
            .body("id", equalTo("7"))
            .body("name", notNullValue())
            .body("data", notNullValue())
            .extract()
            .response();

        // Print response details for debugging
        System.out.println("=== Response Details ===");
        System.out.println("Response Time: " + response.getTime() + " ms");
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asPrettyString());
      
    }
}
