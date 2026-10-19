# Omnichannel BDD Test Automation Framework (Java 21)

Enterprise-grade test automation framework built for **Web UI**, **REST API**, **Mobile (Android & iOS via Appium)**, and **Database (SQL/JDBC)** testing using **Cucumber BDD**, **TestNG**, and **Selenium WebDriver**, with out-of-the-box support for **Sauce Labs** cloud execution.

> 📚 **Interactive Cheat Sheets & Quick References**:
> - [CSS & ID Selectors Guide (Accordion)](README_CSS_SELECTORS.md)
> - [Java & Automation Methods Guide (Accordion)](README_AUTOMATION_METHODS.md)

---


## ⚡ Quick Start & Common Usage Commands

This section provides a quick copy-paste cheat-sheet for running the application under test (AUT) and executing automated test suites.

### 1. Prerequisites Check
Ensure your environment meets the minimum version requirements:
```bash
java -version    # Java 21+ required
mvn -version     # Apache Maven 3.9+ required
docker --version # Docker 24+ (optional, for containerized run)
```

---

### 2. Starting the Application Under Test (AUT)

Choose between **Local Mode (H2 file database)** or **Containerized Mode (Docker + PostgreSQL 16)**:

#### Option A: Local Spring Boot 3 (Fastest, zero Docker dependencies)
```bash
# Start Spring Boot backend on http://localhost:8080 (in background or new terminal)
cd backend && mvn spring-boot:run
```

#### Option B: Full Docker Compose (Spring Boot + PostgreSQL 16)
```bash
# Build and run containers in detached mode
docker compose up -d --build

# Verify container health and logs
docker compose ps
curl http://localhost:8080/actuator/health

# Stop and wipe container volumes when finished
docker compose down -v
```

---

### 3. Running Automated UI Tests (Selenium + TestNG + Cucumber)

All UI tests run against `http://localhost:8080` by default.

```bash
# Run all UI tests (Login, Installments CRUD, and Security Redirects) in headless mode
mvn clean test -Dtest=UiTestRunner -Dheadless=true

# Run UI tests visually (opens Chrome browser window)
mvn test -Dtest=UiTestRunner -Dheadless=false

# Run a specific UI feature file directly
mvn test -Dtest=UiTestRunner -Dcucumber.features="src/test/resources/features/UI/installments.feature" -Dheadless=true

# Run by Cucumber Tag (e.g. Smoke tests only)
mvn test -Dtest=UiTestRunner -Dcucumber.filter.tags="@smoke" -Dheadless=true

# Run cross-browser on Firefox
mvn test -Dtest=UiTestRunner -Dbrowser=firefox -Dheadless=true

# Re-run ONLY failed UI scenarios from previous build
mvn test -Dtest=FailedUiTestRunner
```

---

### 4. Running Automated REST API Tests (REST Assured + JWT)

API tests validate Spring Security JWT issuance, bearer token authentication, and full CRUD operations.

```bash
# Run all API tests
mvn clean test -Dtest=ApiTestRunner

# Run only JWT Authentication & Authorization tests
mvn test -Dtest=ApiTestRunner -Dcucumber.filter.tags="@jwt"

# Run only CRUD & Database SQL verification tests
mvn test -Dtest=ApiTestRunner -Dcucumber.filter.tags="@crud"

# Override API Base URI dynamically
mvn test -Dtest=ApiTestRunner -Dapi.baseUri=http://localhost:8080/api
```

---

### 5. Running on Selenium Grid & Remote Hubs
```bash
# Run tests against a remote Selenium Grid hub with LocalFileDetector enabled
mvn test -Dtest=UiTestRunner -Dexecution=grid -Dgrid.hubUrl=http://localhost:4444/wd/hub

# Run tests in parallel across Selenium Grid nodes on Firefox
mvn test -Dtest=UiTestRunner -Dexecution=grid -Dbrowser=firefox -Dheadless=true
```

---

### 6. Running Cloud & Mobile Tests

