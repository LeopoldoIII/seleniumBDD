package com.bdd.core;

import com.bdd.core.driver.DriverFactory;
import org.openqa.selenium.WebDriver;

/**
 * Thread-safe Driver Manager utilizing ThreadLocal to support parallel test execution.
 */
public class DriverManager {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverManager() {
        // Prevent direct instantiation
    }

    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            WebDriver driver = DriverFactory.createDriver();
            driverThreadLocal.set(driver);
        }
        return driverThreadLocal.get();
    }

    public static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }

    public static void closeDriver() {
        quitDriver();
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                driverThreadLocal.remove();
            }
        }
    }
}