package pages;

import base.base;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page Object covering the Shopping Cart, the multi-step Checkout
 * wizard, and the Order Confirmation screen
 * (routes: checkout/cart, checkout/checkout, checkout/success).
 */
public class cart extends base {

    // ---- Cart page elements ----

    @FindBy(css = "table.table tbody tr")
    private List<WebElement> cartRows;

    @FindBy(linkText = "Checkout")
    private WebElement checkoutLink;

    // ---- Checkout wizard elements ----

    @FindBy(id = "button-account")
    private WebElement paymentAddressContinueBtn;

    @FindBy(id = "button-shipping-address")
    private WebElement shippingAddressContinueBtn;

    @FindBy(id = "button-shipping-method")
    private WebElement shippingMethodContinueBtn;

    @FindBy(id = "button-payment-method")
    private WebElement paymentMethodContinueBtn;

    @FindBy(id = "button-confirm")
    private WebElement confirmOrderBtn;

    @FindBy(name = "agree")
    private WebElement agreeTermsCheckbox;

    // ---- Order confirmation elements ----

    private final By successHeading = By.cssSelector("#content h1");
    private final By successMessage = By.cssSelector("#content p");

    public cart(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    // ---- Cart page ----

    public cart open(String baseUrl) {
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
    public cart proceedToCheckout() {
        logger.info("Proceeding to checkout");
        waitClickable(checkoutLink).click();
        return this;
    }

    public cart openCheckout(String baseUrl) {
        driver.get(baseUrl + "index.php?route=checkout/checkout");
        return this;
    }

    // ---- Checkout wizard (fluent, chainable) ----

    public cart confirmPaymentAddress() {
        logger.info("Step 1: Confirm Payment Address");
        clickContinue(paymentAddressContinueBtn, "collapse-shipping-address");
        return this;
    }

    public cart confirmShippingAddress() {
        logger.info("Step 2: Confirm Shipping Address");
        clickContinue(shippingAddressContinueBtn, "collapse-shipping-method");
        return this;
    }

    public cart selectShippingMethod() {
        logger.info("Step 3: Select Shipping Method");
        WebElement radio = waitClickable(By.cssSelector("input[name='shipping_method']"));
        radio.click();
        clickContinue(shippingMethodContinueBtn, "collapse-payment-method");
        return this;
    }

    public cart selectPaymentMethodAndAgree() {
        logger.info("Step 4: Select Payment Method and accept terms");
        WebElement radio = waitClickable(By.cssSelector("input[name='payment_method']"));
        radio.click();

        if (!agreeTermsCheckbox.isSelected()) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", agreeTermsCheckbox);
            agreeTermsCheckbox.click();
        }
        clickContinue(paymentMethodContinueBtn, "collapse-confirm");
        return this;
    }

    public cart confirmOrder() {
        logger.info("Step 5: Confirm Order");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", confirmOrderBtn);
        waitClickable(confirmOrderBtn).click();
        return this;
    }

    /**
     * Waits for the OpenCart "Your order has been placed" success page,
     * which is rendered after the AJAX confirm-order call completes.
     */
    public cart waitForOrderConfirmation() {
        logger.info("Waiting for order confirmation page");
        wait.until(ExpectedConditions.urlContains("route=checkout/success"));
        return this;
    }

    // ---- Order confirmation ----

    public String getHeadingText() {
        return waitVisible(successHeading).getText();
    }

    public boolean isOrderConfirmed() {
        String heading = getHeadingText().trim();
        return heading.equalsIgnoreCase("Your order has been placed!")
                || heading.toLowerCase().contains("order has been placed");
    }

    public String getConfirmationMessage() {
        return waitVisible(successMessage).getText();
    }

    private void clickContinue(WebElement continueBtn, String nextCollapseId) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", continueBtn);
        waitClickable(continueBtn).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(nextCollapseId)));
    }
}
