package com.bdd.steps.ui;

import com.bdd.core.DriverManager;
import com.bdd.pages.SearchPage;
import io.cucumber.java.en.When;

public class SearchSteps {
    @When("I click on search")
    public void iClickOnSearch() throws InterruptedException {
        new SearchPage(DriverManager.getDriver());
        SearchPage.search();
    }
}
