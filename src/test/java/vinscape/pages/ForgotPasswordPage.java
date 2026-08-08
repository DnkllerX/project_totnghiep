package vinscape.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Page Object cho /forgot-password
 */
public class ForgotPasswordPage {

    private final Page page;
    private final String baseUrl;

    public ForgotPasswordPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public String url() {
        return baseUrl + "/forgot-password";
    }

    public void navigate() {
        page.navigate(url());
        emailInput().waitFor();
    }

    public Locator emailInput() {
        return page.locator("#email");
    }

    public Locator submitButton() {
        return page.getByRole(AriaRole.BUTTON);
    }

    public Locator message() {
        return page.locator(".alert-success, .alert-error, .success-box, .error-box");
    }

    public void requestReset(String email) {
        emailInput().fill(email);
        submitButton().click();
    }
}
