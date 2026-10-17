package com.bdd.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * Runner dedicated to re-executing ONLY scenarios that failed during the primary test run.
 * Reads scenario references dynamically from target/failed_rerun.txt.
 */
@CucumberOptions(
        features = "@target/failed_rerun.txt",
        glue = {"com/bdd/steps/ui", "com/bdd/steps/database", "com/bdd/hooks"},
        plugin = {
                "pretty",
                "html:target/cucumber-rerun-reports.html"
        }
)
public class FailedUiTestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
