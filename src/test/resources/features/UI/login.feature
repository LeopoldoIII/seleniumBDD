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