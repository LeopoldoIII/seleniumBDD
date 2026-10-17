package com.bdd.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * TestNG IRetryAnalyzer implementation to automatically retry failed tests up to MAX_RETRY_COUNT.
 * Placed in src/test/java to align with TestNG test-scoped dependency.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private static final int MAX_RETRY_COUNT = 2;

    @Override
    public boolean retry(ITestResult result) {
        if (!result.isSuccess() && retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            System.out.println(">>> Retrying test: " + result.getName() + " | Attempt " + (retryCount + 1) + " of " + (MAX_RETRY_COUNT + 1));
            return true;
        }
        return false;
    }
}
