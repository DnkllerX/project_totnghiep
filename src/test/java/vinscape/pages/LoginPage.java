package vinscape.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Page Object cho /login (src/main/webapp/login.jsp)
 */
public class LoginPage {

    private final Page page;
    private final String baseUrl;

    public LoginPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public String url() {
        return baseUrl + "/login";
    }

    public void navigate() {
        page.navigate(url());
        usernameInput().waitFor();
    }

    public Locator usernameInput() {
        return page.locator("#username");
    }

    public Locator passwordInput() {
        return page.locator("#password");
    }

    public Locator submitButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Log in"));
    }

    public Locator errorAlert() {
        return page.locator(".alert-error");
    }

    public Locator successAlert() {
        return page.locator(".alert-success");
    }

    public Locator forgotPasswordLink() {
        return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Forgot Password?"));
    }

    public Locator signUpLink() {
        return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Sign up"));
    }

    /** Dien form va bam dang nhap, KHONG cho doi chuyen trang (dung khi test loi). */
    public void submitLogin(String loginOrEmail, String password) {
        usernameInput().fill(loginOrEmail);
        passwordInput().fill(password);
        submitButton().click();
    }

    /** Dang nhap va cho toi khi da chuyen huong xong (dashboard). */
    public void login(String loginOrEmail, String password) {
        submitLogin(loginOrEmail, password);
        page.waitForURL("**/app/dashboard**");
    }
}
