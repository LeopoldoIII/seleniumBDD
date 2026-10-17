package com.bdd.core.driver;

import com.bdd.config.ConfigReader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class responsible for creating Web and Mobile drivers,
 * supporting both Local execution and Sauce Labs cloud execution.
 */
public class DriverFactory {

    public static WebDriver createDriver() {
        String targetPlatform = ConfigReader.getTargetPlatform();
        ExecutionTarget execution = ExecutionTarget.fromString(ConfigReader.getExecutionTarget());

        if ("mobile".equalsIgnoreCase(targetPlatform) || "android".equalsIgnoreCase(targetPlatform) || "ios".equalsIgnoreCase(targetPlatform)) {
            return createMobileDriver(execution);
        } else {
            return createWebDriver(execution);
        }
    }

    private static WebDriver createWebDriver(ExecutionTarget execution) {
        String browserName = ConfigReader.getBrowser();
        DriverType driverType = DriverType.fromString(browserName);

        if (execution == ExecutionTarget.SAUCELABS) {
            return createSauceLabsWebDriver(driverType);
        } else {
            return createLocalWebDriver(driverType);
        }
    }

    private static WebDriver createLocalWebDriver(DriverType driverType) {
        WebDriver driver;
        switch (driverType) {
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (ConfigReader.isHeadless()) {
                    firefoxOptions.addArguments("-headless");
                }
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case CHROME:
            default:
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver(getChromeOptions());
                break;
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getTimeout()));
        driver.manage().window().maximize();
        return driver;
    }

    private static WebDriver createSauceLabsWebDriver(DriverType driverType) {
        URL sauceUrl = SauceLabsManager.getSauceUrl();
        if (driverType == DriverType.FIREFOX) {
            FirefoxOptions options = new FirefoxOptions();
            SauceLabsManager.applySauceOptions(options, "SauceLabs Firefox Execution");
            return new RemoteWebDriver(sauceUrl, options);
        } else {
            ChromeOptions options = getChromeOptions();
            SauceLabsManager.applySauceOptions(options, "SauceLabs Chrome Execution");
            return new RemoteWebDriver(sauceUrl, options);
        }
    }

    private static WebDriver createMobileDriver(ExecutionTarget execution) {
        String platform = ConfigReader.getMobilePlatform();
        try {
            if ("ios".equalsIgnoreCase(platform)) {
                XCUITestOptions options = new XCUITestOptions();
                options.setDeviceName(ConfigReader.getMobileDeviceName());
                String platformVersion = ConfigReader.getMobilePlatformVersion();
                if (platformVersion != null && !platformVersion.isEmpty()) {
                    options.setPlatformVersion(platformVersion);
                }
                String app = ConfigReader.getMobileApp();
                if (app != null && !app.isEmpty()) {
                    options.setApp(app);
                }
                String bundleId = ConfigReader.getIosBundleId();
                if (bundleId != null && !bundleId.isEmpty()) {
                    options.setBundleId(bundleId);
                }

                if (execution == ExecutionTarget.SAUCELABS) {
                    SauceLabsManager.applySauceOptions(options, "SauceLabs iOS Execution");
                    return new IOSDriver(SauceLabsManager.getSauceUrl(), options);
                } else {
                    return new IOSDriver(URI.create("http://127.0.0.1:4723").toURL(), options);
                }
            } else {
                // Default Android
                UiAutomator2Options options = new UiAutomator2Options();
                options.setDeviceName(ConfigReader.getMobileDeviceName());
                String platformVersion = ConfigReader.getMobilePlatformVersion();
                if (platformVersion != null && !platformVersion.isEmpty()) {
                    options.setPlatformVersion(platformVersion);
                }
                String app = ConfigReader.getMobileApp();
                if (app != null && !app.isEmpty()) {
                    options.setApp(app);
                }
                String appPackage = ConfigReader.getAndroidAppPackage();
                String appActivity = ConfigReader.getAndroidAppActivity();
                if (appPackage != null && appActivity != null) {
                    options.setAppPackage(appPackage);
                    options.setAppActivity(appActivity);
                }

                if (execution == ExecutionTarget.SAUCELABS) {
                    SauceLabsManager.applySauceOptions(options, "SauceLabs Android Execution");
                    return new AndroidDriver(SauceLabsManager.getSauceUrl(), options);
                } else {
                    return new AndroidDriver(URI.create("http://127.0.0.1:4723").toURL(), options);
                }
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium URL: " + e.getMessage(), e);
        }
    }

    private static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (ConfigReader.isHeadless()) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        } else {
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-popup-blocking");
            Map<String, Object> prefs = new HashMap<>();
            prefs.put("profile.default_content_settings.popups", 0);
            options.setExperimentalOption("prefs", prefs);
        }
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--remote-allow-origins=*");
        return options;
    }
}
