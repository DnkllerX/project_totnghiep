package vinscape.pages.admin;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import vinscape.pages.AppShellPage;

/**
 * Page Object cho /app/admin/documents (admin/documents.jsp)
 * Cung duoc IT dung lai (@WebServlet dung chung /app/admin/documents va /app/it/documents).
 */
public class AdminDocumentsPage extends AppShellPage {

    public AdminDocumentsPage(Page page, String baseUrl) {
        super(page, baseUrl);
    }

    public String url() {
        return baseUrl + "/app/admin/documents";
    }

    public void navigate() {
        page.navigate(url());
        sidebar().waitFor();
    }

    public Locator titleInput() {
        return page.locator("[name='title']");
    }

    public Locator descriptionInput() {
        return page.locator("[name='description']");
    }

    public Locator fileInput() {
        return page.locator("[name='file']");
    }

    public Locator uploadSubmitButton() {
        return page.locator("button[type='submit']").first();
    }

    public int documentCount() {
        return page.locator("table tbody tr").count();
    }
}
