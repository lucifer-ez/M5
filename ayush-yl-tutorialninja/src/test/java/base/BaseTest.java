package base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import utils.reader;

import java.time.Duration;

/**
 * Base class for all test classes (src/test/java/tests). Provides
 * the WebDriver lifecycle (@BeforeMethod / @AfterMethod / @AfterClass)
 * so each @Test method runs in a fresh browser session, with a
 * class-level safeguard cleanup once all methods finish.
 */
public abstract class BaseTest {

    protected WebDriver driver;
    protected final Logger logger = LogManager.getLogger(this.getClass());
    protected final String baseUrl = reader.get("baseUrl");

    @BeforeMethod(alwaysRun = true)
    @Parameters("browser")
    public void setUp(ITestContext context) {
        String browser = reader.get("browser");
        Object paramBrowser = context.getCurrentXmlTest().getParameter("browser");
        if (paramBrowser != null && !paramBrowser.toString().isEmpty()) {
            browser = paramBrowser.toString();
        }

        logger.info("Initializing {} driver", browser);
        driver = driverfactory.initDriver(browser);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(
                Duration.ofSeconds(Long.parseLong(reader.get("implicitWait"))));
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        logger.info("Quitting driver");
        driverfactory.quitDriver();
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        logger.info("Finished all tests in {}", this.getClass().getSimpleName());
        driverfactory.quitDriver();
    }
}
