package com.qa.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Singleton ExtentReports manager. A single ExtentReports instance
 * is shared across all threads (it is thread-safe), while each
 * test method gets its own ExtentTest node via ThreadLocal so
 * parallel execution produces correctly-attributed report entries.
 */
public class ExtentManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> tlTest = new ThreadLocal<>();
    private static final Map<String, ExtentTest> testMap = new ConcurrentHashMap<>();

    private ExtentManager() {
    }

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("target/extent-reports/ExtentReport.html");
            spark.config().setDocumentTitle("TutorialNinja Automation Report");
            spark.config().setReportName("E-Commerce Regression Suite");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Application", "TutorialsNinja Demo Store");
            extent.setSystemInfo("Environment", "QA");
        }
        return extent;
    }

    public static ExtentTest startTest(String testName, String description) {
        ExtentTest test = getInstance().createTest(testName, description);
        tlTest.set(test);
        testMap.put(testName + "-" + Thread.currentThread().getId(), test);
        return test;
    }

    public static ExtentTest getTest() {
        return tlTest.get();
    }

    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
