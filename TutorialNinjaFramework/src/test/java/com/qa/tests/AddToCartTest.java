package com.qa.tests;

import com.qa.pages.CartPage;
import com.qa.pages.HomePage;
import com.qa.pages.SearchResultsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Validates the add-to-cart mechanism: search for a product,
 * add it to cart, then confirm it appears on the Cart page.
 */
public class AddToCartTest extends BaseTest {

    @Test(dataProvider = "cartProductData",
            dataProviderClass = DataProviders.class,
            description = "Verify product can be added to cart and is reflected on the Cart page")
    public void testAddToCart(String productName) {
        logger.info("Adding product to cart: {}", productName);

        SearchResultsPage searchResults = new HomePage(driver)
                .open(baseUrl)
                .search(productName);

        searchResults.addProductToCart(productName);

        String successText = searchResults.getSuccessAlertText();
        Assert.assertTrue(successText.toLowerCase().contains("success"),
                "Expected success alert after adding '" + productName + "' to cart");

        CartPage cartPage = new CartPage(driver).open(baseUrl);
        Assert.assertTrue(cartPage.isProductInCart(productName),
                "Expected '" + productName + "' to be present in the cart");
        Assert.assertTrue(cartPage.getCartItemCount() > 0,
                "Expected at least one item in the cart");
    }
}
