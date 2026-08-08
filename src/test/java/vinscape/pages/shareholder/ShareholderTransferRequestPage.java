package vinscape.pages.shareholder;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import vinscape.pages.AppShellPage;

/**
 * Page Object cho /app/shareholder/transfer-request (shareholder/transfer-request.jsp)
 */
public class ShareholderTransferRequestPage extends AppShellPage {

    public ShareholderTransferRequestPage(Page page, String baseUrl) {
        super(page, baseUrl);
    }

    public String url() {
        return baseUrl + "/app/shareholder/transfer-request";
    }

    public void navigate() {
        page.navigate(url());
        toShareholderIdInput().waitFor();
    }

    public Locator toShareholderIdInput() {
        return page.locator("#toShareholderId");
    }

    public Locator quantityInput() {
        return page.locator("#quantity");
    }

    public Locator submitButton() {
        return page.locator("button[type='submit']").first();
    }

    public Locator errorBox() {
        return page.locator(".error-box");
    }

    public Locator successBox() {
        return page.locator(".success-box");
    }

    public void submitTransferRequest(String toShareholderId, String quantity) {
        toShareholderIdInput().fill(toShareholderId);
        quantityInput().fill(quantity);
        submitButton().click();
    }
}
