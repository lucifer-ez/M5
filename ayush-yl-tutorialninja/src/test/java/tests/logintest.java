package tests;

import base.BaseTest;
import com.fasterxml.jackson.databind.JsonNode;
import pages.login;
import pages.search;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.testdata;

/**
 * Validates the Login flow (valid/invalid credential datasets)
 * and Product Search flow (multiple datasets), both driven from
 * src/test/resources/data/testdata.json.
 */
public class logintest extends BaseTest {

    @DataProvider(name = "loginData")
    public Object[][] loginData() {
        JsonNode loginCases = testdata.get().get("login");
        Object[][] data = new Object[loginCases.size()][3];
        for (int i = 0; i < loginCases.size(); i++) {
            JsonNode c = loginCases.get(i);
            data[i][0] = c.get("email").asText();
            data[i][1] = c.get("password").asText();
            data[i][2] = c.get("expectedSuccess").asBoolean();
        }
        return data;
    }

    @DataProvider(name = "searchData")
    public Object[][] searchData() {
        JsonNode searchCases = testdata.get().get("search");
        Object[][] data = new Object[searchCases.size()][2];
        for (int i = 0; i < searchCases.size(); i++) {
            JsonNode c = searchCases.get(i);
            data[i][0] = c.get("productName").asText();
            data[i][1] = c.get("shouldExist").asBoolean();
        }
        return data;
    }

    @Test(dataProvider = "loginData",
            description = "Verify login behavior for valid and invalid credential sets")
    public void testLogin(String email, String password, boolean expectedSuccess) {
        logger.info("Running login test with email='{}', expectedSuccess={}", email, expectedSuccess);

        driver.get(baseUrl);
        login loginPage = new login(driver).open().login(email, password);

        if (expectedSuccess) {
            Assert.assertTrue(loginPage.isLoginSuccessful(),
                    "Expected successful login but 'My Account' page was not shown");
        } else {
            Assert.assertTrue(loginPage.isErrorDisplayed(),
                    "Expected a login error message but none was displayed");
            logger.info("Error message shown: {}", loginPage.getErrorMessage());
        }
    }

    @Test(dataProvider = "searchData",
            description = "Verify search results for valid and invalid product names")
    public void testProductSearch(String productName, boolean shouldExist) {
        logger.info("Searching product='{}', shouldExist={}", productName, shouldExist);

        search searchPage = new search(driver).open(baseUrl).search(productName);

        if (shouldExist) {
            Assert.assertTrue(searchPage.isProductDisplayed(productName),
                    "Expected product '" + productName + "' to appear in search results");
        } else {
            Assert.assertTrue(searchPage.isNoResults(),
                    "Expected no results for '" + productName + "' but products were found");
        }
    }
}
