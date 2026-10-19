package com.bdd.steps.database;

import com.bdd.core.database.DbManager;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

public class DbSteps {

    private List<Map<String, Object>> lastQueryResults;

    @When("I execute the database query {string}")
    public void i_execute_the_database_query(String sql) {
        this.lastQueryResults = DbManager.executeQuery(sql);
    }

    @Then("the database query should return at least {int} record(s)")
    public void the_database_query_should_return_at_least_records(int expectedCount) {
        Assert.assertNotNull(lastQueryResults, "No query results found");
        Assert.assertTrue(lastQueryResults.size() >= expectedCount,
                "Expected at least " + expectedCount + " records, but got " + lastQueryResults.size());
    }

    @Then("the database column {string} in the first record should be {string}")
    public void the_database_column_in_first_record_should_be(String columnName, String expectedValue) {
        Assert.assertFalse(lastQueryResults.isEmpty(), "Query returned no rows");
        Object val = lastQueryResults.get(0).get(columnName);
        Assert.assertEquals(String.valueOf(val), expectedValue, "Mismatch in database column: " + columnName);
    }

    @Then("the database record for plan {string} should have status {string}")
    public void the_database_record_for_plan_should_have_status(String planId, String expectedStatus) {
        String sql = String.format("SELECT status FROM installment_plans WHERE id = '%s'", planId);
        List<Map<String, Object>> rows = DbManager.executeQuery(sql);
        Assert.assertFalse(rows.isEmpty(), "No plan found in database with id: " + planId);
        String actualStatus = String.valueOf(rows.get(0).get("status"));
        Assert.assertEquals(actualStatus, expectedStatus, "Mismatch in plan status in database");
    }

    @Then("the database record for plan {string} should not exist")
    public void the_database_record_for_plan_should_not_exist(String planId) {
        String sql = String.format("SELECT count(*) as total FROM installment_plans WHERE id = '%s'", planId);
        List<Map<String, Object>> rows = DbManager.executeQuery(sql);
        int count = Integer.parseInt(String.valueOf(rows.get(0).values().iterator().next()));
        Assert.assertEquals(count, 0, "Plan record still exists in database: " + planId);
    }

    @Then("^the database should have at least (\\d+) payment records? for plan \"([^\"]*)\"$")
    public void the_database_should_have_payment_records_for_plan(int expectedMin, String planId) {
        String sql = String.format("SELECT * FROM payments WHERE plan_id = '%s'", planId);
        List<Map<String, Object>> rows = DbManager.executeQuery(sql);
        Assert.assertTrue(rows.size() >= expectedMin,
                "Expected at least " + expectedMin + " payment records for plan " + planId + " but found: " + rows.size());
    }

    @Then("the database total amount paid for plan {string} should equal {double}")
    public void the_database_total_amount_paid_for_plan_should_equal(String planId, double expectedAmount) {
        String sql = String.format("SELECT SUM(amount) as total_paid FROM payments WHERE plan_id = '%s'", planId);
        List<Map<String, Object>> rows = DbManager.executeQuery(sql);
        Assert.assertFalse(rows.isEmpty(), "No payment sum returned");
        double actualSum = Double.parseDouble(String.valueOf(rows.get(0).get("total_paid")));
        Assert.assertEquals(actualSum, expectedAmount, 0.01, "Mismatch in total amount paid in database for plan: " + planId);
    }
}
