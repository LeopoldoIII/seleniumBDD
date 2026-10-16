package com.bdd.hooks;

import com.bdd.config.ConfigReader;
import com.bdd.core.DriverManager;
import com.bdd.core.driver.SauceLabsManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class Hooks {

    @Before("@ui or @mobile or not @api")
    public void setUp(Scenario scenario) {
        // Initialize WebDriver / AppiumDriver for UI or Mobile scenarios
        DriverManager.getDriver();
    }

    @After("@ui or @mobile or not @api")
    public void tearDown(Scenario scenario) {
        WebDriver driver = DriverManager.getDriver();

        if (driver != null) {
            // 1. Capture screenshot on failure
            if (scenario.isFailed()) {
                try {
                    byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    scenario.attach(screenshot, "image/png", "Failed Scenario: " + scenario.getName());
                } catch (Exception e) {
                    System.err.println("Failed to capture screenshot: " + e.getMessage());
                }
            }

            // 2. Report test result to Sauce Labs if running in the cloud
            if ("saucelabs".equalsIgnoreCase(ConfigReader.getExecutionTarget())) {
                SauceLabsManager.updateJobStatus(driver, !scenario.isFailed());
            }

            // 3. Teardown driver and clean up ThreadLocal
            DriverManager.quitDriver();
        }
    }
}
