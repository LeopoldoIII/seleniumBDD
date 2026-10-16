package com.bdd.core.driver;

import com.bdd.config.ConfigReader;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages Sauce Labs connection parameters, capabilities and test reporting.
 */
public class SauceLabsManager {

    public static URL getSauceUrl() {
        try {
            return URI.create(ConfigReader.getSauceHubUrl()).toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Sauce Labs Hub URL: " + e.getMessage(), e);
        }
    }

    public static MutableCapabilities applySauceOptions(MutableCapabilities capabilities, String testName) {
        Map<String, Object> sauceOptions = new HashMap<>();
        sauceOptions.put("username", ConfigReader.getSauceUsername());
        sauceOptions.put("accessKey", ConfigReader.getSauceAccessKey());
        sauceOptions.put("name", testName != null ? testName : "Automated BDD Test");
        sauceOptions.put("build", "seleniumBDD-build-" + System.currentTimeMillis());

        String tunnelName = ConfigReader.getProperty("saucelabs.tunnelName");
        if (tunnelName != null && !tunnelName.trim().isEmpty()) {
            sauceOptions.put("tunnelName", tunnelName.trim());
        }

        capabilities.setCapability("sauce:options", sauceOptions);
        return capabilities;
    }

    /**
     * Updates the job status on Sauce Labs dashboard.
     */
    public static void updateJobStatus(WebDriver driver, boolean passed) {
        if (driver instanceof JavascriptExecutor) {
            try {
                ((JavascriptExecutor) driver).executeScript("sauce:job-result=" + (passed ? "passed" : "failed"));
            } catch (Exception e) {
                System.err.println("Could not update Sauce Labs job status: " + e.getMessage());
            }
        }
    }
}
