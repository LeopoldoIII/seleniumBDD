# 🎯 Master Guide to CSS & ID Selectors for Selenium & Automation

> **Interactive Cheat Sheet & Accordion Reference**  
> Complete guide with all possible CSS selector combinations, ID syntax, attributes, combinators, pseudo-classes, and production-ready examples for Selenium Java (`@FindBy` and `By.cssSelector`).

---

## 📑 Table of Contents (Accordion)

> Click on each section to expand or collapse examples and selector combinations.

<details>
<summary><b>1. Basic Selectors: ID, Tag, Class, and Universal</b></summary>
<br>

The foundation of CSS selectors.

| CSS Syntax | Description | Target HTML Example |
| :--- | :--- | :--- |
| `#id` | Element with a specific ID | `<input id="username">` |
| `tag#id` | Specific tag with that ID | `<input id="username">` |
| `.class` | Element with a specific class | `<button class="btn">` |
| `tag.class` | Specific tag with that class | `<button class="btn">` |
| `.class1.class2` | Element containing **both** classes simultaneously | `<button class="btn btn-primary">` |
| `*` | Any element (universal wildcard) | Entire DOM |
| `tag` | By HTML tag name | `<div>`, `<span>`, `<input>` |

#### 💡 Practical Examples

```html
<!-- Sample HTML -->
<div id="login-container" class="auth-box card-shadow">
  <input type="text" id="user-email" class="form-control required-field" placeholder="Email">
  <button id="btn-submit" class="btn btn-primary active">Sign In</button>
</div>
```

```java
// Selenium Java:
@FindBy(css = "#user-email")
private WebElement emailById;

@FindBy(css = "input#user-email")
private WebElement emailByTagAndId;

@FindBy(css = ".btn.btn-primary.active")
private WebElement buttonWithMultipleClasses;

@FindBy(css = "div.auth-box.card-shadow")
private WebElement containerWithTagAndTwoClasses;
```

> **Golden rule with multiple classes:**  
> If an element has `class="btn btn-primary active"`, chain them with dots without spaces: `.btn.btn-primary.active`. If you insert a space (`.btn .btn-primary`), it will search for a descendant child, not the element itself.

</details>

---

<details>
<summary><b>2. Attribute Selectors: Exact, Partial, and Dynamic IDs</b></summary>
<br>

Essential when IDs change across page reloads (dynamic IDs) or to leverage dedicated QA hooks (`data-testid`, `name`, `type`, `placeholder`).

| Operator | Meaning | Example Selector | Matches... |
| :---: | :--- | :--- | :--- |
| `[attr='val']` | **Exact** value | `[data-testid='login-btn']` | `data-testid="login-btn"` |
| `[attr^='val']` | **Starts with** (Prefix) | `[id^='session_']` | `id="session_12345"`, `id="session_abc"` |
| `[attr$='val']` | **Ends with** (Suffix) | `[id$='_input']` | `id="username_input"`, `id="987_input"` |
| `[attr*='val']` | **Contains** substring | `[class*='alert-danger']` | `class="box alert-danger-custom active"` |
| `[attr~='val']` | Contains exact word in space-separated list | `[class~='primary']` | `class="btn primary large"` |
| `[attr\|='val']` | Starts with value or `val-` | `[lang\|='en']` | `lang="en"`, `lang="en-US"` |
| `[attr]` | Attribute is present (regardless of value) | `input[disabled]`, `[required]` | `<input required>`, `<button disabled>` |
| `[attr='val' i]` | Case-insensitive matching | `[placeholder*='search' i]` | `placeholder="SEARCH..."` |

#### 💡 Practical Examples for Dynamic IDs

In modern frameworks (React, Angular, Salesforce), element IDs are often auto-generated: `input-ember1234-field`, `ctrl_9823_email`.

```html
<!-- Dynamically generated IDs -->
<input id="usr_9281736_txt" name="username" data-test="user-input" required>
<button id="btn_submit_99482" data-role="action-button">Submit</button>
<div id="container-random123-body">Content</div>
```

