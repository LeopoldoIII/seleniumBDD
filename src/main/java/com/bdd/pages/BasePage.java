package com.bdd.pages;

import com.bdd.config.ConfigReader;
import com.bdd.core.DriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Robust BasePage ("Navaja Suiza") providing resilient interaction with web and mobile UI components:
 * inputs, dropdowns, dynamic tables, modals, alerts, scrolling, and JavaScript fallbacks.
 */
public class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;
    protected JavascriptExecutor js;

    public BasePage(WebDriver driver) {
        this.driver = driver != null ? driver : DriverManager.getDriver();
        int timeoutSeconds = ConfigReader.getTimeout();
        this.wait = new WebDriverWait(this.driver, Duration.ofSeconds(timeoutSeconds));
        this.actions = new Actions(this.driver);
        this.js = (JavascriptExecutor) this.driver;
        PageFactory.initElements(this.driver, this);
    }

    public BasePage() {
        this(DriverManager.getDriver());
    }

    // ==========================================
    // Navigation & URL
    // ==========================================

    public void navigateTo(String url) {
        driver.get(url);
    }

    public void navigateToBaseUrl() {
        driver.get(ConfigReader.getBaseUrl());
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    // ==========================================
    // Clicks & Actions
    // ==========================================

    public void click(WebElement element) {
        try {
            waitForClickable(element).click();
        } catch (ElementClickInterceptedException | StaleElementReferenceException e) {
            // Fallback to JavaScript click when animations or overlays intercept
            jsClick(element);
        }
    }

    public void click(By locator) {
        click(driver.findElement(locator));
    }

    public void jsClick(WebElement element) {
        waitForVisibility(element);
        js.executeScript("arguments[0].click();", element);
    }

    public void hover(WebElement element) {
        waitForVisibility(element);
        actions.moveToElement(element).perform();
    }

    public void doubleClick(WebElement element) {
        waitForClickable(element);
        actions.doubleClick(element).perform();
    }

    public void pressEnter(WebElement element) {
        waitForVisibility(element);
        element.sendKeys(Keys.ENTER);
    }

    // ==========================================
    // Text Inputs & Clear
    // ==========================================

    public void type(WebElement element, String text) {
        waitForVisibility(element);
        element.clear();
        if (text != null) {
            element.sendKeys(text);
        }
    }

    public void type(By locator, String text) {
        type(driver.findElement(locator), text);
    }

    public void clearAndType(WebElement element, String text) {
        waitForClickable(element);
        // Clear via backspaces to trigger change events in dynamic frameworks (React/Vue/Angular)
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        element.sendKeys(Keys.chord(Keys.COMMAND, "a"), Keys.DELETE);
        if (text != null) {
            element.sendKeys(text);
        }
    }

    public String getText(WebElement element) {
        return waitForVisibility(element).getText().trim();
    }

    public String getText(By locator) {
        return getText(driver.findElement(locator));
    }

    public String getInputValue(WebElement element) {
        waitForVisibility(element);
        return element.getAttribute("value");
    }

    // ==========================================
    // Dropdowns & Selects
    // ==========================================

    public void selectByVisibleText(WebElement element, String text) {
        waitForVisibility(element);
        Select select = new Select(element);
        select.selectByVisibleText(text);
    }

    public void selectByValue(WebElement element, String value) {
        waitForVisibility(element);
        Select select = new Select(element);
        select.selectByValue(value);
    }

    public void selectByIndex(WebElement element, int index) {
        waitForVisibility(element);
        Select select = new Select(element);
        select.selectByIndex(index);
    }

    public String getSelectedOptionText(WebElement element) {
        waitForVisibility(element);
        Select select = new Select(element);
        return select.getFirstSelectedOption().getText().trim();
    }

    // ==========================================
    // Checkboxes & Radio Buttons
    // ==========================================

    public void setCheckbox(WebElement element, boolean check) {
        waitForClickable(element);
        if (element.isSelected() != check) {
            element.click();
        }
    }

    public boolean isCheckboxChecked(WebElement element) {
        waitForVisibility(element);
        return element.isSelected();
    }

    // ==========================================
    // Scrolling
    // ==========================================

    public void scrollToElement(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    public void scrollToBottom() {
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    // ==========================================
    // Dynamic Tables (Crucial for Installments & Plans)
    // ==========================================

    public int getTableRowCount(By tableLocator) {
        WebElement table = waitForVisibility(driver.findElement(tableLocator));
        List<WebElement> rows = table.findElements(By.xpath(".//tbody/tr"));
        if (rows.isEmpty()) {
            rows = table.findElements(By.xpath(".//tr"));
        }
        return rows.size();
    }

    public String getTableCellText(By tableLocator, int rowIndex, int colIndex) {
        WebElement table = waitForVisibility(driver.findElement(tableLocator));
        String xpath = ".//tbody/tr[" + rowIndex + "]/td[" + colIndex + "]";
        List<WebElement> cells = table.findElements(By.xpath(xpath));
        if (cells.isEmpty()) {
            xpath = ".//tr[" + rowIndex + "]/td[" + colIndex + "]";
        }
        return table.findElement(By.xpath(xpath)).getText().trim();
    }

    public int findRowIndexContainingText(By tableLocator, String targetText) {
        WebElement table = waitForVisibility(driver.findElement(tableLocator));
        List<WebElement> rows = table.findElements(By.xpath(".//tbody/tr"));
        if (rows.isEmpty()) {
            rows = table.findElements(By.xpath(".//tr"));
        }
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).getText().contains(targetText)) {
                return i + 1; // 1-based index
            }
        }
        return -1;
    }

    // ==========================================
    // Alerts & Iframes
    // ==========================================

    public void acceptAlert() {
        wait.until(ExpectedConditions.alertIsPresent()).accept();
    }

    public void dismissAlert() {
        wait.until(ExpectedConditions.alertIsPresent()).dismiss();
    }

    public String getAlertText() {
        return wait.until(ExpectedConditions.alertIsPresent()).getText();
    }

    public void switchToFrame(WebElement frameElement) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement));
    }

    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    // ==========================================
    // Explicit Waits & Assertions
    // ==========================================

    public WebElement waitForVisibility(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public WebElement waitForClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    public boolean waitForInvisibility(WebElement element) {
        try {
            return wait.until(ExpectedConditions.invisibilityOf(element));
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean waitForTextPresent(WebElement element, String text) {
        return wait.until(ExpectedConditions.textToBePresentInElement(element, text));
    }
}