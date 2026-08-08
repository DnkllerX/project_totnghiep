package vinscape.pages.admin;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import vinscape.pages.AppShellPage;

/**
 * Page Object cho /app/admin/share-issue (admin/share-issue.jsp)
 */
public class AdminShareIssuePage extends AppShellPage {

    public AdminShareIssuePage(Page page, String baseUrl) {
        super(page, baseUrl);
    }

    public String url() {
        return baseUrl + "/app/admin/share-issue";
    }

    public void navigate() {
        page.navigate(url());
        form().waitFor();
    }

    public Locator form() {
        return page.locator("#issueForm");
    }

    public Locator titleInput() {
        return page.locator("[name='title']");
    }

    public Locator descriptionInput() {
        return page.locator("[name='description']");
    }

    public Locator issueRatioInput() {
        return page.locator("#issueRatio");
    }

    public Locator issueDateInput() {
        return page.locator("#issueDate");
    }

    public Locator startDateInput() {
        return page.locator("#startDate");
    }

    public Locator endDateInput() {
        return page.locator("#endDate");
    }

    public Locator estimatedTotalPreview() {
        return page.locator("#estimatedTotalPreview");
    }
}
