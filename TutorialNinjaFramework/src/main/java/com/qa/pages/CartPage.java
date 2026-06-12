package com.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

/**
 * Page Object for the Shopping Cart page
 * (route=checkout/cart).
 */
public class CartPage extends BasePage {

    @FindBy(css = "table.table tbody tr")
    private List<WebElement> cartRows;

    @FindBy(linkText = "Checkout")
    private WebElement checkoutLink;

    @FindBy(css = "#top-links")
    private WebElement topLinksMenu;

    @FindBy(css = "a#cart")
    private WebElement cartIcon;

    public CartPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public CartPage open(String baseUrl) {
        driver.get(baseUrl + "index.php?route=checkout/cart");
        return this;
    }

    public boolean isProductInCart(String productName) {
        waitVisible(By.cssSelector("table.table tbody tr"));
        for (WebElement row : cartRows) {
            WebElement nameCell = row.findElement(By.cssSelector("td.text-left a, td.text-left"));
            if (nameCell.getText().trim().toLowerCase().contains(productName.trim().toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public int getCartItemCount() {
        waitVisible(By.cssSelector("table.table tbody tr"));
        return cartRows.size();
    }

    /**
     * Navigates to checkout via the page's Checkout link.
     */
    public CheckoutPage proceedToCheckout() {
        logger.info("Proceeding to checkout");
        waitClickable(checkoutLink).click();
        return new CheckoutPage(driver);
    }
}
