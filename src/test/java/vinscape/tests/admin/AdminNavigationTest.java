package vinscape.tests.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vinscape.base.BaseTest;
import vinscape.config.TestAccount;
import vinscape.pages.AppShellPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Test menu dieu huong (sidebar) danh cho role ADMIN - dua theo header.jsp.
 */
@DisplayName("Điều hướng - ADMIN")
class AdminNavigationTest extends BaseTest {

    private AppShellPage shell;

    @BeforeEach
    void setUp() {
        loginAs(TestAccount.ADMIN);
        shell = new AppShellPage(page, baseUrl);
    }

    @Test
    @DisplayName("Sidebar ADMIN hiển thị đầy đủ 7 mục menu theo đúng phân quyền")
    void adminSidebar_shouldShowAllAdminMenuItems() {
        String[] expectedItems = {
                "Bảng Điều Khiển", "Quản lý Cổ đông", "Duyệt Chuyển nhượng",
                "Phát hành & Cổ tức", "Nghị quyết Biểu quyết", "Điều chỉnh Cổ phần",
                "Tài liệu Hệ thống", "Báo cáo Tài chính"
        };
        for (String item : expectedItems) {
            assertThat(shell.navItem(item)).isVisible();
        }
    }

    @Test
    @DisplayName("ADMIN KHÔNG thấy menu dành riêng cho IT (Quản lý Người dùng)")
    void adminSidebar_shouldNotShowItOnlyMenu() {
        assertThat(page.locator(".nav-item", new com.microsoft.playwright.Page.LocatorOptions()
                .setHasText("Quản lý Người dùng"))).hasCount(0);
    }

    @Test
    @DisplayName("Click từng mục menu -> URL và trạng thái 'active' của menu phải khớp")
    void clickingEachMenuItem_shouldNavigateAndHighlightActive() {
        assertNavItemWorks("Quản lý Cổ đông", "/app/admin/shareholders");
        assertNavItemWorks("Duyệt Chuyển nhượng", "/app/admin/transfer-approval");
        assertNavItemWorks("Phát hành & Cổ tức", "/app/admin/share-issue");
        assertNavItemWorks("Nghị quyết Biểu quyết", "/app/admin/resolution");
        assertNavItemWorks("Điều chỉnh Cổ phần", "/app/admin/share-adjust");
        assertNavItemWorks("Tài liệu Hệ thống", "/app/admin/documents");
        assertNavItemWorks("Báo cáo Tài chính", "/app/admin/financial-reports/manage");
        assertNavItemWorks("Bảng Điều Khiển", "/app/dashboard");
    }

    private void assertNavItemWorks(String label, String expectedPathSuffix) {
        shell.goToNav(label);
        assertThat(page).hasURL(baseUrl + expectedPathSuffix);
        assertThat(shell.navItem(label)).hasClass(java.util.regex.Pattern.compile(".*active.*"));
    }
/*
    @Test
    @DisplayName("Topbar hiển thị đúng username và role ADMIN")
    void topbar_shouldShowCorrectUsernameAndRole() {
        assertThat(shell.topbarUserLabel()).containsText("ADMIN");
        assertThat(shell.logoutButton()).isVisible();
    }
*/
}
