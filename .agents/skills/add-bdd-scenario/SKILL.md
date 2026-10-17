---
name: add-bdd-scenario
description: >-
  Use this skill when adding new BDD Cucumber scenarios to existing or new feature files,
  generating Gherkin steps from a requirement or title, reusing existing steps, and scaffolding
  any missing Page Object methods or Step Definitions.
---

# Add BDD Scenario Workflow

Use this procedural runbook to translate functional requirements or scenario titles into robust BDD scenarios, ensuring step reuse, POM compliance, and proper tagging.

---

## Step 1: Analyze Input & Classify Intent
When given a requirement, user story, or scenario title:
1. **Identify Target Layer**:
   - `@ui`: Interactions with Web or Mobile UI.
   - `@api`: Calls to REST endpoints.
   - `@database`: SQL validations via `DbManager`.
2. **Determine Scenario Type & Tag**:
   - `@smoke`: Critical happy-path workflows.
   - `@regression`: Detailed functional coverage.
   - `@negative`: Validation errors, empty fields, unauthorized access.
3. **Identify Target Feature File**:
   - `src/test/resources/features/UI/<name>.feature`
   - `src/test/resources/features/API/<name>.feature`

---

## Step 2: Audit Existing Steps for Reuse (Crucial)
Before writing any new step definition, inspect existing steps in:
- `src/test/java/com/bdd/steps/ui/` (`LoginSteps`, `InstallmentsSteps`, `SearchSteps`)
- `src/test/java/com/bdd/steps/api/` (`ApiSteps`)
- `src/test/java/com/bdd/steps/database/` (`DbSteps`)

**Standard Reusable Steps Catalog:**
- `Given I am on the login page`
- `When I enter username {string} and password {string}`
- `When I click on the login button`
- `Then I should see the homepage`
- `Then I should see the error message {string}`
- `Then I should see the installment plans table`
- `And the installment plan {string} should be displayed with status {string}`
- `When I send a GET request`
- `Then the response status should be {int}`
- `Then the response field {string} should be {string}`

> **Rule**: ALWAYS reuse existing step patterns whenever possible. Never introduce near-duplicate phrasing.

---

## Step 3: Draft Gherkin Scenario
Format the scenario cleanly using declarative business language:

```gherkin
  @negative
  Scenario: Login with unverified email displays verification alert
    Given I am on the login page
    When I enter username "unverified@example.com" and password "ValidPass123"
    Then I should see the error message "Please verify your email before signing in."
```

For parameterized multi-data validation, use `Scenario Outline`:
```gherkin
  @negative
  Scenario Outline: Validate login field constraints
    Given I am on the login page
    When I enter username "<username>" and password "<password>"
    Then I should see the error message "<expectedError>"

    Examples:
      | username             | password     | expectedError                            |
      |                      | pass123      | Username is required!                    |
      | invalid-email        | pass123      | Username must be a valid email address!  |
      | user@example.com     |              | Password is required!                    |
```

---

## Step 4: Scaffold Missing Steps & Page Object Methods
If the scenario introduces new actions:
1. **Add Locators & Methods to Page Object**:
   - Place in `src/main/java/com/bdd/pages/<TargetPage>.java`.
   - Extend `BasePage` and use non-static `@FindBy` and `BasePage` methods (`click`, `type`, `getText`).
2. **Add Step Definitions**:
   - Place in `src/test/java/com/bdd/steps/ui/<TargetSteps>.java`.
   - Delegate directly to the Page Object instance.
   - Assert in `@Then` using TestNG `Assert`.

---

## Step 5: Verification Command
Run the newly created scenario using its specific tag or feature path:
```bash
mvn test -Dtest=UiTestRunner -Dcucumber.filter.tags="@negative"
```
