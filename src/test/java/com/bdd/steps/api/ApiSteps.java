package com.bdd.steps.api;

import com.bdd.api.ApiClient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;

public class ApiSteps {

    private ApiClient apiClient;
    private String endpoint;
    private Response response;

    public ApiSteps() {
        this.apiClient = new ApiClient();
    }

    @Given("I have the API endpoint {string}")
    public void i_have_the_api_endpoint(String url) {
        this.endpoint = url;
    }

    @When("I send a GET request")
    public void i_send_a_get_request() {
        if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
            // Absolute URL
            this.response = io.restassured.RestAssured.given()
                    .accept(io.restassured.http.ContentType.JSON)
                    .when()
                    .get(endpoint);
        } else {
            // Relative URL resolved against configured base URI
            this.response = apiClient.get(endpoint);
        }
    }

    @When("I send a POST request with body:")
    public void i_send_a_post_request_with_body(String jsonBody) {
        this.response = apiClient.post(endpoint, jsonBody);
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode, "Unexpected HTTP status code");
    }

    @Then("the response should contain {string}")
    public void the_response_should_contain(String content) {
        Assert.assertTrue(response.getBody().asString().contains(content),
                "Response body does not contain expected string: " + content);
    }

    @Then("the response field {string} should be {string}")
    public void the_response_field_should_be(String jsonPathExpression, String expectedValue) {
        JsonPath jsonPath = response.jsonPath();
        String actualValue = jsonPath.getString(jsonPathExpression);
        Assert.assertEquals(actualValue, expectedValue, "Mismatch in field: " + jsonPathExpression);
    }

    @Then("the response field {string} should be {int}")
    public void the_response_field_should_be_int(String jsonPathExpression, int expectedValue) {
        JsonPath jsonPath = response.jsonPath();
        int actualValue = jsonPath.getInt(jsonPathExpression);
        Assert.assertEquals(actualValue, expectedValue, "Mismatch in field: " + jsonPathExpression);
    }
}