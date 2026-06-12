package com.qa.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed @Test method up to MAX_RETRY_COUNT times
 * before TestNG marks it as a final failure. Attach via
 * retryAnalyzer = RetryAnalyzer.class on @Test annotations.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private static final int MAX_RETRY_COUNT = 2;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            System.out.println("Retrying test '" + result.getName()
                    + "' — attempt " + (retryCount + 1));
            return true;
        }
        return false;
    }
}
