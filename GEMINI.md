# Antigravity Workspace Configuration: seleniumBDD

## Overview
This repository is an omnichannel test automation framework built with **Java 21**, **Maven**, **Cucumber BDD**, **TestNG**, **Selenium WebDriver**, **Appium**, **REST Assured**, and **JDBC**. It supports automated **Web UI**, **Mobile (Android & iOS)**, **API**, and **Database (SQL)** testing with local execution and **Sauce Labs** cloud execution.

---

## Tech Stack & Dependencies
- **Language**: Java 21 (`<maven.compiler.source>21</maven.compiler.source>`)
- **Build Tool**: Apache Maven (`pom.xml`)
- **BDD Framework**: Cucumber (`io.cucumber:cucumber-java:6.10.4`, `io.cucumber:cucumber-testng:6.10.2`)
- **Test Runner & Assertions**: TestNG (`org.testng:testng:7.4.0`)
- **UI Automation**: Selenium Java (`org.seleniumhq.selenium:selenium-java:4.22.0`), WebDriverManager (`io.github.bonigarcia:webdrivermanager:5.8.0`)
- **Mobile Automation**: Appium Java Client (`io.appium:java-client:9.2.2`) for Android (UiAutomator2) & iOS (XCUITest)
- **API Automation**: REST Assured (`io.rest-assured:rest-assured:5.4.0`, `json-path`, `xml-path`)
- **Database / SQL**: JDBC with drivers for PostgreSQL, MySQL, and H2 (`com.bdd.core.database.DbManager`)
- **Cloud Testing**: Sauce Labs integration (`com.bdd.core.driver.SauceLabsManager`)
- **Reporting**: Cucumber HTML Reports (`target/cucumber-ui-reports.html`, `target/cucumber-api-reports.html`)
- **Configuration**: Multi-level (`System properties` > `Env vars` > `src/main/resources/config.json`)

---

## Directory Structure

```text
seleniumBDD/
├── pom.xml                               # Maven project definition and dependencies
├── README.md                             # Interview cheat-sheet & execution guide
├── GEMINI.md                             # Antigravity workspace instructions
├── AGENTS.md                             # Portable cross-agent instructions
├── .agents/
│   ├── rules/                            # Modular workspace guidelines
│   │   ├── architecture.md               # Framework layer boundaries and roles
│   │   ├── ui-testing.md                 # POM conventions, locators, driver lifecycle
│   │   ├── api-testing.md                # RestAssured patterns and validations
│   │   └── code-style.md                 # Java, Gherkin, and naming conventions
│   └── skills/                           # Workflows and runbooks for Antigravity
├── src/
│   ├── main/
│   │   ├── java/com/bdd/
│   │   │   ├── config/ConfigReader.java  # Multi-level config reader (-D, env, json)
│   │   │   ├── core/
│   │   │   │   ├── DriverManager.java    # ThreadLocal<WebDriver> lifecycle
│   │   │   │   ├── driver/               # DriverFactory, SauceLabsManager, DriverType
│   │   │   │   └── database/             # DbManager (JDBC SQL queries & updates)
│   │   │   ├── api/                      # ApiClient (REST Assured centralized client)
│   │   │   └── pages/                    # BasePage ("Navaja Suiza") and Page Objects
│   │   └── resources/
│   │       └── config.json               # Environment config (Web, API, Mobile, DB, SauceLabs)
│   └── test/
│       ├── java/com/bdd/
│       │   ├── hooks/Hooks.java          # Screenshot on failure & SauceLabs reporting
│       │   ├── runners/                  # TestNG runners (UiTestRunner, ApiTestRunner)
│       │   └── steps/                    # Steps (ui/, api/, database/)
│       └── resources/features/           # Cucumber Feature files
│           ├── UI/                       # UI features (e.g. login.feature)
│           └── API/                      # API features (e.g. api.feature)
```

---

## Test Execution Commands

| Test Suite | Command |
| :--- | :--- |
| **All UI Tests (Web)** | `mvn clean test -Dtest=UiTestRunner` |
| **UI Tests with Custom Base URL** | `mvn test -Dtest=UiTestRunner -DbaseUrl=http://localhost:8080` |
| **All API Tests** | `mvn clean test -Dtest=ApiTestRunner` |
| **API Tests with Custom URI** | `mvn test -Dtest=ApiTestRunner -Dapi.baseUri=http://localhost:8080/api` |
| **Cloud Execution (Sauce Labs)** | `mvn test -Dtest=UiTestRunner -Dexecution=saucelabs -Dsaucelabs.username="USER" -Dsaucelabs.accessKey="KEY"` |
| **Mobile Tests (Android/iOS)** | `mvn test -Dtest=UiTestRunner -Dtarget=mobile -Dmobile.platformName=Android` |
| **Specific Tag** | `mvn test -Dtest=UiTestRunner -Dcucumber.filter.tags="@smoke"` |
| **Compilation Only** | `mvn test-compile` |

---

## Core Guidelines for Antigravity

1. **Page Object Model (POM)**:
   - All UI locators and web element interactions must reside in `src/main/java/com/bdd/pages/`.
   - Page classes must extend `BasePage` and use Selenium's `@FindBy` annotations.
   - Do not call Selenium `WebDriver` or `findElement` directly inside Step Definitions.
2. **Driver Management**:
   - `DriverManager.getDriver()` manages the `ThreadLocal<WebDriver>` instance.
   - Browser setup and teardown are handled automatically via `Hooks.java` (`@Before` and `@After`).
   - Browser options (headless mode, window size, sandboxing) are set via `DriverFactory.java`.
3. **Database Validations**:
   - Use `DbManager.executeQuery("SELECT ...")` to retrieve and assert database state.
4. **Step Definitions**:
   - Keep step definition classes organized under `com.bdd.steps.ui`, `com.bdd.steps.api`, and `com.bdd.steps.database`.
5. **Configuration**:
   - Read project environment values using `ConfigReader.getProperty(key)` or `ConfigReader.getBaseUrl()`.
   - Never hardcode environment credentials or base URLs inside step definitions or page objects.
6. **Language & Documentation Policy**:
   - All generated code, Gherkin features, comments, JavaDoc, and documentation files (READMEs, cheat sheets) must strictly be written in English.
   - Conversational chat may be in Spanish or the user's preferred language, but all files written to disk must be in English.

