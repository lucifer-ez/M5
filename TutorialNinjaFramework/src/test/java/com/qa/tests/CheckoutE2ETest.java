package com.qa.tests;

import com.qa.factory.ConfigReader;
import com.qa.pages.CartPage;
import com.qa.pages.CheckoutPage;
import com.qa.pages.HomePage;
import com.qa.pages.OrderConfirmationPage;
import com.qa.pages.SearchResultsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * End-to-end happy path: login -> search -> add to cart ->
 * full checkout wizard -> verify order confirmation screen.
 */
public class CheckoutE2ETest extends BaseTest {

    @Test(description = "Verify full E2E checkout flow ends with an order confirmation")
    public void testEndToEndCheckout() {
        String email = ConfigReader.get("validEmail");
        String password = ConfigReader.get("validPassword");
        String product = "MacBook";

        logger.info("Step 1: Login");
        new HomePage(driver)
                .open(baseUrl)
                .goToLogin()
                .login(email, password);

        logger.info("Step 2: Search for product '{}'", product);
        SearchResultsPage searchResults = new HomePage(driver).open(baseUrl).search(product);

        logger.info("Step 3: Add product to cart");
        searchResults.addProductToCart(product);

        CartPage cartPage = new CartPage(driver).open(baseUrl);
        Assert.assertTrue(cartPage.isProductInCart(product),
                "Product should be present in cart before checkout");

        logger.info("Step 4: Proceed through checkout wizard");
        CheckoutPage checkout = cartPage.proceedToCheckout();
        checkout.confirmPaymentAddress()
                .confirmShippingAddress()
                .selectShippingMethod()
                .selectPaymentMethodAndAgree()
                .confirmOrder();

        logger.info("Step 5: Verify order confirmation");
        OrderConfirmationPage confirmation = checkout.waitForOrderConfirmation();

        Assert.assertTrue(confirmation.isOrderConfirmed(),
                "Expected order confirmation heading to indicate the order was placed");
        logger.info("Order confirmation message: {}", confirmation.getConfirmationMessage());
    }
}
