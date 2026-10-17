@ui
Feature: Login and Navigation Functionality

  @smoke
  Scenario: Navigate to base URL and verify page title
    Given I am on the login page
    Then I should see the homepage

  @regression
  Scenario: Login with credentials
    Given I am on the login page
    When I enter username "DEFAULT" and password "DEFAULT"
    Then I should see the homepage

  @negative
  Scenario: Login with empty password displays validation error
    Given I am on the login page
    When I enter username "testuser@example.com" and password ""
    Then I should see the error message "Password is required!"

  @negative
  Scenario: Login with invalid email format displays validation error
    Given I am on the login page
    When I enter username "invalid-user" and password "testpassword"
    Then I should see the error message "Username must be a valid email address!"