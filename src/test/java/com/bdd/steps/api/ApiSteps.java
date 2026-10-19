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
    private String authToken;

    public ApiSteps() {
        this.apiClient = new ApiClient();
    }

    @Given("I have the API endpoint {string}")
    public void i_have_the_api_endpoint(String url) {
        this.endpoint = url;
    }

    @Given("I authenticate with username {string} and password {string}")
    public void i_authenticate_with_username_and_password(String username, String password) {
        String authPayload = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        this.response = apiClient.post("/api/auth/login", authPayload);
        if (response.getStatusCode() == 200) {
            this.authToken = response.jsonPath().getString("token");
            apiClient.setAuthToken(this.authToken);
        }
    }

    @Given("I do not provide an authentication token")
    public void i_do_not_provide_an_authentication_token() {
        this.authToken = null;
        apiClient.clearAuthToken();
    }

    @When("I send a GET request")
    public void i_send_a_get_request() {
        if (endpoint != null && (endpoint.startsWith("http://") || endpoint.startsWith("https://"))) {
            io.restassured.specification.RequestSpecification spec = io.restassured.RestAssured.given()
                    .accept(io.restassured.http.ContentType.JSON);
            if (this.authToken != null) {
                spec.header("Authorization", "Bearer " + this.authToken);
            }
            this.response = spec.when().get(endpoint);
        } else {
            // Relative URL resolved against configured base URI
            this.response = apiClient.get(endpoint);
        }
    }

    @When("I send a GET request to {string}")
    public void i_send_a_get_request_to(String url) {
        this.endpoint = url;
        i_send_a_get_request();
    }

    @When("I send an unauthenticated GET request to {string}")
    public void i_send_an_unauthenticated_get_request_to(String url) {
        this.endpoint = url;
        String baseUri = apiClient.getBaseUri();
        String fullUrl;
        if (url.startsWith("http://") || url.startsWith("https://")) {
            fullUrl = url;
        } else if (baseUri.endsWith("/api") && url.startsWith("/api/")) {
            fullUrl = baseUri.substring(0, baseUri.length() - 4) + url;
        } else {
            fullUrl = baseUri + url;
        }
        this.response = io.restassured.RestAssured.given()
                .accept(io.restassured.http.ContentType.JSON)
                .when()
                .get(fullUrl);
    }

    @When("I send a POST request with body:")
    public void i_send_a_post_request_with_body(String jsonBody) {
        this.response = apiClient.post(endpoint, jsonBody);
    }

    @When("I send a POST request to {string} with body:")
    public void i_send_a_post_request_to_with_body(String url, String jsonBody) {
        this.endpoint = url;
        this.response = apiClient.post(endpoint, jsonBody);
    }

    @When("I send a PUT request to {string} with body:")
    public void i_send_a_put_request_to_with_body(String url, String jsonBody) {
        this.endpoint = url;
        this.response = apiClient.put(endpoint, jsonBody);
    }

    @When("I send a DELETE request to {string}")
    public void i_send_a_delete_request_to(String url) {
        this.endpoint = url;
        this.response = apiClient.delete(endpoint);
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode, "Unexpected HTTP status code");
    }

    @Then("the response status should be one of {int} or {int}")
    public void the_response_status_should_be_one_of(int code1, int code2) {
        int actual = response.getStatusCode();
        Assert.assertTrue(actual == code1 || actual == code2,
                "Expected status code " + code1 + " or " + code2 + ", but got " + actual);
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

    @Then("the response field {string} should not be empty")
    public void the_response_field_should_not_be_empty(String jsonPathExpression) {
        JsonPath jsonPath = response.jsonPath();
        String actualValue = jsonPath.getString(jsonPathExpression);
        Assert.assertNotNull(actualValue, "Field is null: " + jsonPathExpression);
        Assert.assertFalse(actualValue.trim().isEmpty(), "Field is empty: " + jsonPathExpression);
    }
}