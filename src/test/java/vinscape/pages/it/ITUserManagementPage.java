package vinscape.pages.it;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import vinscape.pages.AppShellPage;

/**
 * Page Object cho /app/it/user-management (it/user-management.jsp)
 */
public class ITUserManagementPage extends AppShellPage {

    public ITUserManagementPage(Page page, String baseUrl) {
        super(page, baseUrl);
    }

    public String url() {
        return baseUrl + "/app/it/user-management";
    }

    public void navigate() {
        page.navigate(url());
        sidebar().waitFor();
    }

    // --- Form tao tai khoan moi ---
    public Locator usernameInput() {
        return page.locator("[name='username']");
    }

    public Locator emailInput() {
        return page.locator("[name='email']");
    }

    public Locator createPasswordInput() {
        return page.locator("#createPassword");
    }

    public Locator roleSelect() {
        return page.locator("[name='role']");
    }

    // --- Tabs / bo loc ---
    public void switchTab(String tabValue) {
        page.locator("a[href*='tab=" + tabValue + "'], [data-tab='" + tabValue + "']").first().click();
    }

    // --- Modal chinh sua ---
    public Locator editModal() {
        return page.locator("#editModal");
    }

    public Locator editUsernameInput() {
        return page.locator("#editUsername");
    }

    public Locator editEmailInput() {
        return page.locator("#editEmail");
    }

    public Locator editRoleSelect() {
        return page.locator("#editRole");
    }

    public int userRowCount() {
        return page.locator("table tbody tr").count();
    }

    public Locator resultBox() {
        return page.locator(".error-box, .success-box");
    }
}
