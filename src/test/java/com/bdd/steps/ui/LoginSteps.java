package com.bdd.steps.ui;

import com.bdd.config.ConfigReader;
import com.bdd.core.DriverManager;
import com.bdd.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class LoginSteps {

    private LoginPage loginPage;

    public LoginSteps() {
        this.loginPage = new LoginPage(DriverManager.getDriver());
    }

    @Given("I am on the login page")
    public void i_am_on_the_login_page() {
        loginPage.navigateToBaseUrl();
    }

    @When("I enter username {string} and password {string}")
    public void i_enter_username_and_password(String username, String password) {
        String user = username.equals("DEFAULT") ? ConfigReader.getProperty("username") : username;
        String pass = password.equals("DEFAULT") ? ConfigReader.getProperty("password") : password;
        loginPage.login(user, pass);
    }

    @When("I click on the login button")
    public void i_click_on_the_login_button() {
        loginPage.clickLogin();
    }

    @Then("I should see the homepage")
    public void i_should_see_the_homepage() {
        Assert.assertFalse(loginPage.getPageTitle().isEmpty(), "Page title should not be empty");
    }
}