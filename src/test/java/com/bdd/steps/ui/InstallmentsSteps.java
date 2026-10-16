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
        String actualStatus = installmentsPage.getStatusForPlan(planId);
        Assert.assertEquals(actualStatus, expectedStatus, "Status mismatch for plan " + planId);
    }

    @When("I click to pay for installment plan {string}")
    public void i_click_to_pay_for_installment_plan(String planId) {
        installmentsPage.clickPayForPlan(planId);
    }
}
