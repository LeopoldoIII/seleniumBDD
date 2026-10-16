package com.bdd.steps.ui;

import com.bdd.core.DriverManager;
import com.bdd.pages.SearchPage;
import io.cucumber.java.en.When;

public class SearchSteps {

    private SearchPage searchPage;

    public SearchSteps() {
        this.searchPage = new SearchPage(DriverManager.getDriver());
    }

    @When("I click on search")
    public void iClickOnSearch() {
        searchPage.clickSearch();
    }

    @When("I search for {string}")
    public void iSearchFor(String query) {
        searchPage.searchFor(query);
    }
}
