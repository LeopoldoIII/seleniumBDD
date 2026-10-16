# AGENTS.md - Repository Guidelines for seleniumBDD

This file defines guidelines and standards for autonomous coding agents operating within this repository.

See [GEMINI.md](GEMINI.md) for detailed Antigravity workspace instructions.

## Project Summary
- **Type**: Omnichannel Automation Testing Framework (Web, Mobile, API, SQL, Cloud)
- **Language**: Java 21
- **Build System**: Apache Maven
- **Test Runners**: TestNG (`UiTestRunner`, `ApiTestRunner`)
- **BDD Framework**: Cucumber Java
- **UI Automation**: Selenium WebDriver + Appium (Android & iOS) + Page Object Model
- **API Automation**: REST Assured (`ApiClient`)
- **Database / SQL**: JDBC (`DbManager`)
- **Cloud Execution**: Sauce Labs (`SauceLabsManager`)

## Key Commands
- Run UI Tests: `mvn clean test -Dtest=UiTestRunner`
- Run UI Tests with Base URL: `mvn test -Dtest=UiTestRunner -DbaseUrl=http://localhost:8080`
- Run API Tests: `mvn clean test -Dtest=ApiTestRunner`
- Run Sauce Labs: `mvn test -Dtest=UiTestRunner -Dexecution=saucelabs -Dsaucelabs.username="USER" -Dsaucelabs.accessKey="KEY"`
- Run Mobile Tests: `mvn test -Dtest=UiTestRunner -Dtarget=mobile -Dmobile.platformName=Android`
- Compile Check: `mvn test-compile`

## Rules and Architecture
Modular rules and runbooks are organized under `.agents/`:
- `.agents/rules/architecture.md`: Architectural layers and class responsibilities
- `.agents/rules/ui-testing.md`: Page Object Model, BasePage methods, and driver lifecycle
- `.agents/rules/api-testing.md`: ApiClient, REST Assured conventions, and assertions
- `.agents/rules/code-style.md`: Code style, imports, and naming standards
- `.agents/skills/create-ui-test/`: Procedural runbook for creating UI / Mobile tests
- `.agents/skills/create-api-test/`: Procedural runbook for creating REST API tests
- `.agents/skills/create-db-test/`: Procedural runbook for database SQL validations
- `.agents/skills/run-tests/`: Procedural runbook for running and filtering test suites
