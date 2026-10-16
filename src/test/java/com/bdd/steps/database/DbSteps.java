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
}
