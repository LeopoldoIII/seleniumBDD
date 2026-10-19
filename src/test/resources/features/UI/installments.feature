@ui
Feature: Installment Plans UI Functionality - Full CRUD and Security

  @smoke
  Scenario: READ - Login and verify seeded installment plans in table
    Given I am on the login page
    When I enter username "testuser@example.com" and password "testpassword"
    Then I should see the installment plans table
    And the installment plan "PLAN-101" should be displayed with status "ACTIVE"
    And the installment plan "PLAN-102" should be displayed with status "PAID"

  @smoke
  Scenario: CREATE - Add new installment plan from the dashboard UI
    Given I am on the login page
    When I enter username "testuser@example.com" and password "testpassword"
    And I create a new installment plan with ID "PLAN-601", customer "Bruce Wayne", amount "3600.00", installments "12"
    Then the installment plan "PLAN-601" should be displayed with status "ACTIVE"

  Scenario: UPDATE - Pay installment plan from UI and verify status updates to PAID
    Given I am on the login page
    When I enter username "testuser@example.com" and password "testpassword"
    And I create a new installment plan with ID "PLAN-801", customer "Diana Prince", amount "2000.00", installments "10"
    And I click to pay for installment plan "PLAN-801"
    Then the installment plan "PLAN-801" should be displayed with status "PAID"
    When I delete the installment plan "PLAN-801"

  Scenario: DELETE - Remove installment plan from UI dashboard
    Given I am on the login page
    When I enter username "testuser@example.com" and password "testpassword"
    And I create a new installment plan with ID "PLAN-701", customer "Tony Stark", amount "5000.00", installments "10"
    And the installment plan "PLAN-701" should be displayed with status "ACTIVE"
    When I delete the installment plan "PLAN-701"
    Then the installment plan "PLAN-701" should not be displayed in the table

  @negative @security
  Scenario: SECURITY - Unauthenticated access to dashboard redirects to login page
    When I directly access the dashboard page
    Then I should be redirected to the login page
