package com.bdd.pages;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class LoginPage extends BasePage {

    @FindBy(id = "username")
    private static WebElement usernameField;

    @FindBy(id = "password")
    private static WebElement passwordField;

    @FindBy(id = "loginButton")
    private static WebElement loginButton;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public static void login(String username, String password) {
        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        loginButton.click();
    }
}