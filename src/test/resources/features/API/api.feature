@api
Feature: API Testing with Rest Assured

  @smoke @local
  Scenario: Query seeded installment plan via REST API
    Given I have the API endpoint "http://localhost:8080/api/plans/PLAN-101"
    When I send a GET request
    Then the response status should be 200
    And the response field "id" should be "PLAN-101"
    And the response field "customer" should be "Alice Smith"
    And the response field "status" should be "ACTIVE"

  @regression
  Scenario: Get user details from external public endpoint
    Given I have the API endpoint "https://jsonplaceholder.typicode.com/users/1"
    When I send a GET request
    Then the response status should be 200
    And the response should contain "Leanne Graham"
    And the response field "name" should be "Leanne Graham"
    And the response field "id" should be 1