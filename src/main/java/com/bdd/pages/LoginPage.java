package com.bdd.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    @FindBy(id = "username")
    @AndroidFindBy(id = "com.example.installments:id/username")
    @iOSXCUITFindBy(accessibility = "username")
    private WebElement usernameField;

    @FindBy(id = "password")
    @AndroidFindBy(id = "com.example.installments:id/password")
    @iOSXCUITFindBy(accessibility = "password")
    private WebElement passwordField;

    @FindBy(id = "loginButton")
    @AndroidFindBy(id = "com.example.installments:id/loginButton")
    @iOSXCUITFindBy(accessibility = "loginButton")
    private WebElement loginButton;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage() {
        super();
    }

    public void enterUsername(String username) {
        type(usernameField, username);
    }

    public void enterPassword(String password) {
        type(passwordField, password);
    }

    public void clickLogin() {
        click(loginButton);
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    @FindBy(id = "errorMessage")
    private WebElement errorMessage;

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