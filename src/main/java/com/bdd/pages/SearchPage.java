package com.bdd.pages;

import com.bdd.core.DriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SearchPage extends BasePage{

    @FindBy(css = "#APjFqb")
    private  static WebElement search;

    public SearchPage(WebDriver driver) {
        super(driver);
    }


    public static void search() throws InterruptedException {
        search.click();
    }

}
