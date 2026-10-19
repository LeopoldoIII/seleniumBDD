# ⚡ Java & Automation Methods Quick-Reference Guide

> **Interactive Accordion & Visual Cheat Sheet**  
> A quick reference guide covering essential Java syntax (Collections, Loops, Streams, Strings) and common automation methods (Selenium WebDriver, Explicit Waits, Actions, JS Executor, BasePage helpers, TestNG assertions, and REST Assured).

---

## 📑 Table of Contents (Accordion)

> Click any section below to expand or collapse syntax rules and code snippets.

<details>
<summary><b>1. Java Collections: Initializing Lists, Maps, and Sets</b></summary>
<br>

Never forget how to instantiate collections again. Compare mutable vs. immutable one-liners:

### 📋 List (`List<T>`)

| Type | Syntax | Characteristics |
| :--- | :--- | :--- |
| **Mutable (Standard)** | `List<String> list = new ArrayList<>();` | Elements can be added, removed, and modified. |
| **Immutable (One-liner)** | `List<String> list = List.of("A", "B", "C");` | Java 9+. Fixed size, cannot be modified (`UnsupportedOperationException`). |
| **Fixed-Size (Classic)** | `List<String> list = Arrays.asList("A", "B", "C");` | Backed by array. Allows `.set()`, but not `.add()` or `.remove()`. |
| **Mutable with Preloaded Data** | `List<String> list = new ArrayList<>(List.of("A", "B"));` | Prepopulated and fully mutable. |

```java
// Common List Methods:
list.add("Apple");              // Add item
list.add(0, "Banana");          // Insert at index
String item = list.get(0);      // Access by index
list.remove("Apple");           // Remove by value or index
int total = list.size();        // Number of elements
boolean has = list.contains("A"); // Check existence
boolean empty = list.isEmpty(); // Check if empty
list.clear();                   // Remove all items
```

---

### 🗺️ Map (`Map<K, V>`)

| Type | Syntax | Characteristics |
| :--- | :--- | :--- |
| **Mutable (Standard)** | `Map<String, String> map = new HashMap<>();` | Key-value pairs. Can add and update entries. |
| **Immutable (Up to 10 entries)** | `Map<String, String> map = Map.of("key1", "val1", "key2", "val2");` | Java 9+. Cannot be modified, keys and values cannot be null. |
| **Immutable (10+ entries)** | `Map<String, Object> map = Map.ofEntries(Map.entry("k1", "v1"), Map.entry("k2", 2));` | Java 9+. Clean key-value pairs for larger maps. |
| **Mutable with Initial Values** | `Map<String, String> map = new HashMap<>(Map.of("role", "admin"));` | Prepopulated and fully mutable. |

```java
// Common Map Methods:
map.put("username", "admin");         // Add or replace key
String user = map.get("username");    // Get value by key (returns null if absent)
String role = map.getOrDefault("role", "guest"); // Fallback default value
boolean hasKey = map.containsKey("id"); // Check key presence
boolean hasVal = map.containsValue("admin");
map.remove("username");               // Remove entry
int size = map.size();                // Number of pairs
```

---

### 📦 Set (`Set<T>`) — Eliminate Duplicates

```java
// Mutable Set
Set<String> uniqueTags = new HashSet<>();
uniqueTags.add("smoke");
uniqueTags.add("smoke"); // Ignored, duplicate!

// Immutable Set
Set<String> roles = Set.of("ADMIN", "USER", "GUEST");

// Convert a List with duplicates into a unique Set
List<String> rawList = List.of("A", "B", "A", "C");
Set<String> cleanSet = new HashSet<>(rawList); // ["A", "B", "C"]
```

</details>

---

<details>
<summary><b>2. Loops, Iterations & Streams: For-Each, Maps, and Lambdas</b></summary>
<br>

Visual syntax for all common iteration patterns:

### 🔁 The Classic For-Each Loop (List / Array)

```java
List<String> usernames = List.of("Alice", "Bob", "Charlie");

// Standard for-each
for (String user : usernames) {
    System.out.println("Processing user: " + user);
}

// Indexed loop (when index position is required)
for (int i = 0; i < usernames.size(); i++) {
    System.out.println("User at " + i + ": " + usernames.get(i));
}
```

---

### 🗺️ Iterating Over Maps (`entrySet`)

