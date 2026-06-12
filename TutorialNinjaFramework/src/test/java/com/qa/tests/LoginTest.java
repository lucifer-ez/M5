package com.qa.tests;

import com.qa.pages.HomePage;
import com.qa.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Validates the Login flow with both positive and negative
 * credential sets supplied via DataProviders.loginData().
 */
public class LoginTest extends BaseTest {

    @Test(dataProvider = "loginData",
            dataProviderClass = DataProviders.class,
            description = "Verify login behavior for valid and invalid credential sets")
    public void testLogin(String email, String password, boolean expectedSuccess) {
        logger.info("Running login test with email='{}', expectedSuccess={}", email, expectedSuccess);

        LoginPage loginPage = new HomePage(driver)
                .open(baseUrl)
                .goToLogin()
                .login(email, password);

        if (expectedSuccess) {
            Assert.assertTrue(loginPage.isLoginSuccessful(),
                    "Expected successful login but 'My Account' page was not shown");
        } else {
            Assert.assertTrue(loginPage.isErrorDisplayed(),
                    "Expected a login error message but none was displayed");
            logger.info("Error message shown: {}", loginPage.getErrorMessage());
        }
    }
}
