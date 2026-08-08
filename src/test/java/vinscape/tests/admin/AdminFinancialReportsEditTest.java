package vinscape.tests.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vinscape.base.BaseTest;
import vinscape.config.TestAccount;
import vinscape.pages.admin.AdminFinancialReportsPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test tinh nang SUA bao cao tai chinh (moi bo sung: truoc day chi co THEM, chua co SUA)
 * va bieu do tron "Co cau doanh thu theo quy" tren /app/admin/financial-reports/manage.
 *
 * LUU Y AN TOAN DU LIEU: test "sua thanh cong" chi thuc hien ROUND-TRIP - doc gia tri hien co
 * cua 1 dong, submit lai CHINH XAC cac gia tri do (khong doi gi ca). Cach nay xac nhan duoc
 * luong Sua hoat dong dung (khong loi, khong bao 403/500) ma khong lam sai lech du lieu that,
 * nen co the chay lai nhieu lan an toan.
 */
@DisplayName("ADMIN - Sửa Báo cáo Tài chính (chức năng mới)")
class AdminFinancialReportsEditTest extends BaseTest {

    private AdminFinancialReportsPage reportsPage;

    @BeforeEach
    void setUp() {
        loginAs(TestAccount.ADMIN);
        reportsPage = new AdminFinancialReportsPage(page, baseUrl);
        reportsPage.navigate();
    }

    @Test
    @DisplayName("Bảng danh sách có cột 'Thao tác' với nút Sửa cho mỗi báo cáo")
    void reportsTable_shouldHaveEditButtonPerRow() {
        int rowCount = reportsPage.reportRowCount();
        if (rowCount == 0) {
            return; // Chua co du lieu seed -> bo qua, khong co gi de test
        }
        assertEquals(rowCount, reportsPage.editButtons().count(),
                "So nut Sua phai bang so dong trong bang");
    }

    @Test
    @DisplayName("Click 'Sửa' -> form phía trên tự động điền đúng dữ liệu của dòng đó")
    void clickingEdit_shouldPrefillFormWithRowData() {
        if (reportsPage.reportRowCount() == 0) return;

        var firstEditBtn = reportsPage.editButtonForRow(0);
        String expectedYear = firstEditBtn.getAttribute("data-year");
        String expectedQuarter = firstEditBtn.getAttribute("data-quarter");
        String expectedId = firstEditBtn.getAttribute("data-id");

        reportsPage.clickEditOnFirstRow();

        assertThat(reportsPage.reportYearInput()).hasValue(expectedYear);
        assertThat(reportsPage.reportQuarterSelect()).hasValue(expectedQuarter);
        assertEquals(expectedId, reportsPage.reportIdHidden().inputValue());
    }

    @Test
    @DisplayName("Click 'Sửa' -> tiêu đề form đổi thành 'Sửa báo cáo Qx/yyyy' và hiện nút Hủy chỉnh sửa")
    void clickingEdit_shouldChangeFormTitleAndShowCancelLink() {
        if (reportsPage.reportRowCount() == 0) return;

        var firstEditBtn = reportsPage.editButtonForRow(0);
        String year = firstEditBtn.getAttribute("data-year");
        String quarter = firstEditBtn.getAttribute("data-quarter");

        reportsPage.clickEditOnFirstRow();

        assertThat(reportsPage.formTitle()).containsText("Sửa báo cáo Q" + quarter + "/" + year);
        assertThat(reportsPage.cancelEditLink()).isVisible();
        assertThat(reportsPage.formCard()).hasClass(java.util.regex.Pattern.compile(".*fr-editing.*"));
    }

    @Test
    @DisplayName("Bấm 'Hủy chỉnh sửa' -> form reset về chế độ Thêm mới, xóa reportId ẩn")
    void clickingCancelEdit_shouldResetFormToAddMode() {
        if (reportsPage.reportRowCount() == 0) return;

        reportsPage.clickEditOnFirstRow();
        assertThat(reportsPage.cancelEditLink()).isVisible();

        reportsPage.cancelEditLink().click();

        assertThat(reportsPage.formTitle()).containsText("Thêm báo cáo quý mới");
        assertEquals("", reportsPage.reportIdHidden().inputValue());
        assertThat(reportsPage.cancelEditLink()).not().isVisible();
    }

    @Test
    @DisplayName("Sửa và lưu lại ĐÚNG dữ liệu hiện có (round-trip) -> lưu thành công, không lỗi, không đổi số liệu")
    void editAndResubmitSameValues_shouldSucceedWithoutError() {
        if (reportsPage.reportRowCount() == 0) return;

        var firstEditBtn = reportsPage.editButtonForRow(0);
        String year = firstEditBtn.getAttribute("data-year");
        String quarter = firstEditBtn.getAttribute("data-quarter");
        String reportId = firstEditBtn.getAttribute("data-id");

        reportsPage.clickEditOnFirstRow();
        // Khong doi bat ky gia tri nao, submit lai y nguyen
        reportsPage.submitButton().click();

        page.waitForLoadState();
        assertThat(page).hasURL(reportsPage.url());
        assertThat(page.locator("body")).not().containsText("Exception");

        // Bao cao cho nam/quy do van con nguyen trong bang sau khi luu
        assertTrue(page.locator(".btn-edit-row[data-id='" + reportId + "'][data-year='" + year + "'][data-quarter='" + quarter + "']")
                .count() > 0, "Bao cao phai van con dung nam/quy sau khi round-trip sua");
    }

    @Test
    @DisplayName("Sửa báo cáo trùng năm/quý với báo cáo khác đang tồn tại -> báo lỗi, không lưu đè")
    void editingToConflictingYearQuarter_shouldShowValidationError() {
        int rowCount = reportsPage.reportRowCount();
        if (rowCount < 2) return; // Can it nhat 2 bao cao de tao xung dot

        var secondRowBtn = reportsPage.editButtonForRow(1);
        String otherYear = reportsPage.editButtonForRow(0).getAttribute("data-year");
        String otherQuarter = reportsPage.editButtonForRow(0).getAttribute("data-quarter");

        secondRowBtn.click();
        reportsPage.reportYearInput().fill(otherYear);
        reportsPage.reportQuarterSelect().selectOption(otherQuarter);
        reportsPage.submitButton().click();

        page.waitForTimeout(500);
        // Ky vong: co thong bao loi, KHONG bi redirect (vi doGet duoc goi lai kem error)
        assertThat(page.locator(".error-box, .alert-error")).isVisible();
    }

    @Test
    @DisplayName("Biểu đồ tròn 'Cơ cấu doanh thu theo quý' hiển thị với tab chọn năm")
    void pieChart_shouldDisplayWithYearTabs() {
        assertThat(reportsPage.pieCanvas()).isAttached();
        if (reportsPage.reportRowCount() > 0) {
            assertTrue(reportsPage.pieYearTabs().count() > 0, "Phai co it nhat 1 tab nam neu da co du lieu");
        }
    }

    @Test
    @DisplayName("Click qua các tab năm khác nhau trên biểu đồ tròn -> chú thích (legend) cập nhật, không lỗi")
    void clickingYearTabs_shouldUpdateLegendWithoutError() {
        int tabCount = reportsPage.pieYearTabs().count();
        if (tabCount < 2) return;

        for (int i = 0; i < tabCount; i++) {
            reportsPage.pieYearTabs().nth(i).click();
            page.waitForTimeout(150);
            assertThat(page.locator("body")).not().containsText("Exception");
        }
    }
}
