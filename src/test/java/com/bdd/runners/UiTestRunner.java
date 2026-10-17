package com.bdd.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "src/test/resources/features/UI",
        glue = {"com/bdd/steps/ui", "com/bdd/steps/database", "com/bdd/hooks"},
        plugin = {"pretty", "html:target/cucumber-ui-reports.html", "rerun:target/failed_rerun.txt"}
)
public class UiTestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}