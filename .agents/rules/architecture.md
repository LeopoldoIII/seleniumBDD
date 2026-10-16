# Framework Architecture Guidelines

## Architectural Layers

The framework enforces strict separation of concerns across multiple layers:

### 1. Configuration Layer (`com.bdd.config`)
- **`ConfigReader.java`**: Responsible for multi-level property resolution.
  - Priority: System properties (`-Dparam=val`) -> Environment variables (`PARAM_VAL`) -> `src/main/resources/config.json`.
  - Supports Web, API (`api.baseUri`), Database (`database.url`), Mobile (`mobile.*`), and Sauce Labs (`saucelabs.*`).

### 2. Core Driver & Database Layer (`com.bdd.core`)
- **`DriverManager.java`**: Thread-safe driver controller using `ThreadLocal<WebDriver>`.
- **`DriverFactory.java`**: Instantiates local Chrome/Firefox or Appium drivers (Android/iOS), or remote drivers on Sauce Labs.
- **`SauceLabsManager.java`**: Manages Sauce Labs hub connection, capabilities (`sauce:options`), and passes job results back to the dashboard.
- **`DbManager.java`**: JDBC manager supporting queries (`executeQuery`), updates (`executeUpdate`), and single value checks.

### 3. Page Object Model Layer (`com.bdd.pages`)
- All page objects represent distinct application views or reusable components.
- Extends **`BasePage`**, which acts as a "Swiss Army knife" providing:
  - Explicit waits (`waitForVisibility`, `waitForClickable`)
  - Component interactions (`click`, `type`, `selectByVisibleText`, `setCheckbox`)
  - Dynamic table utilities (`getTableRowCount`, `getTableCellText`, `findRowIndexContainingText`)
  - Scroll and JavaScript fallbacks (`jsClick`, `scrollToElement`)
  - Alerts and iframes
- Page objects are standard POJOs (non-static methods and elements) to support parallel execution.

### 4. API Layer (`com.bdd.api`)
- **`ApiClient.java`**: Centralized REST Assured client with base URI, default headers, auth tokens, and methods for GET, POST, PUT, DELETE, PATCH.

### 5. Step Definitions Layer (`com.bdd.steps`)
- `com.bdd.steps.ui`: BDD steps binding to Page Object methods.
- `com.bdd.steps.api`: BDD steps binding to REST Assured calls and response validations.
- `com.bdd.steps.database`: BDD steps for database assertions (`DbSteps.java`).

### 6. Lifecycle Hooks Layer (`com.bdd.hooks`)
- **`Hooks.java`**: Cucumber hooks annotated with `@Before` and `@After`:
  - Scoped to UI/Mobile tags (`@ui or @mobile or not @api`).
  - Automatically captures screenshots upon test failure and attaches them to the Cucumber report.
  - Automatically reports test execution status (`passed`/`failed`) to Sauce Labs.
  - Safely cleans up the `ThreadLocal` driver session via `DriverManager.quitDriver()`.

### 7. Runners Layer (`com.bdd.runners`)
- **`UiTestRunner.java`**: Runs UI/Mobile/DB feature files in parallel (`parallel = true`).
- **`ApiTestRunner.java`**: Runs API feature files in parallel without spinning up browsers.