```java
Map<String, String> headers = Map.of("Accept", "application/json", "Authorization", "Bearer token123");

// Recommended: Map.Entry (gives access to both key and value)
for (Map.Entry<String, String> entry : headers.entrySet()) {
    String key = entry.getKey();
    String value = entry.getValue();
    System.out.println(key + " -> " + value);
}

// Keys only:
for (String key : headers.keySet()) {
    System.out.println("Key: " + key);
}

// Functional forEach (Lambda):
headers.forEach((key, val) -> System.out.println(key + " = " + val));
```

---

### 🌊 Stream API (Filter, Transform, Find, Collect)

```java
List<WebElement> rows = driver.findElements(By.cssSelector("table tbody tr"));

// 1. Extract text from each element (map) into a List<String>
List<String> rowTexts = rows.stream()
    .map(WebElement::getText)
    .toList(); // or .collect(Collectors.toList()) in Java 11

// 2. Filter elements matching a condition
List<WebElement> activeRows = rows.stream()
    .filter(row -> row.getText().contains("ACTIVE"))
    .toList();

// 3. Find the FIRST element that matches a condition
WebElement targetRow = rows.stream()
    .filter(row -> row.getText().contains("Invoice #1042"))
    .findFirst()
    .orElse(null);

// 4. Check if ANY element matches
boolean hasError = rows.stream()
    .anyMatch(row -> row.getText().contains("FAILED"));
```

</details>

---

<details>
<summary><b>3. String Utilities & Number Parsing (Crucial for QA Validations)</b></summary>
<br>

Common operations for cleaning up UI labels, prices, and error texts.

### ✂️ String Cleaning & Splitting

```java
String rawText = "  Total Price: $1,250.75 USD  ";

// Trim whitespaces
String clean = rawText.trim(); // "Total Price: $1,250.75 USD"

// Split by delimiter
String[] parts = clean.split(":");
String label = parts[0].trim(); // "Total Price"
String value = parts[1].trim(); // "$1,250.75 USD"

// Replace / Substring
String upper = clean.toUpperCase();
String lower = clean.toLowerCase();
boolean contains = clean.toLowerCase().contains("total");

// Format strings dynamically
String xpath = String.format("//td[text()='%s']/following-sibling::td", "User123");
```

---

### 🔢 Extracting Numbers from UI Text (Currency, Counts, IDs)

```java
// Extract pure digits from "$1,250.75" or "Order #98214"
String priceText = "$1,250.75";

// Remove everything that is NOT a digit or dot:
String numericString = priceText.replaceAll("[^0-9.]", ""); // "1250.75"
double price = Double.parseDouble(numericString);           // 1250.75

// Extract integer count from "Showing 42 items":
String itemsText = "Showing 42 items";
String digitsOnly = itemsText.replaceAll("[^0-9]", "");      // "42"
int count = Integer.parseInt(digitsOnly);                   // 42
```

</details>

---

<details>
<summary><b>4. Selenium WebDriver Core Methods: Clicks, Typing, and Attributes</b></summary>
<br>

The bread and butter of UI automation.

```java
WebElement element = driver.findElement(By.id("username"));

// Basic interactions
element.click();                      // Click element
element.sendKeys("admin@test.com");   // Type text
element.clear();                      // Clear existing value before typing
element.sendKeys(Keys.ENTER);         // Send keyboard keys

// Reading text & attributes
String visibleText = element.getText();                 // Visible inner text
String inputValue  = element.getAttribute("value");      // Current value inside <input>
String cssClass    = element.getAttribute("class");      // Class attribute
String placeholder = element.getAttribute("placeholder");

// State checks
boolean isVisible  = element.isDisplayed(); // Rendered on screen
boolean isClickable = element.isEnabled();   // Not disabled
boolean isTicked   = element.isSelected();  // Checkbox/Radio selected
```

</details>

---

<details>
<summary><b>5. Dynamic Explicit Waits (`WebDriverWait` & `ExpectedConditions`)</b></summary>
<br>

Never use `Thread.sleep()`. Use explicit waits for resilient, non-flaky tests:

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

// Wait for element to be visible
WebElement element = wait.until(ExpectedConditions.visibilityOf(myWebElement));
WebElement byLocator = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("dashboard")));

// Wait for element to be clickable (visible AND enabled)
WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.id("submit-btn")));

// Wait for loading spinner / overlay to disappear
boolean gone = wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".spinner-loader")));

// Wait for specific text to appear inside element
wait.until(ExpectedConditions.textToBePresentInElement(element, "Payment Successful"));