```java
// Selenium Java:

// Prefix: ID starting with 'usr_'
@FindBy(css = "input[id^='usr_']")
private WebElement dynamicUsernamePrefix;

// Suffix: Input with ID ending in '_txt'
@FindBy(css = "input[id$='_txt']")
private WebElement dynamicUsernameSuffix;

// Contains: ID containing substring 'random'
@FindBy(css = "div[id*='random']")
private WebElement dynamicContainerContains;

// Dedicated QA test hook (Industry Best Practice)
@FindBy(css = "[data-test='user-input']")
private WebElement usernameByDataTest;

// Input element with the 'required' attribute present
@FindBy(css = "input[required]")
private WebElement requiredInputField;
```

</details>

---

<details>
<summary><b>3. Multi-Attribute Combinations (Logical AND)</b></summary>
<br>

When a single attribute is ambiguous or duplicated across the page, chain 2 or more attributes together in the same selector.

| Syntax | Description |
| :--- | :--- |
| `tag[attr1='val1'][attr2='val2']` | Matches tag AND both attributes simultaneously |
| `tag#id[attr='val']` | Matches specific ID AND additional attribute |
| `tag.class[attr='val']` | Matches class AND attribute |
| `tag.class[attr1*='val1'][attr2^='val2']` | Class + contains attribute + starts-with attribute |

#### 💡 Practical Examples

```html
<form id="checkout-form">
  <input type="text" name="address" class="input-field" placeholder="Line 1" data-index="1">
  <input type="text" name="address" class="input-field" placeholder="Line 2" data-index="2">
  <input type="hidden" name="address" value="token_secret">
</form>
```

```java
// Selenium Java:

// Specific text input matching name 'address' AND data-index '1'
@FindBy(css = "input[type='text'][name='address'][data-index='1']")
private WebElement primaryAddressInput;

// Input with class 'input-field' containing 'Line 2' in its placeholder
@FindBy(css = "input.input-field[placeholder*='Line 2']")
private WebElement secondaryAddressInput;

// Tag + Class + Type + Required
@FindBy(css = "input.input-field[type='text'][required]")
private WebElement requiredTextField;
```

</details>

---

<details>
<summary><b>4. Hierarchical Combinators: Parents, Children, and Siblings</b></summary>
<br>

Navigate the DOM tree based on relationships between elements.

| Symbol | Relationship | Syntax | Description |
| :---: | :--- | :--- | :--- |
| *(Space)* | **Descendant** | `A B` | `B` is inside `A` at any depth level (child, grandchild, etc.) |
| `>` | **Direct Child** | `A > B` | `B` is an immediate first-level child of `A` |
| `+` | **Adjacent Sibling** | `A + B` | `B` is the immediately following sibling after `A` at the same level |
| `~` | **General Sibling** | `A ~ B` | `B` is any subsequent sibling following `A` (not necessarily adjacent) |

#### 💡 Visual Relationship Diagram

```text
div#parent
 │
 ├── p.child-1 (Direct child: div#parent > p.child-1)
 ├── p.child-2 (Adjacent sibling of child-1: p.child-1 + p.child-2)
 ├── ul
 │    └── li.grandchild (Descendant: div#parent li.grandchild)
 └── p.child-3 (General sibling of child-1: p.child-1 ~ p.child-3)
```

#### 💡 Practical Examples

```html
<div id="modal-content" class="modal-body">
  <h3>Dialog Title</h3>
  <label for="pwd">Password:</label>
  <input type="password" id="pwd" class="form-input">
  <span class="error-msg">Password is required</span>
  
  <div class="footer-actions">
    <button class="btn btn-cancel">Cancel</button>
    <button class="btn btn-save">Save</button>
  </div>
</div>
```

