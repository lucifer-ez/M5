package utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import base.driverfactory;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Central utilities for test execution reporting and resilience:
 *
 *  - {@link TestListener}   : ExtentReports lifecycle + failure screenshots (ITestListener)
 *  - {@link RetryAnalyzer}  : retries a failed @Test up to MAX_RETRY_COUNT times
 *  - {@link RetryTransformer}: globally attaches RetryAnalyzer to every @Test (IAnnotationTransformer)
 *  - {@link Screenshot}     : captures full-page screenshots on failure
 *
 * Register TestListener and RetryTransformer in testng.xml under <listeners>.
 */
public class listener {

    /**
     * Captures full-page screenshots and stores them under
     * target/screenshots/, returning the path so it can be
     * attached to the ExtentReports log.
     */
    public static class Screenshot {

        private static final String SCREENSHOT_DIR = "target/screenshots/";

        public static String capture(WebDriver driver, String testName) {
            try {
                File dir = new File(SCREENSHOT_DIR);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String fileName = testName + "_" + timestamp + ".png";
                File destFile = new File(dir, fileName);

                File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(srcFile, destFile);

                return destFile.getAbsolutePath();
            } catch (IOException | ClassCastException e) {
                return null;
            }
        }
    }

    /**
     * Retries a failed @Test method up to MAX_RETRY_COUNT times
     * before TestNG marks it as a final failure.
     */
    public static class RetryAnalyzer implements IRetryAnalyzer {

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

    /**
     * Globally registers RetryAnalyzer for every @Test method so the
     * retry mechanism applies framework-wide without annotating each
     * test individually.
     */
    public static class RetryTransformer implements IAnnotationTransformer {

        @Override
        public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }

    /**
     * Central TestNG listener:
     *  - starts/ends ExtentTest nodes per test method
     *  - logs pass/fail/skip status with messages
     *  - on failure, captures a screenshot and attaches it to the report
     *  - flushes the ExtentReports instance at suite end
     */
    public static class TestListener implements ITestListener {

        private static final Logger logger = LogManager.getLogger(TestListener.class);

        @Override
        public void onTestStart(ITestResult result) {
            ExtentTest test = reporter.startTest(
                    result.getMethod().getMethodName(),
                    result.getMethod().getDescription());
            test.assignCategory(result.getTestClass().getName());
            logger.info("===== STARTING TEST: {} =====", result.getMethod().getMethodName());
        }

        @Override
        public void onTestSuccess(ITestResult result) {
            reporter.getTest().log(Status.PASS, "Test passed");
            logger.info("TEST PASSED: {}", result.getMethod().getMethodName());
        }

        @Override
        public void onTestFailure(ITestResult result) {
            String testName = result.getMethod().getMethodName();
            logger.error("TEST FAILED: {}", testName, result.getThrowable());

            WebDriver driver = driverfactory.getDriver();
            if (driver != null) {
                String path = Screenshot.capture(driver, testName);
                if (path != null) {
                    try {
                        reporter.getTest().addScreenCaptureFromPath(path);
                    } catch (Exception e) {
                        logger.warn("Could not attach screenshot to report: {}", e.getMessage());
                    }
                }
            }
            reporter.getTest().log(Status.FAIL, result.getThrowable());
        }

        @Override
        public void onTestSkipped(ITestResult result) {
            reporter.getTest().log(Status.SKIP, "Test skipped: " + result.getThrowable());
            logger.warn("TEST SKIPPED: {}", result.getMethod().getMethodName());
        }

        @Override
        public void onFinish(ITestContext context) {
            reporter.flush();
            logger.info("===== SUITE FINISHED: {} =====", context.getName());
        }
    }
}
