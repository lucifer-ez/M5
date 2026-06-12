package com.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page Object for the Home page / global header search bar.
 * https://tutorialsninja.com/demo/
 */
public class HomePage extends BasePage {

    @FindBy(name = "search")
    private WebElement searchBox;

    @FindBy(css = "#search button")
    private WebElement searchButton;

    @FindBy(css = "a#wishlist-total")
    private WebElement wishlistLink;

    @FindBy(linkText = "Login")
    private WebElement loginLink;

    @FindBy(css = "#top-links")
    private WebElement topLinksMenu;

    public HomePage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public HomePage open(String baseUrl) {
        logger.info("Navigating to base URL: {}", baseUrl);
        driver.get(baseUrl);
        return this;
    }

    /**
     * Hovers/expands the My Account top-links menu then clicks Login.
     */
    public LoginPage goToLogin() {
        logger.info("Navigating to Login page");
        waitClickable(topLinksMenu).click();
        waitClickable(loginLink).click();
        return new LoginPage(driver);
    }

    public HomePage search(String productName) {
        logger.info("Searching for product: {}", productName);
        waitVisible(By.name("search"));
        searchBox.clear();
        searchBox.sendKeys(productName);
        waitClickable(searchButton).click();
        return this;
    }
}
