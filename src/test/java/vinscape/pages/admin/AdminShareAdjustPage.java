package vinscape.pages.admin;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import vinscape.pages.AppShellPage;

/**
 * Page Object cho /app/admin/share-adjust (admin/share-adjust.jsp)
 */
public class AdminShareAdjustPage extends AppShellPage {

    public AdminShareAdjustPage(Page page, String baseUrl) {
        super(page, baseUrl);
    }

    public String url() {
        return baseUrl + "/app/admin/share-adjust";
    }

    public void navigate() {
        page.navigate(url());
        sidebar().waitFor();
    }

    public Locator shareholderIdInput() {
        return page.locator("[name='shareholderId']");
    }

    public Locator newQuantityInput() {
        return page.locator("[name='newQuantity']");
    }

    public Locator reasonInput() {
        return page.locator("[name='reason']");
    }

    public Locator submitButton() {
        return page.locator("button[type='submit']").first();
    }

    public Locator resultBox() {
        return page.locator(".error-box, .success-box");
    }
}
