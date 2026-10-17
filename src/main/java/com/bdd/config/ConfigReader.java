package com.bdd.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Robust multi-level configuration reader.
 * Priority order for resolution:
 * 1. Java System Property (e.g. -DbaseUrl=http://localhost:8080 or -Dexecution=saucelabs)
 * 2. OS Environment Variable (e.g. BASE_URL or SAUCE_USERNAME)
 * 3. JSON Configuration (src/main/resources/config.json)
 */
public class ConfigReader {

    private static JsonObject config;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        // Try classpath first, then filesystem fallback
        try (InputStream is = ConfigReader.class.getClassLoader().getResourceAsStream("config.json")) {
            if (is != null) {
                config = JsonParser.parseReader(new InputStreamReader(is, StandardCharsets.UTF_8)).getAsJsonObject();
            } else {
                try (FileReader reader = new FileReader("src/main/resources/config.json")) {
                    config = JsonParser.parseReader(reader).getAsJsonObject();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.json: " + e.getMessage(), e);
        }
    }

    /**
     * Resolves a property by checking System properties, Environment variables,
     * and finally the JSON config (supporting dot notation like 'api.baseUri').
     */
    public static String getProperty(String key) {
        // 1. System property (-Dkey=value)
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.trim().isEmpty()) {
            return sysProp.trim();
        }

        // 2. Environment variable (KEY_NAME or dot-to-underscore)
        String envKey = key.replace('.', '_').toUpperCase();
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.trim().isEmpty()) {
            return envVal.trim();
        }

        // Direct env lookup
        String directEnv = System.getenv(key);
        if (directEnv != null && !directEnv.trim().isEmpty()) {
            return directEnv.trim();
        }

        // 3. JSON Configuration lookup (supports nested dot notation)
        return getNestedJsonString(key);
    }

    public static boolean getBooleanProperty(String key) {
        String val = getProperty(key);
        return val != null && Boolean.parseBoolean(val);
    }

    public static int getIntProperty(String key, int defaultValue) {
        String val = getProperty(key);
        if (val == null || val.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static String getNestedJsonString(String key) {
        if (config == null) return null;
        String[] parts = key.split("\\.");
        JsonObject current = config;
        for (int i = 0; i < parts.length - 1; i++) {
            if (current.has(parts[i]) && current.get(parts[i]).isJsonObject()) {
                current = current.getAsJsonObject(parts[i]);
            } else {
                return null;
            }
        }
        String leaf = parts[parts.length - 1];
        if (current.has(leaf)) {
            JsonElement el = current.get(leaf);
            if (el.isJsonPrimitive()) {
                return el.getAsString();
            }
        }
        return null;
    }

    // Convenience Getters for Web & General
    public static String getBaseUrl() {
        String url = getProperty("baseUrl");
        return (url != null && !url.isEmpty()) ? url : getProperty("url");
    }

    public static String getExecutionTarget() {
        String target = getProperty("execution");
        return (target != null && !target.isEmpty()) ? target.toLowerCase() : "local";
    }

    public static String getTargetPlatform() {
        String target = getProperty("target");
        return (target != null && !target.isEmpty()) ? target.toLowerCase() : "web";
    }

    public static String getBrowser() {
        String browser = getProperty("browser");
        return (browser != null && !browser.isEmpty()) ? browser.toLowerCase() : "chrome";
    }

    public static boolean isHeadless() {
        return getBooleanProperty("headless");
    }

    public static int getTimeout() {
        return getIntProperty("timeout", 10);
    }

    // Convenience Getters for API
    public static String getApiBaseUri() {
        String uri = getProperty("api.baseUri");
        return (uri != null && !uri.isEmpty()) ? uri : getBaseUrl();
    }

    // Convenience Getters for Sauce Labs
    public static String getSauceUsername() {
        return getProperty("saucelabs.username");
    }

    public static String getSauceAccessKey() {
        return getProperty("saucelabs.accessKey");
    }

    public static String getSauceRegion() {
        String region = getProperty("saucelabs.region");
        return (region != null && !region.isEmpty()) ? region : "us-west-1";
    }

    public static String getSauceHubUrl() {
        String region = getSauceRegion();
        String domain = region.equalsIgnoreCase("eu-central-1")
                ? "ondemand.eu-central-1.saucelabs.com"
                : "ondemand.us-west-1.saucelabs.com";
        return "https://" + getSauceUsername() + ":" + getSauceAccessKey() + "@" + domain + ":443/wd/hub";
    }

    // Convenience Getters for Mobile
    public static String getMobilePlatform() {
        String platform = getProperty("mobile.platformName");
        return (platform != null && !platform.isEmpty()) ? platform : "Android";
    }

    public static String getMobileDeviceName() {
        String platform = getMobilePlatform().toLowerCase();
        String name = getProperty("mobile." + platform + ".deviceName");
        return (name != null && !name.isEmpty()) ? name : getProperty("mobile.deviceName");
    }

    public static String getMobileAutomationName() {
        String platform = getMobilePlatform().toLowerCase();
        String auto = getProperty("mobile." + platform + ".automationName");
        if (auto != null && !auto.isEmpty()) return auto;
        return platform.equals("ios") ? "XCUITest" : "UiAutomator2";
    }

    public static String getMobileApp() {
        String platform = getMobilePlatform().toLowerCase();
        String app = getProperty("mobile." + platform + ".app");
        return (app != null && !app.isEmpty()) ? app : getProperty("mobile.app");
    }

    public static String getMobilePlatformVersion() {
        String platform = getMobilePlatform().toLowerCase();
        return getProperty("mobile." + platform + ".platformVersion");
    }

    public static String getIosBundleId() {
        return getProperty("mobile.ios.bundleId");
    }

    public static String getAndroidAppPackage() {
        String pkg = getProperty("mobile.android.appPackage");
        return (pkg != null && !pkg.isEmpty()) ? pkg : getProperty("mobile.appPackage");
    }

    public static String getAndroidAppActivity() {
        String act = getProperty("mobile.android.appActivity");
        return (act != null && !act.isEmpty()) ? act : getProperty("mobile.appActivity");
    }

    // Convenience Getters for Database (SQL)
    public static String getDatabaseUrl() {
        return getProperty("database.url");
    }

    public static String getDatabaseUsername() {
        return getProperty("database.username");
    }

    public static String getDatabasePassword() {
        return getProperty("database.password");
    }

    public static String getDatabaseDriver() {
        return getProperty("database.driver");
    }
}