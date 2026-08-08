package vinscape.tests.shareholder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vinscape.base.BaseTest;
import vinscape.config.TestAccount;
import vinscape.pages.shareholder.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Test smoke (tai trang thanh cong, khong loi) cho cac trang con lai cua SHAREHOLDER.
 */
@DisplayName("SHAREHOLDER - Các trang chức năng")
class ShareholderFeaturePagesTest extends BaseTest {

    @BeforeEach
    void setUp() {
        loginAs(TestAccount.SHAREHOLDER);
    }

    @Test
    @DisplayName("Trang Thông báo tải thành công, không lỗi")
    void notificationsPage_shouldLoadSuccessfully() {
        ShareholderNotificationsPage p = new ShareholderNotificationsPage(page, baseUrl);
        p.navigate();

        assertThat(page.locator("body")).not().containsText("Exception");
        assertThat(p.sidebar()).isVisible();
    }

    @Test
    @DisplayName("Trang Ký nhận Cổ phần tải thành công (hiển thị canvas ký tên nếu có đợt phát hành chờ ký)")
    void signPage_shouldLoadSuccessfully() {
        ShareholderSignPage p = new ShareholderSignPage(page, baseUrl);
        p.navigate();

        assertThat(page.locator("body")).not().containsText("Exception");
        assertThat(p.sidebar()).isVisible();
    }

    @Test
    @DisplayName("Trang Biểu quyết tải thành công, không lỗi")
    void votePage_shouldLoadSuccessfully() {
        ShareholderVotePage p = new ShareholderVotePage(page, baseUrl);
        p.navigate();

        assertThat(page.locator("body")).not().containsText("Exception");
        assertThat(p.sidebar()).isVisible();
    }

    @Test
    @DisplayName("Trang Tài liệu Hệ thống (SHAREHOLDER) hiển thị danh sách tài liệu, chỉ xem không upload được")
    void documentsPage_shouldDisplayListOnly() {
        ShareholderDocumentsPage p = new ShareholderDocumentsPage(page, baseUrl);
        p.navigate();

        assertThat(page.locator("body")).not().containsText("Exception");
        // Khong duoc co input[type=file] o trang cua co dong (chi ADMIN moi upload duoc)
        org.junit.jupiter.api.Assertions.assertEquals(0, page.locator("input[type='file']").count());
    }

    @Test
    @DisplayName("Trang Báo cáo Tài chính (SHAREHOLDER) hiển thị biểu đồ, không có form nhập liệu (chỉ xem)")
    void financialReportsPage_shouldDisplayChartOnly() {
        ShareholderFinancialReportsPage p = new ShareholderFinancialReportsPage(page, baseUrl);
        p.navigate();

        assertThat(page.locator("body")).not().containsText("Exception");
        assertThat(p.chartCanvas()).isAttached();
        // Khong duoc co form nhap revenue/profit o trang cua co dong
        org.junit.jupiter.api.Assertions.assertEquals(0, page.locator("[name='revenue']").count());
    }

    @Test
    @DisplayName("Trang Bảng Điều Khiển (Dashboard) của cổ đông hiển thị đúng, không lỗi 500")
    void dashboardPage_shouldLoadSuccessfully() {
        page.navigate(baseUrl + "/app/dashboard");

        assertThat(page.locator("body")).not().containsText("Exception");
        assertThat(page.locator(".app-sidebar")).isVisible();
    }
}
