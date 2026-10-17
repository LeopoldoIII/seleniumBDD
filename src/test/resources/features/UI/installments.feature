@ui
Feature: Installment Plans Verification

  @smoke
  Scenario: Login and verify seeded installment plans in table
    Given I am on the login page
    When I enter username "testuser@example.com" and password "testpassword"
    Then I should see the installment plans table
    And the installment plan "PLAN-101" should be displayed with status "ACTIVE"
    And the installment plan "PLAN-102" should be displayed with status "PAID"
