@api @crud
Feature: Full CRUD Operations for Installment Plans with JWT and SQL Verification

  Background:
    Given I authenticate with username "testuser@example.com" and password "testpassword"

  @smoke
  Scenario: CREATE - Insert new installment plan via API and verify in SQL database
    When I send a POST request to "/api/plans" with body:
      """
      {
        "id": "PLAN-201",
        "customer": "Diana Prince",
        "totalAmount": 1200.00,
        "installments": 12,
        "monthlyDue": 100.00,
        "status": "ACTIVE"
      }
      """
    Then the response status should be 201
    And the response field "id" should be "PLAN-201"
    And the response field "customer" should be "Diana Prince"
    And the response field "status" should be "ACTIVE"
    And the database record for plan "PLAN-201" should have status "ACTIVE"

  Scenario: READ - Retrieve seeded installment plan via API
    When I send a GET request to "/api/plans/PLAN-101"
    Then the response status should be 200
    And the response field "id" should be "PLAN-101"
    And the response field "customer" should be "Alice Smith"
    And the response field "status" should be "ACTIVE"

  Scenario: UPDATE - Modify installment plan status via API and verify in SQL database
    Given I send a POST request to "/api/plans" with body:
      """
      {
        "id": "PLAN-301",
        "customer": "Barry Allen",
        "totalAmount": 1800.00,
        "installments": 18,
        "monthlyDue": 100.00,
        "status": "ACTIVE"
      }
      """
    When I send a PUT request to "/api/plans/PLAN-301" with body:
      """
      {
        "status": "PAID"
      }
      """
    Then the response status should be 200
    And the response field "id" should be "PLAN-301"
    And the response field "status" should be "PAID"
    And the database record for plan "PLAN-301" should have status "PAID"
    And the database should have at least 1 payment record for plan "PLAN-301"
    And the database total amount paid for plan "PLAN-301" should equal 1800.00

  Scenario: DELETE - Remove installment plan via API and verify in SQL database
    Given I send a POST request to "/api/plans" with body:
      """
      {
        "id": "PLAN-401",
        "customer": "Arthur Curry",
        "totalAmount": 600.00,
        "installments": 6,
        "monthlyDue": 100.00,
        "status": "ACTIVE"
      }
      """
    When I send a DELETE request to "/api/plans/PLAN-401"
    Then the response status should be 200
    And the response field "status" should be "DELETED"
    And the database record for plan "PLAN-401" should not exist
    And the database should have at least 0 payment records for plan "PLAN-401"

  Scenario: RELATIONAL - Verify seeded payment record in database for completed plan
    When I send a GET request to "/api/plans/PLAN-102/payments"
    Then the response status should be 200
    And the database should have at least 1 payment record for plan "PLAN-102"
    And the database total amount paid for plan "PLAN-102" should equal 600.00
