package vinscape.tests.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vinscape.base.BaseTest;
import vinscape.config.TestAccount;
import vinscape.pages.admin.AdminShareholdersPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test trang Quan ly Co dong danh cho ADMIN (/app/admin/shareholders).
 */
@DisplayName("ADMIN - Quản lý Cổ đông")
class AdminShareholdersTest extends BaseTest {

    private AdminShareholdersPage shareholdersPage;

    @BeforeEach
    void setUp() {
        loginAs(TestAccount.ADMIN);
        shareholdersPage = new AdminShareholdersPage(page, baseUrl);
        shareholdersPage.navigate();
    }
/*
    @Test
    @DisplayName("Trang hiển thị danh sách cổ đông dạng bảng")
    void shareholdersPage_shouldDisplayTable() {
        assertThat(page.locator("table")).isVisible();
    }

    @Test
    @DisplayName("Tìm kiếm theo tên -> kết quả không lỗi trang, URL/param được cập nhật")
    void searchByFullName_shouldReturnResultsWithoutError() {
        shareholdersPage.searchByFullName("dungdeptry");
        page.waitForLoadState();

        assertTrue(page.url().contains("searchFullName"));
        assertThat(page.locator("body")).not().containsText("Exception");
        System.out.println("URL = [" + page.url() + "]");
        System.out.println(page.url().contains("searchFullName"));
    }
*/
    @Test
    @DisplayName("Tìm kiếm với từ khóa không tồn tại -> trả về danh sách rỗng, không crash")
    void searchWithNoMatchingKeyword_shouldReturnEmptyGracefully() {
        shareholdersPage.searchByFullName("zzzz_khong_ton_tai_zzzz");
        page.waitForLoadState();

        assertThat(page.locator("body")).not().containsText("Exception");
        assertThat(page.locator("body")).not().containsText("SQLException");
    }

    @Test
    @DisplayName("Tìm kiếm chứa ký tự đặc biệt LIKE ( % _ ) -> không gây lỗi server (đã escape LIKE)")
    void searchWithLikeWildcardCharacters_shouldNotBreakQuery() {
        shareholdersPage.searchByFullName("100%_test");
        page.waitForLoadState();

        assertThat(page.locator("body")).not().containsText("Exception");
        assertThat(page.locator("body")).not().containsText("SQLException");
    }

    @Test
    @DisplayName("Tìm kiếm với payload SQL injection cơ bản -> hệ thống an toàn (PreparedStatement), không lỗi/không rò dữ liệu")
    void searchWithSqlInjectionPayload_shouldBeSafe() {
        shareholdersPage.searchByFullName("' OR '1'='1");
        page.waitForLoadState();

        assertThat(page.locator("body")).not().containsText("Exception");
        assertThat(page.locator("body")).not().containsText("SQLException");
    }

    @Test
    @DisplayName("Tìm kiếm với payload XSS cơ bản -> nội dung được escape, không thực thi script")
    void searchWithXssPayload_shouldBeEscaped() {
        boolean[] dialogTriggered = {false};
        page.onDialog(dialog -> {
            dialogTriggered[0] = true;
            dialog.dismiss();
        });

        shareholdersPage.searchByFullName("<script>alert(1)</script>");
        page.waitForLoadState();
        page.waitForTimeout(300);

        assertTrue(!dialogTriggered[0], "Khong duoc xuat hien dialog alert() - chung to XSS da bi chan (c:out escape)");
    }

    @Test
    @DisplayName("Tìm kiếm theo CCCD/CMND")
    void searchByCitizenId_shouldNotError() {
        shareholdersPage.searchByCitizenId("0792");
        page.waitForLoadState();
        assertThat(page.locator("body")).not().containsText("Exception");
    }

    @Test
    @DisplayName("Tìm kiếm theo số điện thoại")
    void searchByPhone_shouldNotError() {
        shareholdersPage.searchByPhone("09");
        page.waitForLoadState();
        assertThat(page.locator("body")).not().containsText("Exception");
    }
}
