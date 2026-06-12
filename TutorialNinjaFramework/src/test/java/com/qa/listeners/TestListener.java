package com.qa.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.qa.factory.DriverFactory;
import com.qa.utils.ExtentManager;
import com.qa.utils.ScreenshotUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Central TestNG listener:
 *  - starts/ends ExtentTest nodes per test method
 *  - logs pass/fail/skip status with messages
 *  - on failure, captures a screenshot and attaches it to the report
 *  - flushes the ExtentReports instance at suite end
 */
public class TestListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = ExtentManager.startTest(
                result.getMethod().getMethodName(),
                result.getMethod().getDescription());
        test.assignCategory(result.getTestClass().getName());
        logger.info("===== STARTING TEST: {} =====", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentManager.getTest().log(Status.PASS, "Test passed");
        logger.info("TEST PASSED: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logger.error("TEST FAILED: {}", testName, result.getThrowable());

        WebDriver driver = DriverFactory.getDriver();
        if (driver != null) {
            String path = ScreenshotUtil.capture(driver, testName);
            if (path != null) {
                try {
                    ExtentManager.getTest().addScreenCaptureFromPath(path);
                } catch (Exception e) {
                    logger.warn("Could not attach screenshot to report: {}", e.getMessage());
                }
            }
        }
        ExtentManager.getTest().log(Status.FAIL, result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentManager.getTest().log(Status.SKIP, "Test skipped: " + result.getThrowable());
        logger.warn("TEST SKIPPED: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
        logger.info("===== SUITE FINISHED: {} =====", context.getName());
    }
}
