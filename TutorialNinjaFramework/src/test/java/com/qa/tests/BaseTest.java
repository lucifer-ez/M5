package com.qa.tests;

import com.qa.factory.ConfigReader;
import com.qa.factory.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

import java.time.Duration;

/**
 * Base class for all test classes. Provides the WebDriver
 * lifecycle (@BeforeMethod / @AfterClass) so each @Test method
 * runs in a fresh browser session, while the driver instance for
 * the whole class is cleaned up once all methods finish.
 */
public abstract class BaseTest {

    protected WebDriver driver;
    protected final Logger logger = LogManager.getLogger(this.getClass());
    protected final String baseUrl = ConfigReader.get("baseUrl");

    @BeforeMethod(alwaysRun = true)
    @Parameters("browser")
    public void setUp(org.testng.ITestContext context) {
        String browser = ConfigReader.get("browser");
        // allow overriding browser per <test> in testng.xml
        Object paramBrowser = context.getCurrentXmlTest().getParameter("browser");
        if (paramBrowser != null && !paramBrowser.toString().isEmpty()) {
            browser = paramBrowser.toString();
        }

        logger.info("Initializing {} driver", browser);
        driver = DriverFactory.initDriver(browser);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(Long.parseLong(ConfigReader.get("implicitWait"))));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        logger.info("Quitting driver");
        DriverFactory.quitDriver();
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        logger.info("Finished all tests in {}", this.getClass().getSimpleName());
        // Safeguard: ensure no orphaned driver remains for this thread
        DriverFactory.quitDriver();
    }
}