// Wait for URL to contain token
wait.until(ExpectedConditions.urlContains("/dashboard"));

// Wait for JavaScript alert to pop up
Alert alert = wait.until(ExpectedConditions.alertIsPresent());
```

</details>

---

<details>
<summary><b>6. Dropdowns (`Select`), Actions API, and JavaScript Executor</b></summary>
<br>

### 🔽 Dropdown Selection (`<select>` HTML tags)

```java
import org.openqa.selenium.support.ui.Select;

Select dropdown = new Select(driver.findElement(By.id("country-select")));

dropdown.selectByVisibleText("United States"); // Preferred: by visible user text
dropdown.selectByValue("US");                 // By <option value="US">
dropdown.selectByIndex(1);                    // By 0-based index

// Get current selected option text:
String selectedText = dropdown.getFirstSelectedOption().getText();

// Get all options:
List<WebElement> options = dropdown.getOptions();
```

---

### 🖱️ Advanced User Actions (`Actions` class)

```java
import org.openqa.selenium.interactions.Actions;

Actions actions = new Actions(driver);

// Hover over element (Tooltip / Dropdown menu)
actions.moveToElement(menuItem).perform();

// Drag and Drop
actions.dragAndDrop(sourceElement, targetElement).perform();

// Double Click
actions.doubleClick(tableRow).perform();

// Right Click (Context Menu)
actions.contextClick(targetElement).perform();

// Key Combinations (e.g. CTRL+A / CMD+A)
actions.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).perform();
```

---

### ⚡ JavaScript Executor Fallbacks

When standard Selenium `.click()` or scrolling fails due to overlays or sticky headers:

```java
JavascriptExecutor js = (JavascriptExecutor) driver;

// 1. Force click an obscured or overlapped element
js.executeScript("arguments[0].click();", blockedButton);

// 2. Scroll element into view (center of screen)
js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);

// 3. Scroll to bottom or top of page
js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
js.executeScript("window.scrollTo(0, 0);");

// 4. Force set value into input field
js.executeScript("arguments[0].value='custom_token';", inputElement);
```

</details>

---

<details>
<summary><b>7. Browser Tabs, Windows, Alerts, and Iframes</b></summary>
<br>

### 🪟 Switching Browser Tabs / Windows

```java
String mainWindow = driver.getWindowHandle();

// Open new tab / click link that opens new window...
for (String handle : driver.getWindowHandles()) {
    if (!handle.equals(mainWindow)) {
        driver.switchTo().window(handle);
        break; // Now operating in the new tab!
    }
}

// Close child tab and return to main window
driver.close();
driver.switchTo().window(mainWindow);
```

---

### ⚠️ Native JavaScript Alerts (`alert`, `confirm`, `prompt`)

```java
Alert alert = driver.switchTo().alert();
String alertMsg = alert.getText(); // Read alert message
alert.accept();                    // Click OK / Accept
// alert.dismiss();                // Click Cancel
```

---

### 🖼️ Iframes

```java
// Switch into iframe by ID, Name, or WebElement
driver.switchTo().frame("payment-iframe");
// driver.switchTo().frame(driver.findElement(By.cssSelector("iframe.checkout")));

// Do interactions inside the iframe...
driver.findElement(By.id("card-number")).sendKeys("4111222233334444");

// ALWAYS return to main document after iframe operations!
driver.switchTo().defaultContent();
```

</details>

---

<details>
<summary><b>8. Framework Swiss Army Knife: `BasePage` Helpers in this Repo</b></summary>
<br>

All Page Objects in this repository extend `com.bdd.pages.BasePage`. Instead of repeating raw WebDriver code, use these built-in methods:

| Category | BasePage Helper Method | What it does |
| :--- | :--- | :--- |
| **Navigation** | `navigateTo(url)` / `navigateToBaseUrl()` | Loads target URL |
| **Clicking** | `click(WebElement)` / `click(By)` | Waits for clickability, highlights, and clicks with auto-scroll |
| **Click Fallback** | `jsClick(WebElement)` | Forces click via JavaScript when intercepted |
| **Typing** | `type(WebElement, "text")` | Waits for visibility, clears, and inputs text |
| **Reading Text** | `getText(WebElement)` | Waits for visibility and returns clean `.trim()` text |
| **Dropdowns** | `selectByVisibleText(WebElement, "Option")` | Selects `<select>` option by text |
| **Visibility** | `isDisplayed(WebElement)` | Returns `true`/`false` safely with 2s short timeout |
| **Presence** | `isElementPresent(By)` | Checks presence without throwing `NoSuchElementException` |
| **Scrolling** | `scrollIntoView(WebElement)` | Smoothly scrolls element into view |
| **Hover** | `hoverOver(WebElement)` | Moves mouse pointer over target |
| **Tables** | `getTableRowCount(tableElement)` | Returns count of `<tr>` elements in `<tbody>` |
| **Tables** | `findRowByText(tableElement, "Alice")` | Finds specific row containing text |
| **Mobile** | `hideKeyboard()` | Dismisses soft keyboard on Android / iOS |

#### 💡 Example inside your Page Object:

```java
public class CheckoutPage extends BasePage {

