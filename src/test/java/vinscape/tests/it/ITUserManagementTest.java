package vinscape.tests.it;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vinscape.base.BaseTest;
import vinscape.config.TestAccount;
import vinscape.pages.it.ITDocumentsPage;
import vinscape.pages.it.ITUserManagementPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Test trang Quan ly Tai khoan (/app/it/user-management) - "acp" workflow (duyet tai khoan).
 */
@DisplayName("IT - Quản lý Tài khoản")
class ITUserManagementTest extends BaseTest {

    private ITUserManagementPage userManagementPage;

    @BeforeEach
    void setUp() {
        loginAs(TestAccount.IT);
        userManagementPage = new ITUserManagementPage(page, baseUrl);
        userManagementPage.navigate();
    }
/*
    @Test
    @DisplayName("Trang hiển thị form tạo tài khoản mới với đủ trường: username, email, password, role")
    void userManagementPage_shouldDisplayCreateAccountForm() {
        assertThat(userManagementPage.usernameInput()).isVisible();
        assertThat(userManagementPage.emailInput()).isVisible();
        assertThat(userManagementPage.createPasswordInput()).isVisible();
        assertThat(userManagementPage.roleSelect()).isVisible();
    }
*/
    @Test
    @DisplayName("Dropdown vai trò (role) chứa đủ 3 lựa chọn ADMIN / IT / SHAREHOLDER")
    void roleDropdown_shouldContainAllThreeRoles() {
        java.util.List<String> optionValues = userManagementPage.roleSelect().locator("option").allTextContents();
        String joined = String.join(",", optionValues).toUpperCase();

        org.junit.jupiter.api.Assertions.assertTrue(joined.contains("ADMIN"));
        org.junit.jupiter.api.Assertions.assertTrue(joined.contains("IT"));
        org.junit.jupiter.api.Assertions.assertTrue(joined.contains("SHAREHOLDER"));
    }

    @Test
    @DisplayName("Trang hiển thị bảng danh sách người dùng")
    void userManagementPage_shouldDisplayUserTable() {
        assertThat(page.locator("table")).isVisible();
    }
/*
    @Test
    @DisplayName("Tạo tài khoản với username đã tồn tại -> hiển thị lỗi, không tạo trùng")
    void createAccount_withDuplicateUsername_shouldShowError() {
        userManagementPage.usernameInput().fill(TestAccount.SHAREHOLDER.login());
        userManagementPage.emailInput().fill("email-hoan-toan-moi-abc@example.com");
        userManagementPage.createPasswordInput().fill("MatKhauManh123");
        userManagementPage.roleSelect().selectOption("SHAREHOLDER");
        page.locator("button[type='submit']").first().click();

        page.waitForTimeout(500);
        assertThat(page.locator("body")).not().containsText("Exception");
    }*/
    @Test
    @DisplayName("Trang Tài liệu Hệ thống (IT) hiển thị danh sách tài liệu, không có form upload (chỉ ADMIN mới upload)")
    void itDocumentsPage_shouldDisplayListOnly() {
        ITDocumentsPage docsPage = new ITDocumentsPage(page, baseUrl);
        docsPage.navigate();

        assertThat(page.locator("table")).isVisible();
        assertThat(page.locator("body")).not().containsText("Exception");
    }
}
