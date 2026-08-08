package vinscape.pages.shareholder;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import vinscape.pages.AppShellPage;

/**
 * Page Object cho /app/shareholder/financial-reports (shareholder/financial-reports.jsp)
 */
public class ShareholderFinancialReportsPage extends AppShellPage {

    public ShareholderFinancialReportsPage(Page page, String baseUrl) {
        super(page, baseUrl);
    }

    public String url() {
        return baseUrl + "/app/shareholder/financial-reports";
    }

    public void navigate() {
        page.navigate(url());
        sidebar().waitFor();
    }

    public Locator chartCanvas() {
        return page.locator("#financialChart");
    }
}
