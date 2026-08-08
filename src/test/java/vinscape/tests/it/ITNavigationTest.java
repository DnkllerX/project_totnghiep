package vinscape.tests.it;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vinscape.base.BaseTest;
import vinscape.config.TestAccount;
import vinscape.pages.AppShellPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Test menu dieu huong (sidebar) danh cho role IT.
 */
@DisplayName("Điều hướng - IT")
class ITNavigationTest extends BaseTest {

    private AppShellPage shell;

    @BeforeEach
    void setUp() {
        loginAs(TestAccount.IT);
        shell = new AppShellPage(page, baseUrl);
    }

    @Test
    @DisplayName("Sidebar IT hiển thị đúng 3 mục: Bảng Điều Khiển, Quản lý Người dùng, Tài liệu Hệ thống")
    void itSidebar_shouldShowCorrectMenuItems() {
        assertThat(shell.navItem("Bảng Điều Khiển")).isVisible();
        assertThat(shell.navItem("Quản lý Người dùng")).isVisible();
        assertThat(shell.navItem("Tài liệu Hệ thống")).isVisible();
    }

    @Test
    @DisplayName("IT KHÔNG thấy các menu quản trị cổ đông của ADMIN (Quản lý Cổ đông, Phát hành & Cổ tức...)")
    void itSidebar_shouldNotShowAdminOnlyMenus() {
        String[] adminOnlyItems = {
                "Quản lý Cổ đông", "Duyệt Chuyển nhượng", "Phát hành & Cổ tức",
                "Nghị quyết Biểu quyết", "Điều chỉnh Cổ phần", "Báo cáo Tài chính"
        };
        for (String item : adminOnlyItems) {
            assertThat(page.locator(".nav-item", new com.microsoft.playwright.Page.LocatorOptions()
                    .setHasText(item))).hasCount(0);
        }
    }

    @Test
    @DisplayName("Click menu Quản lý Người dùng -> chuyển đúng URL /app/it/user-management")
    void clickingUserManagement_shouldNavigateCorrectly() {
        shell.goToNav("Quản lý Người dùng");
        assertThat(page).hasURL(baseUrl + "/app/it/user-management");
        assertThat(shell.navItem("Quản lý Người dùng")).hasClass(java.util.regex.Pattern.compile(".*active.*"));
    }

    @Test
    @DisplayName("Topbar hiển thị đúng role IT")
    void topbar_shouldShowItRole() {
        assertThat(shell.topbarUserLabel()).containsText("IT");
    }
}
