package com.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object for the multi-step Checkout wizard
 * (route=checkout/checkout) for a logged-in customer.
 * Each accordion section (Payment Address, Shipping Address,
 * Shipping Method, Payment Method, Confirm Order) is handled
 * with fluent chainable methods.
 */
public class CheckoutPage extends BasePage {

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

    @FindBy(name = "comment")
    private WebElement orderCommentBox;

    public CheckoutPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public CheckoutPage open(String baseUrl) {
        driver.get(baseUrl + "index.php?route=checkout/checkout");
        return this;
    }

    /**
     * Confirms the existing Payment Address (logged-in user already
     * has an address on file) and continues to the next step.
     */
    public CheckoutPage confirmPaymentAddress() {
        logger.info("Step 1: Confirm Payment Address");
        clickContinue(paymentAddressContinueBtn, "collapse-shipping-address");
        return this;
    }

    public CheckoutPage confirmShippingAddress() {
        logger.info("Step 2: Confirm Shipping Address");
        clickContinue(shippingAddressContinueBtn, "collapse-shipping-method");
        return this;
    }

    public CheckoutPage selectShippingMethod() {
        logger.info("Step 3: Select Shipping Method");
        // default flat-rate radio
        WebElement radio = waitClickable(
                By.cssSelector("input[name='shipping_method']"));
        radio.click();
        clickContinue(shippingMethodContinueBtn, "collapse-payment-method");
        return this;
    }

    public CheckoutPage selectPaymentMethodAndAgree() {
        logger.info("Step 4: Select Payment Method and accept terms");
        WebElement radio = waitClickable(
                By.cssSelector("input[name='payment_method']"));
        radio.click();

        if (!agreeTermsCheckbox.isSelected()) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollIntoView({block:'center'});", agreeTermsCheckbox);
            agreeTermsCheckbox.click();
        }
        clickContinue(paymentMethodContinueBtn, "collapse-confirm");
        return this;
    }

    public CheckoutPage confirmOrder() {
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
    public OrderConfirmationPage waitForOrderConfirmation() {
        logger.info("Waiting for order confirmation page");
        wait.until(ExpectedConditions.urlContains("route=checkout/success"));
        return new OrderConfirmationPage(driver);
    }

    private void clickContinue(WebElement continueBtn, String nextCollapseId) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", continueBtn);
        waitClickable(continueBtn).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(nextCollapseId)));
    }
}