    @FindBy(id = "customer-name")
    private WebElement customerInput;

    @FindBy(css = "button.btn-checkout")
    private WebElement checkoutBtn;

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    public void completeCheckout(String name) {
        type(customerInput, name); // Clean BasePage helper!
        click(checkoutBtn);        // Clean BasePage helper!
    }
}
```

</details>

---

<details>
<summary><b>9. TestNG Assertions (Hard vs. Soft Assertions)</b></summary>
<br>

### 🛑 Hard Assertions (Fails test immediately on first failure)

```java
import org.testng.Assert;

// Equality
Assert.assertEquals(actualTitle, "Dashboard", "Page title mismatch!");
Assert.assertNotEquals(actualStatus, "FAILED");

// Boolean conditions
Assert.assertTrue(isDisplayed, "Expected element to be visible");
Assert.assertFalse(hasError, "Unexpected error banner appeared");

// Nullity
Assert.assertNotNull(userToken, "Token should not be null");
Assert.assertNull(errorMessage, "Expected no error message");
```

---

### 🍦 Soft Assertions (Runs all assertions and reports all failures at the end)

```java
import org.testng.asserts.SoftAssert;

SoftAssert soft = new SoftAssert();

soft.assertEquals(user.getFirstName(), "John", "First name incorrect");
soft.assertEquals(user.getLastName(), "Doe", "Last name incorrect");
soft.assertTrue(user.isActive(), "User should be active");

// CRUCIAL: Must call .assertAll() at the end to evaluate!
soft.assertAll();
```

</details>

---

<details>
<summary><b>10. REST Assured API Testing Quick-Reference</b></summary>
<br>

Common patterns for sending HTTP requests and validating responses in API automation:

```java
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

// 1. GET Request with Query Parameters
Response getResponse = given()
    .baseUri("https://api.example.com")
    .queryParam("status", "ACTIVE")
    .header("Authorization", "Bearer " + token)
.when()
    .get("/users")
.then()
    .statusCode(200)
    .extract().response();

// Extract JSON values with jsonPath:
String firstUserName = getResponse.jsonPath().getString("users[0].name");
int totalUsers       = getResponse.jsonPath().getInt("total");
List<String> allIds  = getResponse.jsonPath().getList("users.id");

// 2. POST Request with JSON Payload
Map<String, Object> payload = Map.of(
    "name", "Installment Plan A",
    "amount", 500.0,
    "active", true
);

Response postResponse = given()
    .baseUri("http://localhost:8080/api")
    .contentType(ContentType.JSON)
    .body(payload)
.when()
    .post("/installments")
.then()
    .statusCode(201)
    .extract().response();

int createdId = postResponse.jsonPath().getInt("id");
```

</details>

---

## 🚀 Quick Syntax Reminders

| Need to do... | Write this: |
| :--- | :--- |
| **New empty List** | `List<String> list = new ArrayList<>();` |
| **Fixed List with items** | `List<String> list = List.of("A", "B", "C");` |
| **New empty Map** | `Map<String, Object> map = new HashMap<>();` |
| **Quick Map with items** | `Map<String, String> map = Map.of("key", "value");` |
| **Iterate over Map** | `for (Map.Entry<K, V> e : map.entrySet()) { ... }` |
| **Number from String** | `Integer.parseInt(str.replaceAll("[^0-9]", ""))` |
| **Decimal from Currency** | `Double.parseDouble(str.replaceAll("[^0-9.]", ""))` |
| **Wait for Clickable** | `wait.until(ExpectedConditions.elementToBeClickable(elem))` |
| **JS Force Click** | `((JavascriptExecutor) driver).executeScript("arguments[0].click();", elem);` |
