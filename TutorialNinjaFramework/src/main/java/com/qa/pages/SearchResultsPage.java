package com.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page Object for the product search results listing
 * (route=product/search).
 */
public class SearchResultsPage extends BasePage {

    private final By productNames = By.cssSelector(".product-thumb h4 a");
    private final By productThumbs = By.cssSelector("div.product-thumb");
    private final By noResultsHeading = By.cssSelector("#content h1");

    public SearchResultsPage(WebDriver driver) {
        super(driver);
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

    /**
     * Adds the matching product to the cart by hovering the
     * product-thumb container and clicking its Add to Cart button.
     */
    public SearchResultsPage addProductToCart(String productName) {
        logger.info("Adding product to cart: {}", productName);
        wait.until(ExpectedConditions.visibilityOfElementLocated(productThumbs));

        List<WebElement> thumbs = driver.findElements(productThumbs);
        for (WebElement thumb : thumbs) {
            WebElement nameLink = thumb.findElement(By.cssSelector("h4 a"));
            if (nameLink.getText().trim().equalsIgnoreCase(productName.trim())) {
                WebElement addToCartBtn = thumb.findElement(
                        By.cssSelector("button[onclick*='cart.add']"));

                // Scroll into view then click via JS to avoid overlay/hover issues
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
        By alert = By.cssSelector("div.alert.alert-success");
        return waitVisible(alert).getText();
    }
}
