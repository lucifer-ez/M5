package com.qa.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object for the Login page.
 * https://tutorialsninja.com/demo/index.php?route=account/login
 */
public class LoginPage extends BasePage {

    @FindBy(id = "input-email")
    private org.openqa.selenium.WebElement emailField;

    @FindBy(id = "input-password")
    private org.openqa.selenium.WebElement passwordField;

    @FindBy(xpath = "//input[@value='Login']")
    private org.openqa.selenium.WebElement loginButton;

    @FindBy(css = "div.alert.alert-danger")
    private org.openqa.selenium.WebElement errorAlert;

    @FindBy(xpath = "//h2[contains(text(),'My Account')]")
    private org.openqa.selenium.WebElement myAccountHeading;

    public LoginPage(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    public LoginPage enterEmail(String email) {
        logger.info("Entering email: {}", email);
        waitVisible(By.id("input-email"));
        emailField.clear();
        emailField.sendKeys(email);
        return this;
    }

    public LoginPage enterPassword(String password) {
        logger.info("Entering password");
        passwordField.clear();
        passwordField.sendKeys(password);
        return this;
    }

    public LoginPage clickLogin() {
        logger.info("Clicking Login button");
        waitClickable(loginButton).click();
        return this;
    }

    /**
     * Fluent login wrapper combining email, password and submit.
     */
    public LoginPage login(String email, String password) {
        return enterEmail(email).enterPassword(password).clickLogin();
    }

    public boolean isLoginSuccessful() {
        try {
            wait.until(ExpectedConditions.visibilityOf(myAccountHeading));
            return myAccountHeading.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isErrorDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(errorAlert));
            return errorAlert.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorMessage() {
        return errorAlert.getText();
    }
}
