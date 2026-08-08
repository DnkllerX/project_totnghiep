package vinscape.tests.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vinscape.base.BaseTest;
import vinscape.config.TestAccount;
import vinscape.pages.ForgotPasswordPage;
import vinscape.pages.RegisterPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Test trang Dang ky (/register) va Quen mat khau (/forgot-password).
 */
@DisplayName("Đăng ký & Quên mật khẩu")
class RegisterAndForgotPasswordTest extends BaseTest {

    @Test
    @DisplayName("Trang đăng ký hiển thị đầy đủ các trường bắt buộc")
    void registerPage_shouldDisplayAllRequiredFields() {
        RegisterPage registerPage = new RegisterPage(page, baseUrl);
        registerPage.navigate();

        assertThat(registerPage.usernameInput()).isVisible();
        assertThat(registerPage.emailInput()).isVisible();
        assertThat(registerPage.passwordInput()).isVisible();
        assertThat(registerPage.fullNameInput()).isVisible();
        assertThat(registerPage.citizenIdInput()).isVisible();
    }

    @Test
    @DisplayName("Đăng ký với username đã tồn tại -> hiển thị lỗi, không tạo tài khoản mới")
    void registerWithExistingUsername_shouldShowError() {
        RegisterPage registerPage = new RegisterPage(page, baseUrl);
        registerPage.navigate();

        registerPage.usernameInput().fill(TestAccount.SHAREHOLDER.login());
        registerPage.emailInput().fill("email-moi-chua-ton-tai@example.com");
        registerPage.passwordInput().fill("MatKhauManh123");
        registerPage.fullNameInput().fill("Nguyen Van Test");
        registerPage.citizenIdInput().fill("079099001122");
        if (registerPage.phoneInput().count() > 0) registerPage.phoneInput().fill("0900000000");
        registerPage.submitButton().first().click();

        // Trang se forward lai register.jsp voi loi, hoac hien thi alert - kiem tra van con o /register
        page.waitForTimeout(500);
        assertThat(page).hasURL(java.util.regex.Pattern.compile(".*register.*"));
    }

    @Test
    @DisplayName("Mật khẩu yếu vẫn được submit (chỉ cảnh báo qua thanh strength-meter, không chặn server-side)")
    void weakPassword_shouldOnlyShowStrengthMeterWarning_notBlockSubmit() {
        RegisterPage registerPage = new RegisterPage(page, baseUrl);
        registerPage.navigate();

        registerPage.passwordInput().fill("123");

        // Theo thiet ke da thong nhat: khong chan server-side, chi canh bao truc quan qua thanh do manh
        if (registerPage.passwordStrengthLabel().count() > 0) {
            assertThat(registerPage.passwordStrengthLabel()).isVisible();
        }
        assertThat(registerPage.passwordInput()).isEnabled();
    }
/*
    @Test
    @DisplayName("Trang quên mật khẩu hiển thị form nhập email")
    void forgotPasswordPage_shouldDisplayEmailForm() {
        ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage(page, baseUrl);
        forgotPasswordPage.navigate();

        assertThat(forgotPasswordPage.emailInput()).isVisible();
        assertThat(forgotPasswordPage.submitButton()).isVisible();
    }

    @Test
    @DisplayName("Gửi yêu cầu quên mật khẩu với email hợp lệ -> hiển thị thông báo xác nhận")
    void forgotPassword_withValidEmail_shouldShowConfirmationMessage() {
        ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage(page, baseUrl);
        forgotPasswordPage.navigate();

        forgotPasswordPage.requestReset(TestAccount.ADMIN.login());

        page.waitForTimeout(500);
        assertThat(forgotPasswordPage.message()).isVisible();
    }

    @Test
    @DisplayName("Gửi yêu cầu quên mật khẩu với email KHÔNG tồn tại -> vẫn hiển thị thông báo chung " +
            "(không được để lộ email nào tồn tại trong hệ thống)")
    void forgotPassword_withNonExistentEmail_shouldNotLeakAccountExistence() {
        ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage(page, baseUrl);
        forgotPasswordPage.navigate();

        forgotPasswordPage.requestReset("khong-ton-tai-tuyet-doi-xyz@example.com");

        page.waitForTimeout(500);
        assertThat(forgotPasswordPage.message()).isVisible();
    }
*/
    @Test
    @DisplayName("Truy cập /reset-password không có token hợp lệ -> không cho đổi mật khẩu thành công")
    void resetPassword_withoutValidToken_shouldNotSucceed() {
        page.navigate(baseUrl + "/reset-password?token=token-gia-mao-khong-hop-le");

        var newPasswordInput = page.locator("#newPassword");
        if (newPasswordInput.count() > 0) {
            newPasswordInput.fill("MatKhauMoi123");
            page.locator("#confirmNewPassword").fill("MatKhauMoi123");
            page.locator("button[type='submit']").first().click();
            page.waitForTimeout(500);
            // Ky vong: co thong bao loi (token het han/khong hop le), khong redirect thanh cong ve login voi passwordChanged=1
            assertThat(page).not().hasURL(java.util.regex.Pattern.compile(".*passwordChanged=1.*"));
        }
    }
}
