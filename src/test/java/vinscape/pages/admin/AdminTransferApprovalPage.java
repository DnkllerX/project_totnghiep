package vinscape.pages.admin;

import com.microsoft.playwright.Page;
import vinscape.pages.AppShellPage;

/**
 * Page Object cho /app/admin/transfer-approval (admin/transfer-approval.jsp)
 */
public class AdminTransferApprovalPage extends AppShellPage {

    public AdminTransferApprovalPage(Page page, String baseUrl) {
        super(page, baseUrl);
    }

    public String url() {
        return baseUrl + "/app/admin/transfer-approval";
    }

    public void navigate() {
        page.navigate(url());
        sidebar().waitFor();
    }

    public int pendingRequestCount() {
        return page.locator("table tbody tr").count();
    }

    public boolean hasEmptyState() {
        return page.getByText("Không có").count() > 0
                || page.locator("table tbody tr").count() == 0;
    }
}
