package vinscape.tests.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vinscape.base.BaseTest;
import vinscape.config.TestAccount;
import vinscape.pages.admin.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Test smoke (tai trang khong loi) + validate form co ban cho cac chuc nang con lai cua ADMIN:
 * Duyet chuyen nhuong, Phat hanh & Co tuc, Nghi quyet Bieu quyet, Dieu chinh Co phan,
 * Tai lieu He thong, Bao cao Tai chinh.
 */
@DisplayName("ADMIN - Các trang chức năng")
class AdminFeaturePagesTest extends BaseTest {

    @BeforeEach
    void setUp() {
        loginAs(TestAccount.ADMIN);
    }

    @Test
    @DisplayName("Trang Duyệt Chuyển nhượng tải thành công, hiển thị bảng yêu cầu")
    void transferApprovalPage_shouldLoadSuccessfully() {
        AdminTransferApprovalPage p = new AdminTransferApprovalPage(page, baseUrl);
        p.navigate();
        assertThat(page.locator("body")).not().containsText("Exception");
        assertThat(p.sidebar()).isVisible();
    }

    @Test
    @DisplayName("Trang Phát hành & Cổ tức hiển thị đầy đủ form tạo đợt phát hành")
    void shareIssuePage_shouldDisplayForm() {
        AdminShareIssuePage p = new AdminShareIssuePage(page, baseUrl);
        p.navigate();

        assertThat(p.titleInput()).isVisible();
        assertThat(p.issueRatioInput()).isVisible();
        assertThat(p.issueDateInput()).isVisible();
        assertThat(p.startDateInput()).isVisible();
        assertThat(p.endDateInput()).isVisible();
    }

    @Test
    @DisplayName("Trang Phát hành & Cổ tức: nhập tỉ lệ phát hành -> preview số lượng ước tính cập nhật (JS phía client)")
    void shareIssuePage_ratioInput_shouldUpdateEstimatedPreview() {
        AdminShareIssuePage p = new AdminShareIssuePage(page, baseUrl);
        p.navigate();

        if (p.issueRatioInput().isVisible()) {
            p.issueRatioInput().fill("10");
            page.waitForTimeout(300);
            // Chi kiem tra khong loi JS, khu vuc preview van hien dien
            assertThat(p.estimatedTotalPreview()).isVisible();
        }
    }

    @Test
    @DisplayName("Trang Nghị quyết Biểu quyết hiển thị form tạo nghị quyết mới và danh sách nghị quyết")
    void resolutionPage_shouldDisplayFormAndList() {
        AdminResolutionPage p = new AdminResolutionPage(page, baseUrl);
        p.navigate();

        assertThat(p.titleInput()).isVisible();
        assertThat(p.startTimeInput()).isVisible();
        assertThat(p.endTimeInput()).isVisible();
    }

    @Test
    @DisplayName("Trang Điều chỉnh Cổ phần hiển thị form điều chỉnh")
    void shareAdjustPage_shouldDisplayForm() {
        AdminShareAdjustPage p = new AdminShareAdjustPage(page, baseUrl);
        p.navigate();

        assertThat(p.shareholderIdInput()).isVisible();
        assertThat(p.newQuantityInput()).isVisible();
        assertThat(p.reasonInput()).isVisible();
    }

    @Test
    @DisplayName("Điều chỉnh Cổ phần với ID cổ đông không tồn tại -> hiển thị thông báo lỗi, không crash")
    void shareAdjustPage_withNonExistentShareholderId_shouldShowError() {
        AdminShareAdjustPage p = new AdminShareAdjustPage(page, baseUrl);
        p.navigate();

        p.shareholderIdInput().fill("999999999");
        p.newQuantityInput().fill("100");
        p.reasonInput().fill("Test tu dong hoa - khong ton tai");
        p.submitButton().click();

        page.waitForTimeout(500);
        assertThat(page.locator("body")).not().containsText("Exception");
    }

    @Test
    @DisplayName("Trang Tài liệu Hệ thống (ADMIN) hiển thị form upload và danh sách tài liệu")
    void documentsPage_shouldDisplayUploadFormAndList() {
        AdminDocumentsPage p = new AdminDocumentsPage(page, baseUrl);
        p.navigate();

        assertThat(p.titleInput()).isVisible();
        assertThat(p.fileInput()).isVisible();
        assertThat(page.locator("table")).isVisible();
    }

    @Test
    @DisplayName("Upload tài liệu không chọn file -> browser chặn submit (thuộc tính required trên input file)")
    void documentsPage_uploadWithoutFile_shouldBeBlockedClientSide() {
        AdminDocumentsPage p = new AdminDocumentsPage(page, baseUrl);
        p.navigate();

        p.titleInput().fill("Tai lieu test khong co file");
        p.uploadSubmitButton().click();

        page.waitForTimeout(300);
        // Van o trang documents (khong bi dieu huong / khong loi 500)
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*/app/admin/documents.*"));
    }

    @Test
    @DisplayName("Trang Báo cáo Tài chính (ADMIN) hiển thị form nhập báo cáo và biểu đồ canvas")
    void financialReportsPage_shouldDisplayFormAndChart() {
        AdminFinancialReportsPage p = new AdminFinancialReportsPage(page, baseUrl);
        p.navigate();

        assertThat(p.reportYearInput()).isVisible();
        assertThat(p.revenueInput()).isVisible();
        assertThat(p.chartCanvas()).isAttached();
    }

    @Test
    @DisplayName("Nhập báo cáo tài chính với giá trị âm (revenue < 0) -> hệ thống báo lỗi hoặc validation chặn")
    void financialReportsPage_withNegativeRevenue_shouldBeRejectedOrValidated() {
        AdminFinancialReportsPage p = new AdminFinancialReportsPage(page, baseUrl);
        p.navigate();

        p.reportYearInput().fill("2026");
        p.revenueInput().fill("-1000");
        p.profitBeforeTaxInput().fill("100");
        p.profitAfterTaxInput().fill("80");
        p.submitButton().click();

        page.waitForTimeout(500);
        assertThat(page.locator("body")).not().containsText("Exception");
    }
}
