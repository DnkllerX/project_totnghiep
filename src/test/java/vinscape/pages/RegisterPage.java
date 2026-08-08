package vinscape.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Page Object cho /register (src/main/webapp/register.jsp)
 */
public class RegisterPage {

    private final Page page;
    private final String baseUrl;

    public RegisterPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public String url() {
        return baseUrl + "/register";
    }

    public void navigate() {
        page.navigate(url());
        usernameInput().waitFor();
    }

    public Locator usernameInput() {
        return page.locator("[name='username']");
    }

    public Locator emailInput() {
        return page.locator("[name='email']");
    }

    public Locator passwordInput() {
        return page.locator("#registerPassword");
    }

    public Locator fullNameInput() {
        return page.locator("[name='fullName']");
    }

    public Locator citizenIdInput() {
        return page.locator("[name='citizenId']");
    }

    public Locator phoneInput() {
        return page.locator("[name='phone']");
    }

    public Locator addressInput() {
        return page.locator("[name='address']");
    }

    public Locator birthDateInput() {
        return page.locator("[name='birthDate']");
    }

    public Locator nationalityInput() {
        return page.locator("[name='nationality']");
    }

    public Locator submitButton() {
        return page.getByRole(AriaRole.BUTTON);
    }

    public Locator errorAlert() {
        return page.locator(".alert-error, .error-box");
    }

    public Locator passwordStrengthLabel() {
        return page.locator("#registerPasswordLabel");
    }
}
