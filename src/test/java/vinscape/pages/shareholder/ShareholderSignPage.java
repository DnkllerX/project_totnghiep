package vinscape.pages.shareholder;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import vinscape.pages.AppShellPage;

/**
 * Page Object cho /app/shareholder/sign (shareholder/sign.jsp)
 * Cho phep co dong ky nhan (draw chu ky tren canvas) cac dot phat hanh co phan.
 */
public class ShareholderSignPage extends AppShellPage {

    public ShareholderSignPage(Page page, String baseUrl) {
        super(page, baseUrl);
    }

    public String url() {
        return baseUrl + "/app/shareholder/sign";
    }

    public void navigate() {
        page.navigate(url());
        sidebar().waitFor();
    }

    public Locator signModal() {
        return page.locator("#signModal");
    }

    public Locator signatureCanvas() {
        return page.locator("#signatureCanvas");
    }

    public Locator signForm() {
        return page.locator("#signForm");
    }

    public boolean hasPendingIssuesToSign() {
        return page.locator("button", new Page.LocatorOptions().setHasText("Ký")).count() > 0;
    }
}
