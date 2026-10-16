---
name: create-ui-test
description: >-
  Use this skill when adding or updating Selenium UI tests, creating new Cucumber UI feature files,
  implementing Page Object Model classes, or adding UI step definitions.
---

# Create UI Test Workflow

Follow this step-by-step workflow to implement new UI tests in this repository.

## Step 1: Define the Gherkin Scenario
Create or update a `.feature` file in `src/test/resources/features/UI/`:
- Tag the feature with `@ui`.
- Use declarative, business-readable language.
- Structure with `Given`, `When`, `Then`, and `And`.
- Tag scenarios appropriately (e.g., `@smoke`, `@regression`).

```gherkin
@ui
Feature: Installment Plans

  @smoke
  Scenario: View available installment plans
    Given I am on the login page
    When I enter username "DEFAULT" and password "DEFAULT"
    Then I should see the homepage
```

## Step 2: Create or Update Page Object
Locate or create the page class in `src/main/java/com/bdd/pages/`:
1. Extend `com.bdd.pages.BasePage`.
2. Do NOT declare `static` elements or methods.
3. Accept `WebDriver driver` in the constructor and call `super(driver)`.
4. Locate elements using `@FindBy` annotations.
5. Use `BasePage` action methods (`click`, `type`, `selectByVisibleText`, `getText`, table helpers):

```java
package com.bdd.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SearchResultsPage extends BasePage {

    @FindBy(css = "h3")
    private WebElement firstResultHeader;

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    public String getFirstResultTitle() {
        return getText(firstResultHeader);
    }
}
```

## Step 3: Implement Step Definitions
Add or update step methods in `src/test/java/com/bdd/steps/ui/`:
1. Annotate methods with `@Given`, `@When`, or `@Then`.
2. Instantiate the Page Object using `DriverManager.getDriver()`.
3. Use TestNG `Assert` statements in `@Then` verification steps.

```java
@Then("the search results should display {string}")
public void the_search_results_should_display(String expectedText) {
    SearchResultsPage resultsPage = new SearchResultsPage(DriverManager.getDriver());
    Assert.assertTrue(resultsPage.getFirstResultTitle().contains(expectedText));
}
```

## Step 4: Verification & Execution
Execute the UI runner with the target base URL:
```bash
mvn clean test -Dtest=UiTestRunner -DbaseUrl=http://localhost:8080
```
Verify the generated HTML report under `target/cucumber-ui-reports.html`.
