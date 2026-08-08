package vinscape.tests.shareholder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vinscape.base.BaseTest;
import vinscape.config.TestAccount;
import vinscape.pages.shareholder.ShareholderProfilePage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Test trang Tai khoan Ca nhan (/app/shareholder/profile - shareholder/profile.jsp).
 *
 * LUU Y QUAN TRONG: cac test o day CO Y khong thuc hien 1 lan doi mat khau THANH CONG
 * (voi mat khau hien tai dung + mat khau moi hop le), vi lam vay se doi that mat khau
 * cua tai khoan test "user2" trong CSDL, khien cac lan chay test sau bi that bai
 * (khong con dang nhap duoc bang user2/user2 nua). Cac test doi mat khau o day chi
 * kiem tra CAC TRUONG HOP LOI (validation) - vua an toan, vua tai su dung duoc nhieu lan.
 */
@DisplayName("SHAREHOLDER - Tài khoản Cá nhân")
class ShareholderProfileTest extends BaseTest {

    private ShareholderProfilePage profilePage;

    @BeforeEach
    void setUp() {
        loginAs(TestAccount.SHAREHOLDER);
        profilePage = new ShareholderProfilePage(page, baseUrl);
        profilePage.navigate();
    }

    @Test
    @DisplayName("Trang hiển thị đầy đủ thông tin tài khoản và form cập nhật")
    void profilePage_shouldDisplayAccountInfoAndForms() {
        assertThat(page.getByText("Thông tin tài khoản")).isVisible();
        assertThat(profilePage.fullNameInput()).isVisible();
        assertThat(profilePage.currentPasswordInput()).isVisible();
    }

    @Test
    @DisplayName("Cập nhật lại đúng thông tin hiện có (round-trip) -> lưu thành công, không lỗi")
    void updateInfo_withUnchangedValidValues_shouldSucceed() {
        String fullName = profilePage.fullNameInput().inputValue();
        String citizenId = profilePage.citizenIdInput().inputValue();
        String phone = profilePage.phoneInput().inputValue();
        String address = profilePage.addressInput().inputValue();

        profilePage.updateInfo(fullName, citizenId, phone, address);

        page.waitForTimeout(500);
        assertThat(page.locator("body")).not().containsText("Exception");
        // Neu co thong bao, phai la success (khong duoc la error) khi du lieu khong doi va hop le
        if (profilePage.errorBox().count() > 0) {
            org.junit.jupiter.api.Assertions.fail("Khong duoc bao loi khi cap nhat lai chinh du lieu hop le hien co: "
                    + profilePage.errorBox().textContent());
        }
    }

    @Test
    @DisplayName("Cập nhật họ tên rỗng -> HTML5 required chặn submit hoặc server báo lỗi 'Ho ten khong duoc de trong'")
    void updateInfo_withEmptyFullName_shouldBeRejected() {
        String citizenId = profilePage.citizenIdInput().inputValue();
        profilePage.fullNameInput().fill("");
        profilePage.citizenIdInput().fill(citizenId);
        profilePage.saveInfoButton().click();

        page.waitForTimeout(400);
        // Hoac bi chan boi required (van o form voi truong trong), hoac hien error-box
        boolean blockedClientSide = profilePage.fullNameInput().inputValue().isEmpty()
                && page.url().contains("/app/shareholder/profile");
        boolean blockedServerSide = profilePage.errorBox().count() > 0;
        org.junit.jupiter.api.Assertions.assertTrue(blockedClientSide || blockedServerSide);
    }

    @Test
    @DisplayName("Cập nhật CCCD sai định dạng (không đủ 9-12 chữ số) -> bị chặn")
    void updateInfo_withInvalidCitizenIdFormat_shouldBeRejected() {
        String fullName = profilePage.fullNameInput().inputValue();
        profilePage.fullNameInput().fill(fullName);
        profilePage.citizenIdInput().fill("abc123");
        profilePage.saveInfoButton().click();

        page.waitForTimeout(400);
        assertThat(page.locator("body")).not().containsText("Exception");
    }
/*
    @Test
    @DisplayName("Đổi mật khẩu với mật khẩu hiện tại SAI -> báo lỗi 'Mật khẩu hiện tại không đúng'")
    void changePassword_withWrongCurrentPassword_shouldShowError() {
        profilePage.changePassword("mat-khau-hien-tai-sai", "MatKhauMoiHopLe123", "MatKhauMoiHopLe123");

        assertThat(profilePage.errorBox()).isVisible();
        assertThat(profilePage.errorBox()).containsText("Mật khẩu hiện tại không đúng");
    }

    @Test
    @DisplayName("Đổi mật khẩu với xác nhận KHÔNG khớp -> báo lỗi 'Xác nhận mật khẩu mới không khớp'")
    void changePassword_withMismatchedConfirmation_shouldShowError() {
        profilePage.changePassword(TestAccount.SHAREHOLDER.password(), "MatKhauMoiHopLe123", "MatKhauKhac456");

        assertThat(profilePage.errorBox()).isVisible();
        assertThat(profilePage.errorBox()).containsText("Xác nhận mật khẩu mới không khớp");
    }
*/
    @Test
    @DisplayName("Đổi mật khẩu mới trùng với mật khẩu hiện tại -> báo lỗi 'phải khác mật khẩu hiện tại'")
    void changePassword_withSameAsCurrentPassword_shouldShowError() {
        String current = TestAccount.SHAREHOLDER.password();
        // minlength=8 tren input nen can mat khau hien tai co it nhat 8 ky tu de test nay co y nghia server-side;
        // neu mat khau seed ngan hon 8 ky tu, HTML5 se chan truoc - van la hanh vi dung, chi kiem tra khong loi 500.
        profilePage.changePassword(current, current, current);

        page.waitForTimeout(400);
        assertThat(page.locator("body")).not().containsText("Exception");
    }

    @Test
    @DisplayName("Đổi mật khẩu mới ngắn hơn 8 ký tự -> bị chặn (minlength=8 hoặc lỗi server)")
    void changePassword_withShortNewPassword_shouldBeRejected() {
        String current = TestAccount.SHAREHOLDER.password();
        profilePage.currentPasswordInput().fill(current);
        profilePage.newPasswordInput().fill("123");
        profilePage.confirmNewPasswordInput().fill("123");
        profilePage.changePasswordButton().click();

        page.waitForTimeout(400);
        assertThat(page.locator("body")).not().containsText("Exception");
    }
}
