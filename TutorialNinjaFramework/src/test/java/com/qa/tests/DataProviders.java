package com.qa.tests;

import com.qa.factory.ConfigReader;
import org.testng.annotations.DataProvider;

/**
 * Centralized @DataProvider definitions used across test classes
 * for login (valid/invalid) and product search scenarios.
 */
public class DataProviders {

    @DataProvider(name = "loginData")
    public static Object[][] loginData() {
        String validEmail = ConfigReader.get("validEmail");
        String validPassword = ConfigReader.get("validPassword");

        return new Object[][]{
                // email, password, expectedSuccess
                {validEmail, validPassword, true},
                {"invalid_user@mailinator.com", "WrongPass123", false},
                {validEmail, "wrongPassword", false},
                {"", "", false}
        };
    }

    @DataProvider(name = "searchData")
    public static Object[][] searchData() {
        return new Object[][]{
                {"MacBook", true},
                {"iPhone", true},
                {"Apple Cinema 30\"", true},
                {"NonExistentProductXYZ123", false}
        };
    }

    @DataProvider(name = "cartProductData")
    public static Object[][] cartProductData() {
        return new Object[][]{
                {"MacBook"},
                {"iPhone"}
        };
    }
}
