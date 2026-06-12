package com.qa.tests;

import com.qa.pages.HomePage;
import com.qa.pages.SearchResultsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Validates product search results for multiple product names,
 * including a non-existent product to confirm graceful "no
 * results" handling.
 */
public class SearchTest extends BaseTest {

    @Test(dataProvider = "searchData",
            dataProviderClass = DataProviders.class,
            description = "Verify search results for valid and invalid product names")
    public void testProductSearch(String productName, boolean shouldExist) {
        logger.info("Searching product='{}', shouldExist={}", productName, shouldExist);

        new HomePage(driver).open(baseUrl).search(productName);
        SearchResultsPage results = new SearchResultsPage(driver);

        if (shouldExist) {
            Assert.assertTrue(results.isProductDisplayed(productName),
                    "Expected product '" + productName + "' to appear in search results");
        } else {
            Assert.assertTrue(results.isNoResults(),
                    "Expected no results for '" + productName + "' but products were found");
        }
    }
}
