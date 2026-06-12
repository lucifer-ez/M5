package pages;

import base.base;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object for the Login page.
 * https://tutorialsninja.com/demo/index.php?route=account/login
 */
public class login extends base {

    @FindBy(id = "input-email")
    private WebElement emailField;

    @FindBy(id = "input-password")
    private WebElement passwordField;

    @FindBy(xpath = "//input[@value='Login']")
    private WebElement loginButton;

    @FindBy(css = "div.alert.alert-danger")
    private WebElement errorAlert;

    @FindBy(xpath = "//h2[contains(text(),'My Account')]")
    private WebElement myAccountHeading;

    @FindBy(css = "#top-links")
    private WebElement topLinksMenu;

    @FindBy(linkText = "Login")
    private WebElement loginLink;

    public login(WebDriver driver) {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    /**
     * Navigates from the header "My Account" menu to the Login page.
     * Assumes the driver is already on a page with the global header.
     */
    public login open() {
        waitClickable(topLinksMenu).click();
        waitClickable(loginLink).click();
        return this;
    }

    public login enterEmail(String email) {
        logger.info("Entering email: {}", email);
        waitVisible(By.id("input-email"));
        emailField.clear();
        emailField.sendKeys(email);
        return this;
    }

    public login enterPassword(String password) {
        logger.info("Entering password");
        passwordField.clear();
        passwordField.sendKeys(password);
        return this;
    }

    public login clickLogin() {
        logger.info("Clicking Login button");
        waitClickable(loginButton).click();
        return this;
    }

    /**
     * Fluent login wrapper combining email, password and submit.
     */
    public login login(String email, String password) {
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
