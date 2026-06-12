package pages;

import base.base;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page Object for the global header search bar and the
 * product search results listing (route=product/search).
 */
public class search extends base {

    @FindBy(name = "search")
    private WebElement searchBox;

    @FindBy(css = "#search button")
    private WebElement searchButton;

    private final By productNames = By.cssSelector(".product-thumb h4 a");
    private final By productThumbs = By.cssSelector("div.product-thumb");

    public search(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public search open(String baseUrl) {
        logger.info("Navigating to base URL: {}", baseUrl);
        driver.get(baseUrl);
        return this;
    }

    public search search(String productName) {
        logger.info("Searching for product: {}", productName);
        waitVisible(By.name("search"));
        searchBox.clear();
        searchBox.sendKeys(productName);
        waitClickable(searchButton).click();
        return this;
    }

    public boolean isProductDisplayed(String productName) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(productNames));
        List<WebElement> results = driver.findElements(productNames);
        for (WebElement el : results) {
            if (el.getText().trim().equalsIgnoreCase(productName.trim())) {
                return true;
            }
        }
        return false;
    }

    public boolean isNoResults() {
        try {
            List<WebElement> thumbs = driver.findElements(productThumbs);
            return thumbs.isEmpty();
        } catch (Exception e) {
            return true;
        }
    }
}