```java
// Selenium Java:

// Descendant: Any save button inside the modal container
@FindBy(css = "#modal-content button.btn-save")
private WebElement saveButtonInModal;

// Direct child (>): Cancel button directly inside footer-actions
@FindBy(css = "div.footer-actions > button.btn-cancel")
private WebElement cancelDirectChildButton;

// Adjacent sibling (+): Input element placed immediately after its label
@FindBy(css = "label[for='pwd'] + input")
private WebElement inputImmediatelyAfterLabel;

// Adjacent sibling (+): Error message placed immediately after the input
@FindBy(css = "input#pwd + span.error-msg")
private WebElement errorImmediatelyAfterPassword;

// General sibling (~): Any button following the cancel button
@FindBy(css = "button.btn-cancel ~ button")
private WebElement buttonsAfterCancel;
```

</details>

---

<details>
<summary><b>5. Structural Pseudo-Classes & Indexing: `nth-child`, `first-child`, `last-child`</b></summary>
<br>

Ideal for tables, dropdown lists, product cards, or dynamic lists.

| Pseudo-Class | Description | Example |
| :--- | :--- | :--- |
| `:first-child` | First child of its parent | `ul#menu > li:first-child` |
| `:last-child` | Last child of its parent | `ul#menu > li:last-child` |
| `:only-child` | Element that is the only child of its parent | `div.alert > span:only-child` |
| `:nth-child(n)` | Child at index `n` (1-indexed) | `tr:nth-child(2)` *(Second row)* |
| `:nth-child(odd)` | All odd children (1, 3, 5...) | `tbody > tr:nth-child(odd)` |
| `:nth-child(even)` | All even children (2, 4, 6...) | `tbody > tr:nth-child(even)` |
| `:nth-child(2n+1)` | Algebraic formula pattern | `li:nth-child(3n+1)` |
| `:nth-last-child(n)` | Counts backwards from the last child | `tr:nth-last-child(1)` *(Last row)* |
| `:first-of-type` | First element of its specific tag type | `div > p:first-of-type` |
| `:last-of-type` | Last element of its specific tag type | `div > p:last-of-type` |
| `:nth-of-type(n)` | N-th element of its specific tag type | `table td:nth-of-type(3)` *(Column 3)* |
| `:only-of-type` | Sole element of its tag type among siblings | `article > h1:only-of-type` |

#### 💡 Practical Examples with Tables

```html
<table id="users-table">
  <thead>
    <tr> <th>ID</th> <th>Name</th> <th>Role</th> <th>Actions</th> </tr>
  </thead>
  <tbody>
    <tr> <td>1</td> <td>Alice</td> <td>Admin</td> <td><button class="edit">✏️</button></td> </tr>
    <tr> <td>2</td> <td>Bob</td>   <td>User</td>  <td><button class="edit">✏️</button></td> </tr>
    <tr> <td>3</td> <td>Charlie</td> <td>User</td> <td><button class="edit">✏️</button></td> </tr>
  </tbody>
</table>
```

```java
// Selenium Java:

// First user row in table
@FindBy(css = "#users-table tbody > tr:first-child")
private WebElement firstUserRow;

// Second row, column 2 (Name: "Bob")
@FindBy(css = "#users-table tbody > tr:nth-child(2) > td:nth-of-type(2)")
private WebElement secondUserNameCell;

// Edit button of the last table row
@FindBy(css = "#users-table tbody > tr:last-child button.edit")
private WebElement lastRowEditButton;

// The 3rd column (Role) across all rows
@FindBy(css = "#users-table tbody tr > td:nth-child(3)")
private List<WebElement> allRoleCells;
```

</details>

---

<details>
<summary><b>6. Form Element State Pseudo-Classes (`:enabled`, `:disabled`, `:checked`)</b></summary>
<br>

Locate form inputs based on their dynamic browser interaction state.

| State Selector | Description |
| :--- | :--- |
| `:enabled` | Form elements enabled and ready for interaction |
| `:disabled` | Form elements with `disabled` attribute |
| `:checked` | Radio buttons or checkboxes currently ticked/selected |
| `:focus` | Element currently holding keyboard/cursor focus |
| `:required` | Form inputs with the required constraint |
| `:optional` | Form inputs without the required constraint |
| `:valid` / `:invalid` | Elements passing or failing HTML5 constraint validation |
| `:read-only` | Elements with the `readonly` attribute |

