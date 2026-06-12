package com.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the Order Success page
 * (route=checkout/success).
 */
public class OrderConfirmationPage extends BasePage {

    private final By successHeading = By.cssSelector("#content h1");
    private final By successMessage = By.cssSelector("#content p");

    public OrderConfirmationPage(WebDriver driver) {
        super(driver);
    }

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
}
