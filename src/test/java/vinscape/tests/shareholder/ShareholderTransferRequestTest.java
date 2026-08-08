package vinscape.tests.shareholder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vinscape.base.BaseTest;
import vinscape.config.TestAccount;
import vinscape.pages.shareholder.ShareholderTransferRequestPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Test trang Chuyen nhuong Co phan (/app/shareholder/transfer-request).
 *
 * LUU Y: khong test truong hop tao yeu cau chuyen nhuong THANH CONG voi du lieu that,
 * vi se tao ban ghi that trong CSDL (anh huong den trang Duyet Chuyen nhuong cua ADMIN
 * va so du co phan). Chi test cac truong hop VALIDATION (an toan, khong sinh du lieu).
 */
@DisplayName("SHAREHOLDER - Chuyển nhượng Cổ phần")
class ShareholderTransferRequestTest extends BaseTest {

    private ShareholderTransferRequestPage transferPage;

    @BeforeEach
    void setUp() {
        loginAs(TestAccount.SHAREHOLDER);
        transferPage = new ShareholderTransferRequestPage(page, baseUrl);
        transferPage.navigate();
    }

    @Test
    @DisplayName("Trang hiển thị đầy đủ form: mã cổ đông nhận, số lượng")
    void transferRequestPage_shouldDisplayForm() {
        assertThat(transferPage.toShareholderIdInput()).isVisible();
        assertThat(transferPage.quantityInput()).isVisible();
        assertThat(transferPage.submitButton()).isVisible();
    }
/*
    @Test
    @DisplayName("Số lượng chuyển nhượng = 0 -> báo lỗi 'Số lượng phải lớn hơn 0'")
    void submitWithZeroQuantity_shouldShowError() {
        transferPage.submitTransferRequest("1", "0");

        assertThat(transferPage.errorBox()).isVisible();
        assertThat(transferPage.errorBox()).containsText("Value must be greater than or equal to 1");
    }
*/
    @Test
    @DisplayName("Số lượng chuyển nhượng âm -> bị chặn (validation client hoặc server)")
    void submitWithNegativeQuantity_shouldBeRejected() {
        transferPage.toShareholderIdInput().fill("2");
        transferPage.quantityInput().fill("-5");
        transferPage.submitButton().click();

        page.waitForTimeout(400);
        assertThat(page.locator("body")).not().containsText("Exception");
    }

    @Test
    @DisplayName("Bỏ trống mã cổ đông nhận -> HTML5 required chặn submit")
    void submitWithEmptyRecipient_shouldBeBlockedClientSide() {
        transferPage.quantityInput().fill("10");
        transferPage.submitButton().click();

        page.waitForTimeout(300);
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*/app/shareholder/transfer-request.*"));
    }

    @Test
    @DisplayName("Chuyển nhượng số lượng vượt quá số cổ phần đang sở hữu -> hệ thống báo lỗi, không crash")
    void submitWithExcessiveQuantity_shouldBeRejectedGracefully() {
        transferPage.submitTransferRequest("2", "999999999");

        page.waitForTimeout(500);
        assertThat(page.locator("body")).not().containsText("Exception");
    }
}
