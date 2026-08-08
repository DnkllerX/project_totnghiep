package vinscape.pages.admin;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import vinscape.pages.AppShellPage;

/**
 * Page Object cho /app/admin/resolution (admin/resolution.jsp)
 */
public class AdminResolutionPage extends AppShellPage {

    public AdminResolutionPage(Page page, String baseUrl) {
        super(page, baseUrl);
    }

    public String url() {
        return baseUrl + "/app/admin/resolution";
    }

    public void navigate() {
        page.navigate(url());
        titleInput().waitFor();
    }

    public Locator titleInput() {
        return page.locator("[name='title']");
    }

    public Locator descriptionInput() {
        return page.locator("[name='description']");
    }

    public Locator startTimeInput() {
        return page.locator("[name='startTime']");
    }

    public Locator endTimeInput() {
        return page.locator("[name='endTime']");
    }

    public Locator submitButton() {
        return page.locator("button[type='submit']").first();
    }

    public int resolutionCount() {
        return page.locator("table tbody tr").count();
    }
}
