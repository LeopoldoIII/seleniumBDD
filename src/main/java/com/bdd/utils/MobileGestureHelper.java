package com.bdd.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.connection.ConnectionStateBuilder;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Mobile Gesture Helper utilizing Appium 9+ W3C Actions API and native mobile scripts.
 * Replaces legacy Appium TouchAction with standards-compliant gestures.
 */
public class MobileGestureHelper {

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    private final AppiumDriver driver;

    public MobileGestureHelper(AppiumDriver driver) {
        this.driver = driver;
    }

    public MobileGestureHelper(WebDriver driver) {
        if (driver instanceof AppiumDriver) {
            this.driver = (AppiumDriver) driver;
        } else {
            throw new IllegalArgumentException("Driver must be an instance of AppiumDriver for mobile gestures.");
        }
    }

    /**
     * Performs a W3C Pointer swipe in the specified direction.
     *
     * @param direction UP, DOWN, LEFT, RIGHT
     * @param percent   distance of swipe between 0.1 and 0.9 (e.g. 0.5 for 50% screen swipe)
     */
    public void swipe(Direction direction, double percent) {
        Dimension size = driver.manage().window().getSize();
        int width = size.getWidth();
        int height = size.getHeight();

        int startX = width / 2;
        int startY = height / 2;
        int endX = startX;
        int endY = startY;

        switch (direction) {
            case UP:
                startY = (int) (height * (0.5 + (percent / 2)));
                endY = (int) (height * (0.5 - (percent / 2)));
                break;
            case DOWN:
                startY = (int) (height * (0.5 - (percent / 2)));
                endY = (int) (height * (0.5 + (percent / 2)));
                break;
            case LEFT:
                startX = (int) (width * (0.5 + (percent / 2)));
                endX = (int) (width * (0.5 - (percent / 2)));
                break;
            case RIGHT:
                startX = (int) (width * (0.5 - (percent / 2)));
                endX = (int) (width * (0.5 + (percent / 2)));
                break;
        }

        performW3cSwipe(new Point(startX, startY), new Point(endX, endY), Duration.ofMillis(400));
    }

    public void scrollDown() {
        swipe(Direction.UP, 0.5);
    }

    public void scrollUp() {
        swipe(Direction.DOWN, 0.5);
    }

    /**
     * Repeatedly scrolls down until an element with the matching visible text is displayed or max attempts reached.
     */
    public WebElement scrollToText(String visibleText, int maxSwipes) {
        for (int i = 0; i < maxSwipes; i++) {
            List<WebElement> elements = driver.findElements(By.xpath("//*[@text='" + visibleText + "' or @name='" + visibleText + "' or contains(@label,'" + visibleText + "')]"));
            if (!elements.isEmpty() && elements.get(0).isDisplayed()) {
                return elements.get(0);
            }
            scrollDown();
        }
        throw new NoSuchElementException("Element containing text '" + visibleText + "' not found after " + maxSwipes + " scrolls.");
    }

    public WebElement scrollToText(String visibleText) {
        return scrollToText(visibleText, 5);
    }

    /**
     * Long press on a WebElement using W3C pointer sequence.
     */
    public void longPress(WebElement element, Duration duration) {
        Point location = element.getLocation();
        Dimension size = element.getSize();
        Point center = new Point(location.getX() + (size.getWidth() / 2), location.getY() + (size.getHeight() / 2));

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence sequence = new Sequence(finger, 1);

        sequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), center.x, center.y));
        sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        sequence.addAction(new Pause(finger, duration));
        sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(sequence));
    }

    /**
     * Drag and drop from source WebElement to target WebElement.
     */
    public void dragAndDrop(WebElement source, WebElement target) {
        Point sourceCenter = getCenter(source);
        Point targetCenter = getCenter(target);
        dragAndDrop(sourceCenter, targetCenter);
    }

    /**
     * Drag and drop between coordinates using W3C pointer sequence.
     */
    public void dragAndDrop(Point fromPoint, Point toPoint) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence sequence = new Sequence(finger, 1);

        sequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), fromPoint.x, fromPoint.y));
        sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        sequence.addAction(new Pause(finger, Duration.ofMillis(300)));
        sequence.addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), toPoint.x, toPoint.y));
        sequence.addAction(new Pause(finger, Duration.ofMillis(200)));
        sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(sequence));
    }

    /**
     * Toggles Airplane Mode for Android devices/emulators.
     */
    public void setAirplaneMode(boolean enable) {
        if (driver instanceof AndroidDriver) {
            AndroidDriver androidDriver = (AndroidDriver) driver;
            if (enable) {
                androidDriver.setConnection(new ConnectionStateBuilder().withAirplaneModeEnabled().build());
            } else {
                androidDriver.setConnection(new ConnectionStateBuilder().withWiFiEnabled().withDataEnabled().build());
            }
        } else {
            System.out.println("[WARN] Airplane mode programmatic toggle is only supported on AndroidDriver");
        }
    }

    /**
     * Toggles Wi-Fi connectivity for Android devices/emulators.
     */
    public void setWifiConnection(boolean enable) {
        if (driver instanceof AndroidDriver) {
            AndroidDriver androidDriver = (AndroidDriver) driver;
            if (enable) {
                androidDriver.setConnection(new ConnectionStateBuilder().withWiFiEnabled().build());
            } else {
                androidDriver.setConnection(new ConnectionStateBuilder().withWiFiDisabled().build());
            }
        }
    }

    private Point getCenter(WebElement element) {
        Point location = element.getLocation();
        Dimension size = element.getSize();
        return new Point(location.getX() + (size.getWidth() / 2), location.getY() + (size.getHeight() / 2));
    }

    private void performW3cSwipe(Point start, Point end, Duration duration) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipeSequence = new Sequence(finger, 1);

        swipeSequence.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), start.x, start.y));
        swipeSequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipeSequence.addAction(finger.createPointerMove(duration, PointerInput.Origin.viewport(), end.x, end.y));
        swipeSequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Collections.singletonList(swipeSequence));
    }
}
