package com.bdd.steps.ui;

import com.bdd.core.DriverManager;
import com.bdd.pages.InstallmentsPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class InstallmentsSteps {

    private InstallmentsPage installmentsPage;

    public InstallmentsSteps() {
        this.installmentsPage = new InstallmentsPage(DriverManager.getDriver());
    }

    @Then("I should see the installment plans table")
    public void i_should_see_the_installment_plans_table() {
        Assert.assertTrue(installmentsPage.isTableDisplayed(), "Installments table is not visible");
        Assert.assertTrue(installmentsPage.getPlansCount() > 0, "Installments table should contain at least 1 plan");
    }

    @Then("the installment plan {string} should be displayed with status {string}")
    public void the_installment_plan_should_be_displayed_with_status(String planId, String expectedStatus) {
        Assert.assertTrue(installmentsPage.hasPlanWithId(planId), "Plan " + planId + " was not found in the table");
        boolean statusMatched = installmentsPage.waitForStatus(planId, expectedStatus);
        Assert.assertTrue(statusMatched, "Status mismatch for plan " + planId + ". Expected: " + expectedStatus + " but found: " + installmentsPage.getStatusForPlan(planId));
    }

    @When("I click to pay for installment plan {string}")
    public void i_click_to_pay_for_installment_plan(String planId) {
        installmentsPage.clickPayForPlan(planId);
    }

    @When("I create a new installment plan with ID {string}, customer {string}, amount {string}, installments {string}")
    public void i_create_a_new_installment_plan(String id, String customer, String amount, String installments) {
        installmentsPage.createNewPlan(id, customer, amount, installments);
    }

    @When("I delete the installment plan {string}")
    public void i_delete_the_installment_plan(String planId) {
        installmentsPage.clickDeleteForPlan(planId);
    }

    @Then("the installment plan {string} should not be displayed in the table")
    public void the_installment_plan_should_not_be_displayed_in_the_table(String planId) {
        Assert.assertTrue(installmentsPage.isPlanNotPresent(planId), "Plan " + planId + " is still displayed in the table");
    }

    @When("I directly access the dashboard page")
    public void i_directly_access_the_dashboard_page() {
        installmentsPage.navigateTo(com.bdd.config.ConfigReader.getBaseUrl() + "/dashboard");
    }

    @Then("I should be redirected to the login page")
    public void i_should_be_redirected_to_the_login_page() {
        String currentUrl = installmentsPage.getCurrentUrl();
        Assert.assertTrue(currentUrl.endsWith("/login") || currentUrl.endsWith("/"),
                "Expected redirection to login page, but current URL is: " + currentUrl);
    }
}
