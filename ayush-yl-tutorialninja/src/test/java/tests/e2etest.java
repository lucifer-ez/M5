package tests;

import base.BaseTest;
import com.fasterxml.jackson.databind.JsonNode;
import pages.cart;
import pages.login;
import pages.product;
import pages.search;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.testdata;

/**
 * Validates the add-to-cart mechanism with multiple datasets, and
 * the full end-to-end flow: login -> search -> add to cart ->
 * checkout wizard -> order confirmation.
 */
public class e2etest extends BaseTest {

    @DataProvider(name = "cartProductData")
    public Object[][] cartProductData() {
        JsonNode cases = testdata.get().get("cartProducts");
        Object[][] data = new Object[cases.size()][1];
        for (int i = 0; i < cases.size(); i++) {
            data[i][0] = cases.get(i).get("productName").asText();
        }
        return data;
    }

    @Test(dataProvider = "cartProductData",
            description = "Verify product can be added to cart and is reflected on the Cart page")
    public void testAddToCart(String productName) {
        logger.info("Adding product to cart: {}", productName);

        search searchPage = new search(driver).open(baseUrl).search(productName);
        product productPage = new product(driver).addToCart(productName);

        String successText = productPage.getSuccessAlertText();
        Assert.assertTrue(successText.toLowerCase().contains("success"),
                "Expected success alert after adding '" + productName + "' to cart");

        cart cartPage = new cart(driver).open(baseUrl);
        Assert.assertTrue(cartPage.isProductInCart(productName),
                "Expected '" + productName + "' to be present in the cart");
        Assert.assertTrue(cartPage.getCartItemCount() > 0,
                "Expected at least one item in the cart");
    }

    @Test(description = "Verify full E2E checkout flow ends with an order confirmation")
    public void testEndToEndCheckout() {
        JsonNode validUser = testdata.get().get("validUser");
        String email = validUser.get("email").asText();
        String password = validUser.get("password").asText();
        String productName = testdata.get().get("checkoutProduct").asText();

        logger.info("Step 1: Login");
        driver.get(baseUrl);
        new login(driver).open().login(email, password);

        logger.info("Step 2: Search for product '{}'", productName);
        new search(driver).open(baseUrl).search(productName);

        logger.info("Step 3: Add product to cart");
        new product(driver).addToCart(productName);

        cart cartPage = new cart(driver).open(baseUrl);
        Assert.assertTrue(cartPage.isProductInCart(productName),
                "Product should be present in cart before checkout");

        logger.info("Step 4: Proceed through checkout wizard");
        cartPage.openCheckout(baseUrl)
                .confirmPaymentAddress()
                .confirmShippingAddress()
                .selectShippingMethod()
                .selectPaymentMethodAndAgree()
                .confirmOrder()
                .waitForOrderConfirmation();

        logger.info("Step 5: Verify order confirmation");
        Assert.assertTrue(cartPage.isOrderConfirmed(),
                "Expected order confirmation heading to indicate the order was placed");
        logger.info("Order confirmation message: {}", cartPage.getConfirmationMessage());
    }
}
