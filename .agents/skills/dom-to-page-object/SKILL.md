---
name: dom-to-page-object
description: >-
  Use this skill when parsing HTML snippets, full DOM dumps, or web page source code to automatically
  extract resilient Selenium @FindBy locators, generate full Page Object Model classes extending BasePage,
  action methods, step definitions, and Gherkin scenarios.
---

# DOM to Page Object Model Generator

Use this runbook to transform raw HTML snippets, page source code, or full DOM extracts into enterprise-ready Page Object classes, Step Definitions, and Gherkin scenarios.

---

## 🎯 What This Skill Generates

When provided with an HTML snippet or full DOM:
1. **Resilient `@FindBy` Locators**: Follows strict selector hierarchy (ID > data-testid > Name > semantic CSS > semantic XPath).
2. **Complete Page Object Class**: Extends `BasePage`, zero static elements, fully encapsulated.
3. **Atomic Action Methods**: Wraps inputs (`type`), buttons (`click`), selects (`selectByVisibleText`), and checks (`isDisplayed`).
4. **Composite Workflow Methods**: Combines form interactions into single high-level methods (e.g. `fillAndSubmitForm(...)`).
5. **Dynamic Component Helpers**:
   - **Tables**: Row counting, finding row by text, cell extraction.
   - **Error / Success Banners**: Alert text getters and visibility checks.
   - **Spinners / Loaders**: Waits for loading indicators to disappear.
6. **Matching Step Definitions**: Ready-to-use Cucumber glue code in `com.bdd.steps.ui`.
7. **Matching Gherkin Scenario**: Ready-to-use `.feature` file snippet.

---

## 🏆 Locator Resolution Hierarchy

When selecting locators, follow this exact fallback hierarchy. If an element has NO `id` or has a dynamic auto-generated ID (e.g. `id=":r1:"` or hashes), evaluate down the list:

| Priority | Locator Strategy | Syntax Pattern | Best For |
| :--- | :--- | :--- | :--- |
| **1 (Best)** | Unique Semantic ID | `@FindBy(id = "customerName")` | Static, meaningful IDs. |
| **2 (Angular/QA)** | Angular Form / Test Hooks | `@FindBy(css = "input[formcontrolname='username']")`<br>`@FindBy(css = "[data-testid='pay-btn']")` | **Angular Reactive Forms** (`formControlName`) & QA hooks (`data-testid`, `data-qa`). |
| **3** | Form Name / Type | `@FindBy(name = "email")`<br>`@FindBy(css = "input[type='password']")` | Standard form fields. |
| **4** | Accessibility Attributes | `@FindBy(css = "[aria-label='Close dialog']")`<br>`@FindBy(css = "input[placeholder='Enter amount']")` | Inputs with placeholders, icons, buttons with ARIA. |
| **5** | Normalized Text | `@FindBy(xpath = "//button[normalize-space()='Pay Now']")`<br>`@FindBy(xpath = "//a[normalize-space()='Plans']")` | Buttons, links, navigation items without classes. |
| **6** | Label-to-Input Binding | `@FindBy(xpath = "//label[contains(text(), 'Amount')]/following-sibling::input")` | Inputs without identifiers adjacent to a descriptive `<label>`. |
| **7** | Dynamic Table Row Anchor | `@FindBy(xpath = "//tr[td[contains(text(),'PLAN-101')]]//button")` | Actions inside table rows anchored to a specific record text. |

### 🅰️ Angular & Angular Material Detection Engine
When the parsed DOM contains Angular markers (`formcontrolname`, `<mat-select>`, `<app-...>`, `cdk-overlay`):
1. **Angular Reactive Forms**:
   - Detect `formControlName="fieldName"`.
   - Generate: `@FindBy(css = "[formcontrolname='fieldName']")`
   - Use `clearAndType(element, text)` in action methods so Angular's Change Detection pipeline registers the value change and updates form validity.
2. **Angular Material Dropdowns (`<mat-select>`)**:
   - **Do NOT** use `Select` from Selenium (will throw `UnexpectedTagNameException`).
   - Generate locator: `@FindBy(css = "mat-select[formcontrolname='fieldName']")`
   - Generate action method using `selectMatOption(element, "Option Text")` inherited from `BasePage`.
3. **Angular Material Checkboxes & Toggles (`<mat-checkbox>`, `<mat-slide-toggle>`)**:
   - Locate the host element directly: `@FindBy(css = "mat-checkbox[formcontrolname='terms']")`.
   - Action method uses `click(element)` which handles the underlying hidden input via JS click fallback.
4. **CDK Overlays & Snackbars (Modals & Alerts)**:
   - Angular renders dialogs and toasts in `<div class="cdk-overlay-container">`.
   - For snackbars/toasts: `@FindBy(css = ".mat-mdc-snack-bar-label, .cdk-overlay-pane .alert")`.

### ⚠️ Strict Rules for Robust Selectors
1. **Discard Auto-Generated IDs**: If an ID contains random alphanumeric hashes (e.g. `id="ember123"`, `id=":r1:"`, `id="j_idt4"`), treat it as having NO ID.
2. **Avoid Compiled CSS Hashes**: Do not use CSS classes from CSS-in-JS or compiled Tailwind like `.css-1a2b3c` or `.sc-bwzfXH`.
3. **Never use Absolute XPath**: Reject selectors starting with `/html/body/...`.
4. **Prefer CSS over XPath** for attributes; use XPath only when locating by inner text, traversing parent/sibling axes, or table cell matching.

---

## 🛠️ Step-by-Step Generation Workflow

### Step 1: Parse the DOM Elements
Identify all interactive elements:
- `<input type="text|email|password|number">` -> `type(element, text)`
- `<select>` -> `selectByVisibleText(element, option)`
- `<button>` or `<input type="submit">` -> `click(element)`
- `<input type="checkbox">` -> `setCheckbox(element, boolean)`
- `<table>` -> Dynamic table helpers
- Alert / error divs (`class="alert"`, `id="errorMessage"`) -> `isDisplayed()`, `getText()`

### Step 2: Generate the Page Object (`src/main/java/com/bdd/pages/`)
```java
package com.bdd.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SamplePage extends BasePage {

    @FindBy(id = "username")
    private WebElement usernameField;

    @FindBy(id = "loginButton")
    private WebElement submitButton;

    @FindBy(id = "errorMessage")
    private WebElement errorMessageAlert;

    public SamplePage(WebDriver driver) {
        super(driver);
    }

    // Atomic Actions
    public void enterUsername(String username) {
        type(usernameField, username);
    }

    public void clickSubmit() {
        click(submitButton);
    }

    // Status Queries
    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessageAlert);
    }

    public String getErrorMessageText() {
        return getText(errorMessageAlert);
    }
}
```

### Step 3: Generate Step Definitions (`src/test/java/com/bdd/steps/ui/`)
```java
public class SampleSteps {
    private SamplePage samplePage = new SamplePage(DriverManager.getDriver());

    @When("I submit the form with username {string}")
    public void i_submit_form(String username) {
        samplePage.enterUsername(username);
        samplePage.clickSubmit();
    }

    @Then("I should see the alert {string}")
    public void i_should_see_alert(String expectedMessage) {
        Assert.assertTrue(samplePage.isErrorMessageDisplayed());
        Assert.assertEquals(samplePage.getErrorMessageText(), expectedMessage);
    }
}
```

### Step 4: Generate Feature Scenario Snippet
```gherkin
  @ui @smoke
  Scenario: Successfully submit form
    Given I am on the sample page
    When I submit the form with username "testuser@example.com"
    Then I should see the alert "Operation successful"
```
