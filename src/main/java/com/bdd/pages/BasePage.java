package com.bdd.pages;

import com.bdd.config.ConfigReader;
import com.bdd.core.DriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.HidesKeyboard;
import io.appium.java_client.InteractsWithApps;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.remote.SupportsContextSwitching;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * Robust BasePage ("Navaja Suiza") providing resilient interaction with web and mobile UI components:
 * inputs, dropdowns, dynamic tables, modals, alerts, scrolling, JavaScript fallbacks,
 * hybrid WebView context switching, app lifecycle management, and AppiumFieldDecorator.
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
        // AppiumFieldDecorator supports @FindBy, @AndroidFindBy, and @iOSXCUITFindBy simultaneously
        PageFactory.initElements(new AppiumFieldDecorator(this.driver, Duration.ofSeconds(timeoutSeconds)), this);
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

    /**
     * Angular Material support: opens mat-select and selects option from CDK overlay.
     */
    public void selectMatOption(WebElement matSelectElement, String optionText) {
        click(matSelectElement);
        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//mat-option[normalize-space()='" + optionText + "'] | //div[@role='listbox']//span[normalize-space()='" + optionText + "']")));
        click(option);
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
        WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(tableLocator));
        String xpath = ".//tbody/tr[" + rowIndex + "]/td[" + colIndex + "]";
        List<WebElement> cells = table.findElements(By.xpath(xpath));
        if (cells.isEmpty()) {
            xpath = ".//tr[" + rowIndex + "]/td[" + colIndex + "]";
        }
        return table.findElement(By.xpath(xpath)).getText().trim();
    }

    public int findRowIndexContainingText(By tableLocator, String targetText) {
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                WebElement table = wait.until(ExpectedConditions.visibilityOfElementLocated(tableLocator));
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
            } catch (WebDriverException e) {
                // DOM is re-rendering, retry on next loop iteration
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {}
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

    // ==========================================
    // Deep Linking & Mobile Navigation
    // ==========================================

    /**
     * Navigates directly via deep link URL (e.g. "myapp://orders/123" or https deeplink).
     */
    public void openDeepLink(String deepLinkUrl) {
        if (deepLinkUrl != null && !deepLinkUrl.isEmpty()) {
            driver.get(deepLinkUrl);
        }
    }

    // ==========================================
    // Hybrid Apps & Context Switching (WebView / Native)
    // ==========================================

    /**
     * Switches context to the first available WEBVIEW context (for hybrid apps, OAuth, payment gateways).
     */
    public void switchToWebView() {
        if (driver instanceof SupportsContextSwitching) {
            SupportsContextSwitching contextDriver = (SupportsContextSwitching) driver;
            Set<String> contexts = contextDriver.getContextHandles();
            for (String context : contexts) {
                if (context.toUpperCase().contains("WEBVIEW")) {
                    contextDriver.context(context);
                    return;
                }
            }
            throw new NoSuchContextException("No WEBVIEW context found. Available contexts: " + contexts);
        }
    }

    /**
     * Switches context to a specific context containing target string.
     */
    public void switchToContext(String partialContextName) {
        if (driver instanceof SupportsContextSwitching) {
            SupportsContextSwitching contextDriver = (SupportsContextSwitching) driver;
            Set<String> contexts = contextDriver.getContextHandles();
            for (String context : contexts) {
                if (context.toUpperCase().contains(partialContextName.toUpperCase())) {
                    contextDriver.context(context);
                    return;
                }
            }
            throw new NoSuchContextException("Context containing '" + partialContextName + "' not found. Available: " + contexts);
        }
    }

    /**
     * Switches back to the native application context (NATIVE_APP).
     */
    public void switchToNativeApp() {
        if (driver instanceof SupportsContextSwitching) {
            ((SupportsContextSwitching) driver).context("NATIVE_APP");
        }
    }

    public String getCurrentContext() {
        if (driver instanceof SupportsContextSwitching) {
            return ((SupportsContextSwitching) driver).getContext();
        }
        return "NATIVE_APP";
    }

    // ==========================================
    // Mobile App Lifecycle & Keyboard
    // ==========================================

    /**
     * Minimizes app to background for specified duration and brings it back to foreground.
     */
    public void runAppInBackground(Duration duration) {
        if (driver instanceof InteractsWithApps) {
            ((InteractsWithApps) driver).runAppInBackground(duration);
        }
    }

    /**
     * Terminates the app with given package / bundle ID.
     */
    public void terminateApp(String bundleOrPackageId) {
        if (driver instanceof InteractsWithApps) {
            ((InteractsWithApps) driver).terminateApp(bundleOrPackageId);
        }
    }

    /**
     * Activates the app with given package / bundle ID.
     */
    public void activateApp(String bundleOrPackageId) {
        if (driver instanceof InteractsWithApps) {
            ((InteractsWithApps) driver).activateApp(bundleOrPackageId);
        }
    }

    /**
     * Safely hides software keyboard if active.
     */
    public void hideKeyboard() {
        if (driver instanceof HidesKeyboard) {
            try {
                ((HidesKeyboard) driver).hideKeyboard();
            } catch (Exception ignored) {
                // Keyboard might already be closed or not dismissible
            }
        }
    }
}