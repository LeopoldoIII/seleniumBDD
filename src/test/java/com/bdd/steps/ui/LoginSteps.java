package com.bdd.steps.ui;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import com.bdd.pages.LoginPage;
import com.bdd.config.ConfigReader;
import com.bdd.core.DriverManager;

public class LoginSteps {

    @Given("I am on the login page")
    public void i_am_on_the_login_page() {
        DriverManager.getDriver().get(ConfigReader.getProperty("url"));
        new LoginPage(DriverManager.getDriver());

    }

    @When("I enter username {string} and password {string}")
    public void i_enter_username_and_password(String username, String password) {
        LoginPage.login(ConfigReader.getProperty("username"), ConfigReader.getProperty("password"));

    }

    @When("I click on the login button")
    public void i_click_on_the_login_button() {
    }

    @Then("I should see the homepage")
    public void i_should_see_the_homepage() {
        Assert.assertEquals(DriverManager.getDriver().getTitle(), "Home - Example");
    }


}