```bash
# Run on Sauce Labs Cloud (Chrome)
mvn test -Dtest=UiTestRunner \
  -Dexecution=saucelabs \
  -Dsaucelabs.username="YOUR_USER" \
  -Dsaucelabs.accessKey="YOUR_KEY"

# Run Mobile on Local Android Emulator (via Appium 9+)
mvn test -Dtest=UiTestRunner -Dtarget=mobile -Dmobile.platformName=Android

# Run Mobile on iOS Simulator (via XCUITest)
mvn test -Dtest=UiTestRunner -Dtarget=mobile -Dmobile.platformName=iOS
```

---

### 7. Database Credentials & H2 Web Console

The application supports both local H2 and containerized PostgreSQL databases with pre-seeded users and relational tables (`users`, `installment_plans`, `payments`):

#### Database Connections
| Property | Local (H2 Database) | Docker (PostgreSQL 16) |
| :--- | :--- | :--- |
| **JDBC URL** | `jdbc:h2:file:./backend/data/testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;AUTO_SERVER=TRUE` | `jdbc:postgresql://localhost:5432/installments_db` |
| **Username** | `db_user` | `db_user` |
| **Password** | `db_password123` | `db_password123` |
| **Driver Class** | `org.h2.Driver` | `org.postgresql.Driver` |
| **Web Console** | `http://localhost:8080/h2-console` | (Use pgAdmin or psql) |

#### H2 Web Console Access Guide
1. Start the Spring Boot backend (`cd backend && mvn spring-boot:run`).
2. Open `http://localhost:8080/h2-console` in your browser.
3. Set **JDBC URL** to: `jdbc:h2:file:./data/testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;AUTO_SERVER=TRUE`
4. Set **User Name** to: `db_user`
5. Set **Password** to: `db_password123`
6. Click **Connect** to query tables (`SELECT * FROM installment_plans;`, `SELECT * FROM payments;`, `SELECT * FROM users;`).

#### Application Test Users (Web UI & API JWT)
| Username | Password | Role | Description |
| :--- | :--- | :--- | :--- |
| `testuser@example.com` | `testpassword` | `ROLE_USER` | Standard customer account for smoke & regression tests |
| `admin@example.com` | `adminpassword123` | `ROLE_ADMIN` | Administrator account with elevated privileges |
| `auditor@example.com` | `auditor123` | `ROLE_AUDITOR` | Read-only auditor account |

---

