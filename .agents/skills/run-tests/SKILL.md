---
name: run-tests
description: >-
  Use this skill when running, debugging, or analyzing automated UI and API test suites,
  filtering tests by tags, or configuring execution parameters in config.json.
---

# Test Execution Runbook

Use this runbook to execute and diagnose automated tests in this repository.

## Execution Matrix

| Goal | Command |
| :--- | :--- |
| **Run All UI Tests (Web)** | `mvn clean test -Dtest=UiTestRunner` |
| **Run UI Tests with Custom Base URL** | `mvn test -Dtest=UiTestRunner -DbaseUrl=http://localhost:8080` |
| **Run UI Tests with Visible Browser** | `mvn test -Dtest=UiTestRunner -Dheadless=false` |
| **Run All API Tests** | `mvn clean test -Dtest=ApiTestRunner` |
| **Run API Tests with Custom URI** | `mvn test -Dtest=ApiTestRunner -Dapi.baseUri=http://localhost:8080/api` |
| **Cloud Execution (Sauce Labs)** | `mvn test -Dtest=UiTestRunner -Dexecution=saucelabs -Dsaucelabs.username="USER" -Dsaucelabs.accessKey="KEY"` |
| **Mobile Tests (Android via Appium)**| `mvn test -Dtest=UiTestRunner -Dtarget=mobile -Dmobile.platformName=Android` |
| **Mobile Tests (iOS via Appium)**    | `mvn test -Dtest=UiTestRunner -Dtarget=mobile -Dmobile.platformName=iOS` |
| **Run Specific Tag**                | `mvn test -Dtest=UiTestRunner -Dcucumber.filter.tags="@smoke"` |
| **Compile & Validate Syntax**        | `mvn test-compile` |

## Dynamic Configuration Hierarchy

Configuration is resolved automatically via `ConfigReader`:
1. **CLI System Properties** (`-DbaseUrl=...`, `-Dexecution=saucelabs`, `-Dheadless=false`)
2. **Environment Variables** (`BASE_URL`, `SAUCE_USERNAME`, `SAUCE_ACCESS_KEY`)
3. **Configuration File**: `src/main/resources/config.json`

## Parallel Execution
The test runners `UiTestRunner` and `ApiTestRunner` configure parallel execution via TestNG DataProvider (`parallel = true`), safely isolated via `ThreadLocal<WebDriver>` in `DriverManager`.

To control thread count:
```bash
mvn clean test -Dtest=UiTestRunner -Ddataproviderthreadcount=2
```

## Report Artifacts
After test execution, review results in:
- UI Runner HTML Report: `target/cucumber-ui-reports.html`
- API Runner HTML Report: `target/cucumber-api-reports.html`
- Surefire Reports: `target/surefire-reports/`
