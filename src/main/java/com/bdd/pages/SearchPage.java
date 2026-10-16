package com.bdd.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SearchPage extends BasePage {

    @FindBy(css = "#APjFqb")
    private WebElement searchField;

    public SearchPage(WebDriver driver) {
        super(driver);
    }

    public SearchPage() {
        super();
    }

    public void clickSearch() {
        click(searchField);
    }

    public void searchFor(String query) {
        type(searchField, query);
        pressEnter(searchField);
    }
}