## 📑 Table of Contents
0. [⚡ Quick Start & Common Usage Commands](#-quick-start--common-usage-commands)
1. [Execution Matrix (All CLI Combinations)](#1-execution-matrix-all-cli-combinations)
2. [TestNG Architecture & Lifecycle Deep Dive](#2-testng-architecture--lifecycle-deep-dive)
3. [The 4 OOP Pillars in Java: Corporate & Framework Examples](#3-the-4-oop-pillars-in-java-corporate--framework-examples)
4. [Mobile Automation (Android & iOS Support)](#4-mobile-automation-android--ios-support)
5. [Sharing State Across Steps (The `ScenarioContext` Pattern)](#5-sharing-state-across-steps-the-scenariocontext-pattern)
6. [Hybrid Strategy: Combining API, UI & SQL](#6-hybrid-strategy-combining-api-ui--sql)
7. [Directory Structure](#7-directory-structure)

---

## 1. Execution Matrix (All CLI Combinations)

Configuration resolution follows a strict priority hierarchy:
`CLI System Properties (-D)` > `Environment Variables` > `src/main/resources/config.json`.

### A. Web UI Execution (Local Browsers)
```bash
# 1. Standard execution against default URL (config.json)
mvn clean test -Dtest=UiTestRunner

# 2. Override Base URL dynamically (e.g., local installments.jar on port 8080)
mvn test -Dtest=UiTestRunner -DbaseUrl=http://localhost:8080

# 3. Disable headless mode (visually observe browser in desktop Chrome)
mvn test -Dtest=UiTestRunner -Dheadless=false

# 4. Cross-browser: Run on Firefox (headless)
mvn test -Dtest=UiTestRunner -Dbrowser=firefox -Dheadless=true

# 5. Cross-browser: Run on Firefox (desktop window)
mvn test -Dtest=UiTestRunner -Dbrowser=firefox -Dheadless=false

# 6. Run a specific Cucumber tag (e.g., @smoke, @regression, @negative)
mvn test -Dtest=UiTestRunner -Dcucumber.filter.tags="@smoke"

# 7. Combined tag expression (Smoke tests that are NOT negative)
mvn test -Dtest=UiTestRunner -Dcucumber.filter.tags="@smoke and not @negative"

# 8. Run an exact feature file directly
mvn test -Dtest=UiTestRunner -Dcucumber.features="src/test/resources/features/UI/installments.feature"

# 9. Control parallel thread count (e.g., 4 concurrent scenarios)
mvn test -Dtest=UiTestRunner -Ddataproviderthreadcount=4
```

### B. REST API Execution (REST Assured + JWT)
```bash
# 1. Run all API scenarios (does not launch browser instances)
mvn clean test -Dtest=ApiTestRunner

# 2. Run Spring Boot Security JWT scenarios (Login, 401/403 access control)
mvn test -Dtest=ApiTestRunner -Dcucumber.filter.tags="@jwt"

# 3. Run full CRUD API scenarios with PostgreSQL / H2 database assertions
mvn test -Dtest=ApiTestRunner -Dcucumber.filter.tags="@crud"

# 4. Override API Base URI dynamically
mvn test -Dtest=ApiTestRunner -Dapi.baseUri=http://localhost:8080/api
```

### C. Docker & Microservice Execution
```bash
# 1. Run Spring Boot 3 locally with H2 in-memory/file database:
cd backend && mvn spring-boot:run

# 2. Start PostgreSQL 16 and Spring Boot 3 in Docker containers:
docker compose up -d --build

# 3. Check backend health in Docker:
curl http://localhost:8080/actuator/health

# 4. Stop Docker containers:
docker compose down -v
```

### D. Cloud Execution (Sauce Labs)
```bash
# 1. Desktop Web on Sauce Labs (Chrome)
mvn test -Dtest=UiTestRunner \
  -Dexecution=saucelabs \
  -Dsaucelabs.username="YOUR_USERNAME" \
  -Dsaucelabs.accessKey="YOUR_ACCESS_KEY" \
  -Dsaucelabs.region="us-west-1"

# 2. Desktop Web on Sauce Labs (Firefox)
mvn test -Dtest=UiTestRunner \
  -Dexecution=saucelabs \
  -Dbrowser=firefox \
  -Dsaucelabs.username="YOUR_USERNAME" \
  -Dsaucelabs.accessKey="YOUR_ACCESS_KEY"

# 3. Cloud Mobile: Android on Sauce Labs Real Device / Emulator
mvn test -Dtest=UiTestRunner \
  -Dexecution=saucelabs \
  -Dtarget=mobile \
  -Dmobile.platformName=Android \
  -Dmobile.deviceName="Google Pixel 7" \
  -Dsaucelabs.username="YOUR_USERNAME" \
  -Dsaucelabs.accessKey="YOUR_ACCESS_KEY"

# 4. Cloud Mobile: iOS on Sauce Labs Real Device / Simulator
mvn test -Dtest=UiTestRunner \
  -Dexecution=saucelabs \
  -Dtarget=mobile \
  -Dmobile.platformName=iOS \
  -Dmobile.deviceName="iPhone 15 Pro" \
  -Dsaucelabs.username="YOUR_USERNAME" \
  -Dsaucelabs.accessKey="YOUR_ACCESS_KEY"
```

### D. Mobile Execution (Local Appium Server)
```bash
# 1. Local Android (requires local Appium server running on port 4723)
mvn test -Dtest=UiTestRunner \
  -Dtarget=mobile \
  -Dmobile.platformName=Android \
  -Dmobile.android.deviceName="emulator-5554"

# 2. Local iOS (requires macOS with Xcode & Appium XCUITest driver)
mvn test -Dtest=UiTestRunner \
  -Dtarget=mobile \
  -Dmobile.platformName=iOS \
  -Dmobile.ios.deviceName="iPhone 15 Pro"
```

### E. Database Validation (Dynamic JDBC Override)
```bash
# Override PostgreSQL / MySQL / H2 connection string via CLI
mvn test -Dtest=UiTestRunner \
  -Ddatabase.url="jdbc:postgresql://localhost:5432/installments_db" \
  -Ddatabase.username="postgres" \
  -Ddatabase.password="secret123"
```

---

## 2. TestNG Architecture & Lifecycle Deep Dive

### Q1: Is TestNG only implemented in `Hooks.java`?
**No.** There is a critical architectural distinction between **Cucumber Hooks** and **TestNG Annotations**:

| Aspect | Cucumber Hooks (`io.cucumber.java.*`) | TestNG Annotations (`org.testng.annotations.*`) |
| :--- | :--- | :--- |
| **Scope** | Bound to **BDD Scenarios** & Steps. | Bound to **TestNG Test Suites, Classes, and Runners**. |
| **Annotations** | `@Before`, `@After`, `@BeforeStep`, `@AfterStep` | `@BeforeSuite`, `@AfterSuite`, `@BeforeClass`, `@DataProvider`, `@Listeners` |
| **Lifecycle** | Runs before/after every single Gherkin scenario. | Runs once per test suite or manages runner-level parallel threads. |
| **Role in this project** | [Hooks.java](file:///Users/user/Local/projects/seleniumBDD/src/test/java/com/bdd/hooks/Hooks.java) initializes the browser, takes failure screenshots, reports to Sauce Labs, and resets thread state. | [UiTestRunner.java](file:///Users/user/Local/projects/seleniumBDD/src/test/java/com/bdd/runners/UiTestRunner.java) extends `AbstractTestNGCucumberTests` to drive execution. |

### Q2: How is TestNG leveraged in this framework?

1. **Parallel Execution via `@DataProvider(parallel = true)`**:
   In [UiTestRunner.java](file:///Users/user/Local/projects/seleniumBDD/src/test/java/com/bdd/runners/UiTestRunner.java), TestNG feeds Cucumber scenarios into parallel worker threads:
   ```java
   @Override
   @DataProvider(parallel = true)
   public Object[][] scenarios() {
       return super.scenarios();
   }
   ```
2. **Hard Assertions vs Soft Assertions**:
   - **Hard Assertions (`Assert.assertEquals`)**: Immediately stops execution on failure (used in `@Then` verification steps).
   - **Soft Assertions (`SoftAssert`)**: Gathers multiple field validations across a large table or form without stopping prematurely on the first mismatch:
     ```java
     SoftAssert soft = new SoftAssert();
     soft.assertEquals(page.getStatus(), "ACTIVE");
     soft.assertEquals(page.getAmount(), "$1,200.00");
     soft.assertAll();
     ```
3. **TestNG Listeners (`ITestListener`, `IRetryAnalyzer`)**:
   - `IRetryAnalyzer`: Re-executes flaky UI tests automatically up to *N* times before marking as failed.
   - `ITestListener`: Custom logging, sending test metrics to Slack/Datadog, or timing execution durations.

---

## 3. The 4 OOP Pillars in Java: Corporate & Framework Examples

### 1. Encapsulation (Encapsulamiento)
*Keeping internal object state hidden, exposing only controlled, meaningful operations.*

- **Corporate Example**: In a banking core system, a `BankAccount` object has a `private double balance;`. You cannot write `account.balance = -500;`. Instead, you call public audited methods like `account.deposit(amount)` and `account.withdraw(amount)` which validate fraud checks and funds before updating the internal field.
- **In this Framework**:
  In [LoginPage.java](file:///Users/user/Local/projects/seleniumBDD/src/main/java/com/bdd/pages/LoginPage.java) and [InstallmentsPage.java](file:///Users/user/Local/projects/seleniumBDD/src/main/java/com/bdd/pages/InstallmentsPage.java), all WebElements are strictly `private`:
  ```java
  @FindBy(id = "username")
  private WebElement usernameField;
  ```
  Step definitions never touch `usernameField` or `driver.findElement` directly. They invoke public encapsulated actions:
  ```java
  public void enterUsername(String username) {
      type(usernameField, username);
  }
  ```

---

### 2. Inheritance (Herencia)
*Deriving new classes from parent classes to reuse common behavior and eliminate duplicate code.*

- **Corporate Example**: An e-commerce platform has a base class `Notification` with `send()`, `log()`, and `recipient`. Subclasses like `EmailNotification`, `SmsNotification`, and `PushNotification` inherit the base fields and add channel-specific payloads.
- **In this Framework**:
  Every Page Object extends [BasePage.java](file:///Users/user/Local/projects/seleniumBDD/src/main/java/com/bdd/pages/BasePage.java):
  ```java
  public class InstallmentsPage extends BasePage {
      public InstallmentsPage(WebDriver driver) {
          super(driver);
      }
  }
  ```
  By calling `super(driver)`, `InstallmentsPage` automatically inherits `type()`, `click()`, `selectMatOption()`, `getTableCellText()`, explicit `WebDriverWait`, and JavaScript click fallbacks without re-implementing them.

---

### 3. Polymorphism (Polimorfismo)
*The ability of different objects to respond to the same interface or method call in their own specific way (Subtyping / Method Overriding / Overloading).*

- **Corporate Example**: A payment gateway interface `PaymentProcessor` with method `processPayment(double amount)`. Whether the customer pays with `StripeProcessor`, `PayPalProcessor`, or `CryptoProcessor`, the calling checkout service invokes `paymentProcessor.processPayment(amount)` identically.
- **In this Framework**:
  - **Interface Polymorphism**: `WebDriver` is a polymorphic interface. In [DriverFactory.java](file:///Users/user/Local/projects/seleniumBDD/src/main/java/com/bdd/core/driver/DriverFactory.java), `createDriver()` returns `WebDriver`. At runtime, the actual concrete instance can be a `ChromeDriver`, a `FirefoxDriver`, a `RemoteWebDriver` (Sauce Labs), an `AndroidDriver`, or an `IOSDriver`. [BasePage.java](file:///Users/user/Local/projects/seleniumBDD/src/main/java/com/bdd/pages/BasePage.java) interacts with all of them identically through the `WebDriver` contract.
  - **Method Overloading**: `BasePage` overloads methods:
    `click(WebElement element)` and `click(By locator)`.
    `type(WebElement element, String text)` and `type(By locator, String text)`.

---

### 4. Abstraction (Abstracción)
*Hiding complex low-level implementation details behind clean, intuitive high-level interfaces.*

- **Corporate Example**: When you drive a car, you press the gas pedal to accelerate. You do not need to understand fuel injectors, air-fuel ratios, or spark plug timings. The pedal is the abstraction.
- **In this Framework**:
  - In [BasePage.java](file:///Users/user/Local/projects/seleniumBDD/src/main/java/com/bdd/pages/BasePage.java), when `click(WebElement)` is called, the caller does not care about `WebDriverWait`, `ExpectedConditions.elementToBeClickable()`, `ElementClickInterceptedException`, or `JavascriptExecutor.executeScript("arguments[0].click();")`. All synchronization and fallback complexity is abstracted away behind a clean `click(element)`.
  - In [ApiClient.java](file:///Users/user/Local/projects/seleniumBDD/src/main/java/com/bdd/api/ApiClient.java), calling `apiClient.get("/plans")` abstracts away `RequestSpecBuilder`, Content-Type headers, HTTP connection pools, and URI builders.

---

## 4. Mobile Automation (Android & iOS Support)

The framework natively handles **both Android (UiAutomator2) and iOS (XCUITest)** via Appium Java Client 9.x.

### Configuration Schema (`src/main/resources/config.json`)
```json
{
  "target": "mobile",
  "mobile": {
    "platformName": "Android",
    "android": {
      "deviceName": "emulator-5554",
      "platformVersion": "14.0",
      "automationName": "UiAutomator2",
      "app": "src/test/resources/apps/sample.apk",
      "appPackage": "com.example.installments",
      "appActivity": "com.example.installments.MainActivity"
    },
    "ios": {
      "deviceName": "iPhone 15 Pro",
      "platformVersion": "17.4",
      "automationName": "XCUITest",
      "app": "src/test/resources/apps/sample.app",
      "bundleId": "com.example.installments"
    }
  }
}
```

### Switching Platforms on the Fly:
```bash
# Switch to Android
mvn test -Dtest=UiTestRunner -Dtarget=mobile -Dmobile.platformName=Android

# Switch to iOS
mvn test -Dtest=UiTestRunner -Dtarget=mobile -Dmobile.platformName=iOS
```

### Tri-Platform Unified Page Object Model (POM)
Thanks to `AppiumFieldDecorator` in [BasePage.java](file:///Users/usermone/local/projects/seleniumBDD/src/main/java/com/bdd/pages/BasePage.java), a single Page Object class serves Web UI, Android Native, and iOS Native simultaneously:

```java
public class UnifiedLoginPage extends BasePage {

    @FindBy(id = "username")
    @AndroidFindBy(id = "com.example.app:id/input_username")
    @iOSXCUITFindBy(accessibility = "input_username")
    private WebElement usernameInput;

    @FindBy(id = "password")
    @AndroidFindBy(id = "com.example.app:id/input_password")
    @iOSXCUITFindBy(accessibility = "input_password")
    private WebElement passwordInput;

    @FindBy(id = "loginButton")
    @AndroidFindBy(id = "com.example.app:id/btn_login")
    @iOSXCUITFindBy(accessibility = "btn_login")
    private WebElement loginButton;

    public void login(String user, String pass) {
        type(usernameInput, user);
        type(passwordInput, pass);
        click(loginButton);
        hideKeyboard();
    }
}
```

### Modern W3C Mobile Gestures (`MobileGestureHelper`)
Appium 9 replaces legacy `TouchAction` with W3C Pointer actions:
```java
MobileGestureHelper gestures = new MobileGestureHelper(driver);

// Swipe up / down by percentage
gestures.swipe(Direction.UP, 0.5);

// Scroll down repeatedly until text is visible
WebElement element = gestures.scrollToText("Submit Order");

// Long press on an element
gestures.longPress(element, Duration.ofSeconds(2));

// Drag and drop between elements or coordinates
gestures.dragAndDrop(sourceElement, targetElement);

// Network emulation (Android/Emulators)
gestures.setAirplaneMode(true);
```

### Hybrid Apps & Context Switching (WebView / Native)
When your mobile app opens an in-app browser or WebView (SSO, payments, Terms of Service):
```java
// Inside any Page Object extending BasePage:
switchToWebView();          // Switches to active WEBVIEW context
click(paymentButton);       // Interacts with web elements inside the app
switchToNativeApp();        // Returns seamlessly to NATIVE_APP
```

### Failure Diagnostics & Enterprise Utilities
- **Browser Console Logs & Video Recording** ([Hooks.java](file:///Users/usermone/local/projects/seleniumBDD/src/test/java/com/bdd/hooks/Hooks.java)): On scenario failure, JavaScript console errors (`LogType.BROWSER`) and mobile MP4 video recordings are automatically attached to the Cucumber HTML report.
- **File Download Manager** ([FileManager.java](file:///Users/usermone/local/projects/seleniumBDD/src/main/java/com/bdd/utils/FileManager.java)): Polls and verifies PDF, CSV, and Excel downloads in headless and remote Grid environments.
- **Dynamic Test Data** ([TestDataUtils.java](file:///Users/usermone/local/projects/seleniumBDD/src/main/java/com/bdd/utils/TestDataUtils.java)): Generates collision-free timestamped emails, unique IDs, and relative dates (`getPastDate(30)`, `getFutureDate(15)`).
- **Auto-Retry Listener** ([RetryListener.java](file:///Users/usermone/local/projects/seleniumBDD/src/test/java/com/bdd/utils/RetryListener.java)): Binds TestNG's `RetryAnalyzer` dynamically to automatically retry failed tests.

---

## 5. Sharing State Across Steps (The `ScenarioContext` Pattern)

In Python Behave, you share variables using `context.my_var = value`.
In this framework, [ScenarioContext.java](file:///Users/user/Local/projects/seleniumBDD/src/main/java/com/bdd/utils/ScenarioContext.java) replicates this exact capability in a **thread-safe** manner using `ThreadLocal<Map<String, Object>>`:

### Storing Data in Step 1 (e.g., API creation):
```java
// Inside an API Step:
Response response = apiClient.post("/api/plans", body);
String createdPlanId = response.jsonPath().getString("id");
ScenarioContext.set("planId", createdPlanId);
```

### Retrieving Data in Step 2 (e.g., UI or Database validation):
```java
// Inside a UI Step:
String planId = ScenarioContext.get("planId", String.class);
installmentsPage.clickPayForPlan(planId);
```

### Automatic Teardown:
In [Hooks.java](file:///Users/user/Local/projects/seleniumBDD/src/test/java/com/bdd/hooks/Hooks.java), `@After` automatically invokes `ScenarioContext.clear()` to prevent data leaking into subsequent tests.

---

## 6. Hybrid Strategy: Combining API, UI & SQL

In high-velocity enterprise teams and 60-minute live coding interviews, pure UI end-to-end tests are slow. We employ a **hybrid approach**:

```text
[ 1. Seed via API ]           [ 2. Execute via UI ]          [ 3. Verify via SQL / API ]
POST /api/plans (100ms)  ───> Navigate & Pay via UI   ───> SELECT * FROM plans (10ms)
Fast test data setup          Verify user experience         Verify database persistence
```

- **Step 1**: Use [ApiClient.java](file:///Users/user/Local/projects/seleniumBDD/src/main/java/com/bdd/api/ApiClient.java) to create test entities in milliseconds without filling long forms.
- **Step 2**: Use [BasePage.java](file:///Users/user/Local/projects/seleniumBDD/src/main/java/com/bdd/pages/BasePage.java) to validate the visual flow and payment action.
- **Step 3**: Use [DbManager.java](file:///Users/user/Local/projects/seleniumBDD/src/main/java/com/bdd/core/database/DbManager.java) to execute SQL assertions verifying data consistency in the backend.

---

## 8. Ordered & Sequential Execution Strategies

When business rules demand strict sequential scenario ordering (e.g., Onboarding -> KYC Approval -> Loan Issuance -> Payment):

### Strategy A: Sequential Execution per Feature (Default Cucumber Behavior)
By disabling TestNG parallelism (`parallel = false` in `DataProvider`), Cucumber executes scenarios **strictly top-to-bottom** in the order they appear in the `.feature` file:
```java
@Override
@DataProvider(parallel = false)
public Object[][] scenarios() {
    return super.scenarios();
}
```

### Strategy B: Phased Tag-Based Pipelines (Enterprise Standard)
Instead of forcing sequential execution across 100 tests, divide business stages using tags in your CI/CD pipeline:
```bash
# Phase 1: Setup master accounts sequentially
mvn test -Dtest=UiTestRunner -Dcucumber.filter.tags="@phase1_setup" -Ddataproviderthreadcount=1

# Phase 2: Run core operations in parallel (10 threads)
mvn test -Dtest=UiTestRunner -Dcucumber.filter.tags="@phase2_core" -Ddataproviderthreadcount=10
```

---

## 9. Flaky Test Retries (How Retry is Implemented)

The framework supports three complementary retry mechanisms:

### Method 1: Automatic Re-run via `FailedUiTestRunner` (CI/CD Best Practice)
The primary runner [UiTestRunner.java](file:///Users/user/Local/projects/seleniumBDD/src/test/java/com/bdd/runners/UiTestRunner.java) outputs failed scenario paths into `target/failed_rerun.txt`.
To re-run **only** the failed scenarios after the main build:
```bash
# In your pipeline step:
mvn test -Dtest=FailedUiTestRunner
```

### Method 2: Dynamic Cucumber Native Retry CLI Flag
In Cucumber 6+, pass the retry count directly via Maven without modifying code:
```bash
mvn test -Dtest=UiTestRunner -Dcucumber.filter.rerun-count=2
```
If a scenario encounters a transient network glitch, Cucumber automatically retries it up to 2 additional times before recording a failure.

### Method 3: TestNG `RetryAnalyzer`
The framework includes [RetryAnalyzer.java](file:///Users/user/Local/projects/seleniumBDD/src/main/java/com/bdd/utils/RetryAnalyzer.java), which implements TestNG's `IRetryAnalyzer` to intercept failed test results and retry them programmatically.

---

## 10. Directory Structure

```text
seleniumBDD/
├── pom.xml                               # Dependencies (Selenium, Appium, Cucumber, TestNG, REST Assured)
├── README.md                             # Comprehensive Framework Reference Guide
├── docker-compose.yml                    # Multi-container orchestration (PostgreSQL 16 + Spring Boot 3)
├── backend/                              # Dedicated Spring Boot 3 Microservice (AUT)
│   ├── pom.xml                           # Spring Boot 3, Spring Security 6, JJWT, JPA, Thymeleaf
│   ├── Dockerfile                        # Multi-stage container build
│   └── src/main/
│       ├── java/com/installments/backend/
│       │   ├── config/                   # SecurityConfig, JwtTokenProvider, JwtAuthenticationFilter
│       │   ├── controller/               # AuthController, PlanController, WebUiController
│       │   ├── model/                    # User, InstallmentPlan, AuthRequest, AuthResponse, PlanDto
│       │   └── repository/               # UserRepository, InstallmentPlanRepository
│       └── resources/
│           ├── application.yml           # Multi-profile config (local H2 / docker PostgreSQL)
│           └── templates/                # login.html, dashboard.html
├── src/
│   ├── main/
│   │   ├── java/com/bdd/
│   │   │   ├── api/ApiClient.java        # Centralized REST Assured client (JWT Bearer support)
│   │   │   ├── config/ConfigReader.java  # Multi-level hierarchical configuration reader
│   │   │   ├── core/
│   │   │   │   ├── DriverManager.java    # ThreadLocal<WebDriver> lifecycle
│   │   │   │   ├── database/DbManager.java # Generic JDBC database client
│   │   │   │   └── driver/
│   │   │   │       ├── DriverFactory.java     # Instantiates Local & Cloud drivers (Web/Mobile)
│   │   │   │       └── SauceLabsManager.java  # Sauce Labs capabilities & status reporter
│   │   │   ├── pages/                    # Page Object Model layer
│   │   │   │   ├── BasePage.java         # "Swiss Army Knife" interaction & wait engine
│   │   │   │   ├── LoginPage.java        # Standard POM (encapsulated WebElements)
│   │   │   │   └── InstallmentsPage.java  # Table & action handlers for installments
│   │   │   └── utils/
│   │   │       └── ScenarioContext.java  # Thread-safe cross-step state manager
│   │   └── resources/
│   │       └── config.json               # Environment config (Web, API, Mobile, DB, SauceLabs)
│   └── test/
│       ├── java/com/bdd/
│       │   ├── hooks/Hooks.java          # Screenshots on failure & SauceLabs reporting
│       │   ├── runners/
│       │   │   ├── UiTestRunner.java     # Primary parallel TestNG runner
│       │   │   ├── FailedUiTestRunner.java # Dedicated runner for re-executing failed tests
│       │   │   └── ApiTestRunner.java    # REST API test runner
│       │   ├── steps/                    # Step definitions (ui/, api/, database/)
│       │   └── utils/
│       │       └── RetryAnalyzer.java    # TestNG flaky test retry analyzer
│       └── resources/features/           # Gherkin feature files (UI/ and API/)
│           ├── UI/                       # login.feature, installments.feature
│           └── API/                      # api.feature, auth_jwt.feature, crud_plans.feature
```
