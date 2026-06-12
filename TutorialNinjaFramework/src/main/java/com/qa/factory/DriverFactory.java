package com.qa.factory;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Thread-safe Singleton + Factory pattern for WebDriver instances.
 * Each test thread gets its own driver instance via ThreadLocal,
 * making the framework safe for parallel execution.
 */
public class DriverFactory {

    private static final ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    private DriverFactory() {
        // private constructor enforces Singleton access via getDriver()
    }

    /**
     * Initializes the WebDriver for the current thread based on
     * the browser name. Factory pattern: chooses the concrete
     * driver implementation at runtime.
     *
     * @param browser chrome | firefox | edge
     * @return the initialized WebDriver instance
     */
    public static WebDriver initDriver(String browser) {
        WebDriver driver;

        switch (browser.trim().toLowerCase()) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions ffOptions = new FirefoxOptions();
                if (Boolean.parseBoolean(ConfigReader.get("headless"))) {
                    ffOptions.addArguments("-headless");
                }
                driver = new FirefoxDriver(ffOptions);
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (Boolean.parseBoolean(ConfigReader.get("headless"))) {
                    edgeOptions.addArguments("--headless=new");
                }
                driver = new EdgeDriver(edgeOptions);
                break;

            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (Boolean.parseBoolean(ConfigReader.get("headless"))) {
                    chromeOptions.addArguments("--headless=new");
                }
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--window-size=1920,1080");
                driver = new ChromeDriver(chromeOptions);
                break;
        }

        tlDriver.set(driver);
        return driver;
    }

    /**
     * Singleton accessor — returns the WebDriver instance bound
     * to the current thread.
     */
    public static WebDriver getDriver() {
        return tlDriver.get();
    }

    /**
     * Quits the driver for the current thread and clears the
     * ThreadLocal reference to avoid memory leaks.
     */
    public static void quitDriver() {
        WebDriver driver = tlDriver.get();
        if (driver != null) {
            driver.quit();
            tlDriver.remove();
        }
    }
}
