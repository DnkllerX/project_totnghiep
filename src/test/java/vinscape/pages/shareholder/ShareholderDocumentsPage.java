package vinscape.pages.shareholder;

import com.microsoft.playwright.Page;
import vinscape.pages.AppShellPage;

/**
 * Page Object cho /app/shareholder/documents (shareholder/documents.jsp)
 */
public class ShareholderDocumentsPage extends AppShellPage {

    public ShareholderDocumentsPage(Page page, String baseUrl) {
        super(page, baseUrl);
    }

    public String url() {
        return baseUrl + "/app/shareholder/documents";
    }

    public void navigate() {
        page.navigate(url());
        sidebar().waitFor();
    }

    public int documentCount() {
        return page.locator("table tbody tr").count();
    }

    public com.microsoft.playwright.Locator previewPanel() {
        return page.locator("#previewPanel");
    }
}
