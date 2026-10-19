@api @jwt
Feature: Authentication and Security with Spring Boot JWT

  @smoke
  Scenario: Login with valid credentials returns 200 OK and JWT token
    When I send a POST request to "/api/auth/login" with body:
      """
      {
        "username": "testuser@example.com",
        "password": "testpassword"
      }
      """
    Then the response status should be 200
    And the response field "token" should not be empty
    And the response field "type" should be "Bearer"
    And the response field "username" should be "testuser@example.com"

  @negative
  Scenario: Login with invalid password returns 401 Unauthorized
    When I send a POST request to "/api/auth/login" with body:
      """
      {
        "username": "testuser@example.com",
        "password": "wrongpassword"
      }
      """
    Then the response status should be 401
    And the response field "error" should be "Unauthorized"

  @negative
  Scenario: Access protected /api/plans without JWT token returns 401 or 403
    Given I do not provide an authentication token
    When I send an unauthenticated GET request to "/api/plans"
    Then the response status should be one of 401 or 403

  @smoke
  Scenario: Access protected /api/plans with valid JWT token returns 200 OK
    Given I authenticate with username "testuser@example.com" and password "testpassword"
    When I send a GET request to "/api/plans"
    Then the response status should be 200
    And the response should contain "PLAN-101"

  Scenario: Admin user authenticates and receives JWT token
    When I send a POST request to "/api/auth/login" with body:
      """
      {
        "username": "admin@example.com",
        "password": "adminpassword123"
      }
      """
    Then the response status should be 200
    And the response field "token" should not be empty
    And the response field "username" should be "admin@example.com"
