package com.bdd.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.bdd.core.DriverManager;
import org.openqa.selenium.support.PageFactory;

public class BasePage {
    protected static WebDriver driver;

    public BasePage(WebDriver driver) {
        BasePage.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public static WebElement findElement(By locator) {
        return driver.findElement(locator);
    }


    public static String getPageTitle() {
        return driver.getTitle();
    }

    public static void navigateTo(String url) {
        driver.get(url);
    }


}