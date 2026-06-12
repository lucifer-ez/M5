package pages;

import base.base;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page Object representing product interactions on the search
 * results listing — adding a product to the cart and reading
 * the resulting success alert.
 */
public class product extends base {

    private final By productThumbs = By.cssSelector("div.product-thumb");
    private final By successAlert = By.cssSelector("div.alert.alert-success");

    public product(WebDriver driver) {
        super(driver);
    }

    /**
     * Adds the matching product to the cart by locating its
     * product-thumb container and clicking its Add to Cart button.
     */
    public product addToCart(String productName) {
        logger.info("Adding product to cart: {}", productName);
        wait.until(ExpectedConditions.visibilityOfElementLocated(productThumbs));

        List<WebElement> thumbs = driver.findElements(productThumbs);
        for (WebElement thumb : thumbs) {
            WebElement nameLink = thumb.findElement(By.cssSelector("h4 a"));
            if (nameLink.getText().trim().equalsIgnoreCase(productName.trim())) {
                WebElement addToCartBtn = thumb.findElement(
                        By.cssSelector("button[onclick*='cart.add']"));

                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].scrollIntoView({block:'center'});", addToCartBtn);
                wait.until(ExpectedConditions.elementToBeClickable(addToCartBtn)).click();
                return this;
            }
        }
        throw new org.openqa.selenium.NoSuchElementException(
                "Product not found in search results: " + productName);
    }

    public String getSuccessAlertText() {
        return waitVisible(successAlert).getText();
    }
}
