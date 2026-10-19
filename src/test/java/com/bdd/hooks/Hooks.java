package com.bdd.hooks;

import com.bdd.config.ConfigReader;
import com.bdd.core.DriverManager;
import com.bdd.core.driver.SauceLabsManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

public class Hooks {

    @Before("@ui or @mobile or not @api")
    public void setUp(Scenario scenario) {
        // Initialize WebDriver / AppiumDriver for UI or Mobile scenarios
        WebDriver driver = DriverManager.getDriver();

        // Start mobile screen recording if driver supports it
        if (driver instanceof CanRecordScreen) {
            try {
                ((CanRecordScreen) driver).startRecordingScreen();
            } catch (Exception e) {
                System.out.println("[INFO] Mobile screen recording not started: " + e.getMessage());
            }
        }
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

                // 2. Capture Browser Console Logs for Web failures
                try {
                    if (!(driver instanceof AppiumDriver)) {
                        LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
                        if (logEntries != null && logEntries.getAll().size() > 0) {
                            StringBuilder sb = new StringBuilder("=== Browser Console Logs ===\n");
                            for (LogEntry entry : logEntries) {
                                sb.append("[").append(new Date(entry.getTimestamp())).append("] [")
                                  .append(entry.getLevel()).append("] ")
                                  .append(entry.getMessage()).append("\n");
                            }
                            scenario.attach(sb.toString().getBytes(StandardCharsets.UTF_8), "text/plain", "Browser Console Logs");
                        }
                    }
                } catch (Exception ignored) {
                    // Browser log capture not supported on certain drivers/configurations
                }
            }

            // 3. Stop and attach Mobile Screen Recording on failure
            if (driver instanceof CanRecordScreen) {
                try {
                    String base64Video = ((CanRecordScreen) driver).stopRecordingScreen();
                    if (scenario.isFailed() && base64Video != null && !base64Video.isEmpty()) {
                        byte[] videoBytes = Base64.getDecoder().decode(base64Video);
                        scenario.attach(videoBytes, "video/mp4", "Failed Mobile Recording: " + scenario.getName());
                    }
                } catch (Exception ignored) {
                    // Recording may have already stopped or not be available
                }
            }

            // 4. Report test result to Sauce Labs if running in the cloud
            if ("saucelabs".equalsIgnoreCase(ConfigReader.getExecutionTarget())) {
                SauceLabsManager.updateJobStatus(driver, !scenario.isFailed());
            }

            // 5. Teardown driver and clean up ThreadLocal
            DriverManager.quitDriver();
        }

        // Clean up scenario context data
        com.bdd.utils.ScenarioContext.clear();
    }
}
