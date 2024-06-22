package com.bdd.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.bdd.core.DriverManager;

public class BasePage {

    protected static WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    protected static WebElement findElement(By locator) {
        return getDriver().findElement(locator);
    }

    protected static void click(By locator) {
        findElement(locator).click();
    }

    protected static void type(By locator, String text) {
        findElement(locator).sendKeys(text);
    }

    protected static String getText(By locator) {
        return findElement(locator).getText();
    }

}