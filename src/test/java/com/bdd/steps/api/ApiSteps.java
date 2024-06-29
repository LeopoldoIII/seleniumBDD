package com.bdd.steps.api;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.testng.AssertJUnit.assertEquals;


public class ApiSteps {

    private String endpoint;
    private Response response;

    @Given("I have the API endpoint {string}")
    public void i_have_the_api_endpoint(String url) {
        endpoint = url;
    }

    @When("I send a GET request")
    public void i_send_a_get_request() {
        response = given().when().get(endpoint);
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(int statusCode) {
        assertEquals(response.getStatusCode(), statusCode);
    }

    @Then("the response should contain {string}")
    public void the_response_should_contain(String content) {
        String responseString = response.getBody().asString();
        JsonPath jsonPath = new JsonPath(responseString);
        String name = jsonPath.getString("name");
        assertEquals(name, content);
    }
}