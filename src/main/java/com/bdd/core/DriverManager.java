package com.bdd.core;

import com.bdd.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class DriverManager {
    private static WebDriver driver;

    public DriverManager() {
        // Private constructor to prevent instantiation
    }

    public static WebDriver getDriver() {
        if (driver == null) {
            WebDriverManager.chromedriver().setup();
//            WebDriverManager.chromedriver().browserVersion("126.0.6478.115").setup();
            driver = new ChromeDriver(getChromeOptions());
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
            driver.manage().window().maximize();
        }
        return driver;
    }

    private static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (ConfigReader.getBooleanProperty("headless")) {
            configHeadlessOptions(options);
        } else {
            configStandardOptions(options);
        }
        return options;
    }

    private static void configHeadlessOptions(ChromeOptions options) {
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-extensions");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
    }

    private static void configStandardOptions(ChromeOptions options) {
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--ignore-certificate-errors");
//        options.setBinary("/Applications/Google Chrome.app/");
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_settings.popups", 0);
        prefs.put("download.prompt_for_download", "false");
        options.setExperimentalOption("prefs", prefs);
    }
    public static void closeDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}