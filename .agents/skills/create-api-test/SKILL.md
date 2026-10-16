---
name: create-api-test
description: >-
  Use this skill when adding or updating REST Assured API tests, creating new API feature files,
  or writing API step definitions with endpoint validations.
---

# Create API Test Workflow

Follow this step-by-step workflow to implement new REST Assured API tests in this repository.

## Step 1: Define the Gherkin Scenario
Create or update a `.feature` file in `src/test/resources/features/API/`:
- Tag scenarios with `@api`.
- Parameterize URLs (relative endpoints resolve to `api.baseUri`), query parameters, payloads, and expected status codes.

```gherkin
@api @smoke
Scenario: Fetch user details
  Given I have the API endpoint "https://jsonplaceholder.typicode.com/users/1"
  When I send a GET request
  Then the response status should be 200
  And the response should contain "Leanne Graham"
  And the response field "name" should be "Leanne Graham"
```

## Step 2: Implement Step Definitions
Leverage `com.bdd.api.ApiClient` in `src/test/java/com/bdd/steps/api/ApiSteps.java`:
1. Use `ApiClient` to send requests and store `Response`.
2. Extract fields using `response.jsonPath()`.
3. Validate assertions with TestNG (`Assert.assertEquals`, etc.).

```java
package com.bdd.steps.api;

import com.bdd.api.ApiClient;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.testng.Assert;

public class ApiSteps {
    private ApiClient apiClient = new ApiClient();
    private String endpoint;
    private Response response;

    @Given("I have the API endpoint {string}")
    public void i_have_the_api_endpoint(String url) {
        this.endpoint = url;
    }

    @When("I send a GET request")
    public void i_send_a_get_request() {
        this.response = apiClient.get(endpoint);
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(int statusCode) {
        Assert.assertEquals(response.getStatusCode(), statusCode);
    }
}
```

## Step 3: Verification & Execution
Run API tests targeting the endpoint:
```bash
mvn clean test -Dtest=ApiTestRunner -Dapi.baseUri=http://localhost:8080/api
```
Verify the generated HTML report under `target/cucumber-api-reports.html`.
