package vinscape.pages.it;

import com.microsoft.playwright.Page;
import vinscape.pages.AppShellPage;

/**
 * Page Object cho /app/it/documents (it/documents.jsp)
 * Trang nay chi cho phep IT XEM / XOA tai lieu (khong co upload, khac voi admin/documents.jsp).
 */
public class ITDocumentsPage extends AppShellPage {

    public ITDocumentsPage(Page page, String baseUrl) {
        super(page, baseUrl);
    }

    public String url() {
        return baseUrl + "/app/it/documents";
    }

    public void navigate() {
        page.navigate(url());
        sidebar().waitFor();
    }

    public int documentCount() {
        return page.locator("table tbody tr").count();
    }
}
