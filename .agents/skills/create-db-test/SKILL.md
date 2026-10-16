---
name: create-db-test
description: >-
  Use this skill when adding or updating database SQL validations, executing queries with DbManager,
  or writing Cucumber scenarios that verify database state.
---

# Create Database Test Workflow

Follow this step-by-step workflow to validate database state (SQL) during test execution.

## Step 1: Configure Database Connection
Set database parameters in `src/main/resources/config.json` or pass via CLI:
```bash
mvn test -Dtest=UiTestRunner -Ddatabase.url="jdbc:postgresql://localhost:5432/testdb" -Ddatabase.username="postgres" -Ddatabase.password="secret"
```

## Step 2: Use Predefined Database Steps
Step definitions are located in `src/test/java/com/bdd/steps/database/DbSteps.java`:

```gherkin
@regression
Scenario: Verify newly registered user exists in database
  Given I am on the login page
  When I enter username "new_user" and password "pass123"
  And I click on the login button
  Then I execute the database query "SELECT * FROM users WHERE username = 'new_user'"
  And the database query should return at least 1 record(s)
  And the database column "status" in the first record should be "ACTIVE"
```

## Step 3: Direct Usage via DbManager in Java
If creating custom step definitions:
```java
List<Map<String, Object>> rows = DbManager.executeQuery("SELECT * FROM installments WHERE id = 101");
Assert.assertEquals(rows.get(0).get("status"), "PAID");
```
