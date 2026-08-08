package vinscape.tests.shareholder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vinscape.base.BaseTest;
import vinscape.config.TestAccount;
import vinscape.pages.AppShellPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Test menu dieu huong (sidebar) danh cho role SHAREHOLDER.
 */
@DisplayName("Điều hướng - SHAREHOLDER")
class ShareholderNavigationTest extends BaseTest {

    private AppShellPage shell;

    @BeforeEach
    void setUp() {
        loginAs(TestAccount.SHAREHOLDER);
        shell = new AppShellPage(page, baseUrl);
    }

    @Test
    @DisplayName("Sidebar SHAREHOLDER hiển thị đầy đủ các mục menu")
    void shareholderSidebar_shouldShowAllMenuItems() {
        String[] expectedItems = {
                "Bảng Điều Khiển", "Thông báo", "Ký nhận Cổ phần", "Biểu quyết",
                "Chuyển nhượng", "Báo cáo Tài chính", "Tài liệu Hệ thống", "Tài khoản Cá nhân"
        };
        for (String item : expectedItems) {
            assertThat(shell.navItem(item)).isVisible();
        }
    }

    @Test
    @DisplayName("SHAREHOLDER KHÔNG thấy menu quản trị (Quản lý Cổ đông, Quản lý Người dùng...)")
    void shareholderSidebar_shouldNotShowAdminOrItMenus() {
        String[] restrictedItems = {"Quản lý Cổ đông", "Quản lý Người dùng", "Duyệt Chuyển nhượng", "Điều chỉnh Cổ phần"};
        for (String item : restrictedItems) {
            assertThat(page.locator(".nav-item", new com.microsoft.playwright.Page.LocatorOptions()
                    .setHasText(item))).hasCount(0);
        }
    }

    @Test
    @DisplayName("Click từng mục menu -> URL và trạng thái active khớp đúng")
    void clickingEachMenuItem_shouldNavigateCorrectly() {
        assertNavItemWorks("Thông báo", "/app/shareholder/notifications");
        assertNavItemWorks("Ký nhận Cổ phần", "/app/shareholder/sign");
        assertNavItemWorks("Biểu quyết", "/app/shareholder/vote");
        assertNavItemWorks("Chuyển nhượng", "/app/shareholder/transfer-request");
        assertNavItemWorks("Báo cáo Tài chính", "/app/shareholder/financial-reports");
        assertNavItemWorks("Tài liệu Hệ thống", "/app/shareholder/documents");
        assertNavItemWorks("Tài khoản Cá nhân", "/app/shareholder/profile");
        assertNavItemWorks("Bảng Điều Khiển", "/app/dashboard");
    }

    private void assertNavItemWorks(String label, String expectedPathSuffix) {
        shell.goToNav(label);
        assertThat(page).hasURL(baseUrl + expectedPathSuffix);
        assertThat(shell.navItem(label)).hasClass(java.util.regex.Pattern.compile(".*active.*"));
    }

    @Test
    @DisplayName("Topbar hiển thị đúng username 'user2' và role SHAREHOLDER")
    void topbar_shouldShowCorrectUsernameAndRole() {
        assertThat(shell.topbarUserLabel()).containsText("user2");
        assertThat(shell.topbarUserLabel()).containsText("SHAREHOLDER");
    }
}
