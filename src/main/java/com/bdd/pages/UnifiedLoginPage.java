package com.bdd.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Unified Multiplatform Page Object Model.
 * Demonstrates how a single Page Object class serves Web UI, Android Native,
 * and iOS Native using AppiumFieldDecorator and multi-platform annotations.
 *
 * Depending on the active driver (Chrome, Firefox, AndroidDriver, or IOSDriver),
 * the appropriate locator is automatically evaluated without changing the test code.
 */
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

    @FindBy(id = "errorMessage")
    @AndroidFindBy(id = "com.example.app:id/tv_error_message")
    @iOSXCUITFindBy(accessibility = "tv_error_message")
    private WebElement errorMessage;

    public UnifiedLoginPage(WebDriver driver) {
        super(driver);
    }

    public UnifiedLoginPage() {
        super();
    }

    public void enterUsername(String username) {
        type(usernameInput, username);
    }

    public void enterPassword(String password) {
        type(passwordInput, password);
    }

    public void clickLogin() {
        click(loginButton);
        hideKeyboard();
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public boolean isLoginButtonDisplayed() {
        return isDisplayed(loginButton);
    }

    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessage);
    }

    public String getErrorMessageText() {
        return getText(errorMessage);
    }
}
