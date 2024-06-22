package com.bdd.pages;

import org.openqa.selenium.By;


public class LoginPage extends BasePage {

    private static By usernameField = By.id("username");
    private static By passwordField = By.id("password");
    private static By loginButton = By.id("login");

    public static void enterUsername(String username) {
        type(usernameField, username);
    }

    public static void enterPassword(String password) {
        type(passwordField, password);
    }

    public static void clickLoginButton() {
        click(loginButton);
    }
}