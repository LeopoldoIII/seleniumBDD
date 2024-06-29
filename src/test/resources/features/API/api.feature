Feature: API Testing with Rest Assured

  Scenario: Get user details
    Given I have the API endpoint "https://jsonplaceholder.typicode.com/users/1"
    When I send a GET request
    Then the response status should be 200
    And the response should contain "Leanne Graham"