#### 💡 Practical Examples

```html
<form id="order-form">
  <input type="checkbox" id="terms" checked>
  <input type="checkbox" id="newsletter">
  
  <input type="text" id="coupon" disabled placeholder="Coupon disabled">
  <input type="text" id="phone" required placeholder="Phone number (Required)">
  
  <button id="submit-btn" disabled class="btn">Pay Now</button>
  <button id="reset-btn" class="btn">Clear</button>
</form>
```

```java
// Selenium Java:

// Checkbox that is currently checked
@FindBy(css = "#order-form input[type='checkbox']:checked")
private WebElement checkedTermsCheckbox;

// Submit button in disabled state
@FindBy(css = "#order-form button:disabled")
private WebElement disabledSubmitButton;

// Button enabled for click
@FindBy(css = "#order-form button:enabled")
private WebElement enabledButton;

// Mandatory text field
@FindBy(css = "#order-form input:required")
private WebElement requiredPhoneField;
```

</details>

---

<details>
<summary><b>7. Logical Pseudo-Classes: Negation `:not()` and Relational `:has()`</b></summary>
<br>

Advanced filtering and upward parent-node matching.

| Selector | Name | Function |
| :--- | :--- | :--- |
| `:not(selector)` | Negation | Excludes elements matching the inner selector |
| `:has(selector)` | Relational *(CSS Level 4)* | Selects the **parent/ancestor** element containing that child |
| `:is(s1, s2)` | Match-any list | Matches any selector in the comma-separated list |
| `:where(s1, s2)` | Zero-specificity list | Identical to `:is()`, but keeps specificity score at 0 |

#### 💡 The Power of `:has()` (Selecting Parents in CSS)
> Historically, CSS could not traverse upwards to select parent elements based on child contents (requiring XPath `//div[..//button]`). **Today, `:has()` is natively supported across all modern browsers!**

#### 💡 Practical Examples

```html
<div class="user-card" id="card-1">
  <h4>John Doe</h4>
  <span class="badge status-pending">Pending</span>
  <button class="btn-approve">Approve</button>
</div>

<div class="user-card" id="card-2">
  <h4>Jane Smith</h4>
  <span class="badge status-active">Active</span>
  <button class="btn-delete">Delete</button>
</div>

<form>
  <input type="text" name="user" class="input valid">
  <input type="password" name="pass" class="input">
  <input type="hidden" name="csrf" value="12345">
</form>
```

```java
// Selenium Java:

// 1. NEGATION :not()
// All input fields that are NOT hidden
@FindBy(css = "form input:not([type='hidden'])")
private List<WebElement> visibleInputs;

// Buttons that do NOT have the 'disabled' class
@FindBy(css = "button:not(.disabled)")
private List<WebElement> enabledButtons;

// 2. RELATIONAL :has() -> PARENT SELECTION
// The user card (.user-card) containing the delete button
@FindBy(css = ".user-card:has(button.btn-delete)")
private WebElement cardWithDeleteButton;

// The table row containing an active status badge
@FindBy(css = "tr:has(span.status-active)")
private WebElement rowWithActiveUser;

// 3. GROUPING :is()
// Headings h1, h2, or h3 inside the main container
@FindBy(css = "main :is(h1, h2, h3)")
private List<WebElement> mainHeadings;
```

</details>

---

<details>
<summary><b>8. Catalog of Real-World Automation Combinations</b></summary>
<br>

Production-tested combinations for enterprise web automation with Selenium WebDriver.

### Combination 1: Dynamic ID Prefix + Class + Specific Attribute
```css
/* Input with ID starting with 'usr_', having class 'form-control' and type 'email' */
input[id^='usr_'].form-control[type='email']
```

### Combination 2: Parent ID Suffix + Direct Child + Partial Action Attribute
```css
/* Save button inside modal container ending with '_container' */
div[id$='_container'] > div.modal-footer > button[data-action*='save']
```

### Combination 3: Sibling with Negated State Filter
```css
/* Error span placed immediately after an invalid input, ignoring hidden alerts */
input:invalid + span.error-message:not(.hidden)
```

