package vinscape.pages.shareholder;

import com.microsoft.playwright.Page;
import vinscape.pages.AppShellPage;

/**
 * Page Object cho /app/shareholder/vote (shareholder/vote.jsp)
 */
public class ShareholderVotePage extends AppShellPage {

    public ShareholderVotePage(Page page, String baseUrl) {
        super(page, baseUrl);
    }

    public String url() {
        return baseUrl + "/app/shareholder/vote";
    }

    public void navigate() {
        page.navigate(url());
        sidebar().waitFor();
    }

    public boolean hasOpenResolutionsToVote() {
        return page.locator("[name='resolutionId']").count() > 0;
    }

    public void vote(String resolutionId, String voteValue) {
        page.locator("[name='resolutionId'][value='" + resolutionId + "']")
                .locator("xpath=ancestor::form")
                .locator("[name='voteValue'][value='" + voteValue + "']")
                .check();
    }
}
