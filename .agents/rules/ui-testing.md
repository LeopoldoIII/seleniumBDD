# UI & Mobile Automation & Page Object Model Rules

## Page Object Guidelines

1. **Inheritance & Construction**:
   - Every page object class must extend `com.bdd.pages.BasePage`.
   - The constructor must take `WebDriver driver` and invoke `super(driver)`, or a zero-arg constructor calling `super()`.
   - Never declare `WebElement` or page actions as `static` (which breaks multi-threaded parallel execution).
   ```java
   public class DashboardPage extends BasePage {
       public DashboardPage(WebDriver driver) {
           super(driver);
       }
   }
   ```

2. **Element Identification**:
   - Use `@FindBy` annotations with resilient locators (ID, CSS selector, data-testid, or semantic xpath).
   - Prefer CSS selectors or IDs over fragile, absolute XPath expressions.
   ```java
   @FindBy(id = "user-profile-menu")
   private WebElement userProfileMenu;

   @FindBy(css = "button[data-action='submit']")
   private WebElement submitButton;
   ```

3. **Use BasePage Actions ("Navaja Suiza")**:
   - Instead of calling raw `element.click()` or `element.sendKeys()`, utilize the resilient methods provided by `BasePage`:
     - `click(element)` (auto-waits for clickability and falls back to JavaScript click if intercepted)
     - `type(element, text)` (auto-waits for visibility, clears, and types)
     - `clearAndType(element, text)` (replaces text triggering framework change events)
     - `getText(element)` (auto-waits for visibility)
     - `selectByVisibleText(element, text)`, `selectByValue(element, value)`
     - `setCheckbox(element, boolean)`
     - `scrollToElement(element)`
     - `getTableCellText(tableLocator, row, col)`, `findRowIndexContainingText(tableLocator, text)`

4. **Encapsulation & Action Methods**:
   - WebElements should be `private` or `protected`.
   - Expose public methods that represent user actions or query page state (e.g., `submitForm()`, `getWelcomeMessage()`).
   - Do not perform TestNG assertions inside Page Objects. Assertions belong in Step Definitions.

5. **Synchronization & Waits**:
   - Never use `Thread.sleep()` in test code.
   - Rely on `BasePage`'s built-in explicit waits (`waitForVisibility`, `waitForClickable`) or `wait.until(...)`.

6. **Driver Lifecycle & Parallel Execution**:
   - Never instantiate `ChromeDriver`, `FirefoxDriver`, or `AppiumDriver` directly in tests.
   - Always rely on `DriverManager.getDriver()` (which manages a `ThreadLocal<WebDriver>` session).
   - Browser setup and teardown are handled automatically via `Hooks.java`.

7. **Omnichannel Execution (Web, Mobile, Sauce Labs)**:
   - Web desktop vs Mobile (Android / iOS) vs Sauce Labs is switchable via `config.json` or CLI flags (`-Dexecution=saucelabs`, `-Dtarget=mobile`).
