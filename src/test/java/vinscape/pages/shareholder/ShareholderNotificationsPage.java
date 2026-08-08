package vinscape.pages.shareholder;

import com.microsoft.playwright.Page;
import vinscape.pages.AppShellPage;

/**
 * Page Object cho /app/shareholder/notifications (shareholder/notifications.jsp)
 */
public class ShareholderNotificationsPage extends AppShellPage {

    public ShareholderNotificationsPage(Page page, String baseUrl) {
        super(page, baseUrl);
    }

    public String url() {
        return baseUrl + "/app/shareholder/notifications";
    }

    public void navigate() {
        page.navigate(url());
        sidebar().waitFor();
    }

    public int notificationCount() {
        return page.locator("table tbody tr, .notification-item").count();
    }
}
