package vinscape.pages.admin;

import com.microsoft.playwright.Page;
import vinscape.pages.AppShellPage;

/**
 * Page Object cho /app/admin/shareholders (admin/shareholders.jsp)
 */
public class AdminShareholdersPage extends AppShellPage {

    public AdminShareholdersPage(Page page, String baseUrl) {
        super(page, baseUrl);
    }

    public String url() {
        return baseUrl + "/app/admin/shareholders";
    }

    public void navigate() {
        page.navigate(url());
        page.waitForSelector("[name='searchFullName']");
    }

    public void searchByFullName(String name) {
        page.locator("[name='searchFullName']").fill(name);
        page.keyboard().press("Enter");
    }

    public void searchByCitizenId(String citizenId) {
        page.locator("[name='searchCitizenId']").fill(citizenId);
        page.keyboard().press("Enter");
    }

    public void searchByPhone(String phone) {
        page.locator("[name='searchPhone']").fill(phone);
        page.keyboard().press("Enter");
    }

    public void searchById(String shareholderId) {
        page.locator("[name='searchId']").fill(shareholderId);
        page.keyboard().press("Enter");
    }

    public int resultRowCount() {
        return page.locator("table tbody tr").count();
    }

    public boolean hasErrorOrSuccessBox() {
        return page.locator(".error-box, .success-box").count() > 0;
    }
}