### Combination 4: Specific Table Row Filtered by Child Badge
```css
/* Download button only inside the table row that has a 'completed' badge */
table#orders-table tbody tr:has(span.badge-completed) button.btn-download
```

### Combination 5: Dropdown Menu Item N Excluding Disabled Items
```css
/* Third item in dropdown list that is not disabled */
ul.dropdown-menu > li:nth-child(3):not(.disabled) > a
```

### Combination 6: Multi-Attribute Case-Insensitive Search Input
```css
/* Input matching placeholder containing 'search' regardless of upper/lower case */
input.search-input[placeholder*='search' i][type='search']
```

### Combination 7: Parent Row Containing Checked Checkbox
```css
/* Row container (.item-row) whose inner checkbox is currently checked */
div.item-row:has(input[type='checkbox']:checked)
```

</details>

---

<details>
<summary><b>9. CSS vs. XPath: Decision Matrix</b></summary>
<br>

| Criteria | CSS Selector | XPath | Recommendation |
| :--- | :---: | :---: | :--- |
| **Execution Speed** | ⚡ Fastest (native browser engine) | 🐢 Slower in legacy engines | **CSS** for 90% of locators |
| **Readability & Maintenance** | Clean, concise, standard web syntax | Verbose, brittle (`//div[2]/span/..`) | **CSS** |
| **Dynamic IDs (Prefix/Suffix)** | `[id^='x']`, `[id$='y']` | `starts-with()`, `ends-with()` | **CSS** (cleaner syntax) |
| **Upward Traversal (Parent)** | `:has(...)` | `/..`, `parent::`, `ancestor::` | **CSS with `:has()`** or **XPath** if browser is legacy |
| **Direct Visible Text Search** | ❌ Not natively supported | `//*[text()='Submit']` | **XPath** (sole unique advantage) |

</details>

---

<details>
<summary><b>10. Page Object Model Template (`BasePage`)</b></summary>
<br>

How to organize these selectors in your Page Object classes under `src/main/java/com/bdd/pages/`:

```java
package com.bdd.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import java.util.List;

public class LoginPage extends BasePage {

    // 1. Simple ID selector
    @FindBy(css = "#username")
    private WebElement usernameInput;

    // 2. Exact test attribute selector
    @FindBy(css = "[data-testid='password-field']")
    private WebElement passwordInput;

    // 3. Dynamic ID prefix ('btn_login_') with chained classes
    @FindBy(css = "button[id^='btn_login_'].btn.btn-primary")
    private WebElement submitButton;

    // 4. Negation selector
    @FindBy(css = ".alert-box:not(.d-none)")
    private WebElement visibleErrorMessage;

    // 5. Relational :has() selector to find active card parent
    @FindBy(css = ".account-card:has(span.badge-active)")
    private WebElement activeAccountCard;

    // 6. List of elements with nth-child pattern
    @FindBy(css = "table#results-table tbody > tr:nth-child(odd)")
    private List<WebElement> oddResultRows;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // Dynamic runtime selector using By.cssSelector
    public WebElement getCellByColumnIndex(int columnIndex) {
        By dynamicCss = By.cssSelector(String.format("table tbody tr > td:nth-child(%d)", columnIndex));
        return getDriver().findElement(dynamicCss);
    }

    public void enterCredentials(String user, String pass) {
        type(usernameInput, user);
        type(passwordInput, pass);
        click(submitButton);
    }
}
```

</details>

---

## 🚀 Quick Mnemonic Summary

Remember attribute symbols at a glance:

- `#` : **ID** (`#my-id`)
- `.` : **Class** (`.my-class`)
- `^=` : **Starts with** (Caret pointing to the top/beginning)
- `$=` : **Ends with** (Dollar sign as line ending)
- `*=` : **Contains** (Asterisk wildcard)
- `>` : **Direct child**
- `+` : **Adjacent sibling**
- `~` : **General sibling**
- `:has()` : **Parent containing child**
- `:not()` : **Exclusion / negation filter**
