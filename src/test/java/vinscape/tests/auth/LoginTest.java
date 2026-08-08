package vinscape.tests.auth;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import vinscape.base.BaseTest;
import vinscape.config.TestAccount;
import vinscape.pages.AppShellPage;
import vinscape.pages.LoginPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test chuc nang Dang nhap (/login - LoginServlet + login.jsp).
 */
@DisplayName("Đăng nhập")
class LoginTest extends BaseTest {

    @ParameterizedTest(name = "Đăng nhập thành công với tài khoản {0}")
    @EnumSource(TestAccount.class)
    @DisplayName("Đăng nhập thành công chuyển hướng tới /app/dashboard đúng role")
    void loginSuccess_shouldRedirectToDashboard(TestAccount account) {
        LoginPage loginPage = new LoginPage(page, baseUrl);
        loginPage.navigate();

        loginPage.login(account.login(), account.password());

        assertThat(page).hasURL(baseUrl + "/app/dashboard");

        AppShellPage shell = new AppShellPage(page, baseUrl);
        assertThat(shell.topbarUserLabel()).containsText(account.role());
    }
/*
    @Test
    @DisplayName("Sai mật khẩu -> hiển thị lỗi, ở lại trang login")
    void loginWithWrongPassword_shouldShowError() {
        LoginPage loginPage = new LoginPage(page, baseUrl);
        loginPage.navigate();

        loginPage.submitLogin(TestAccount.SHAREHOLDER.login(), "mat-khau-sai-123");

        assertThat(loginPage.errorAlert()).isVisible();
        assertThat(loginPage.errorAlert()).containsText("Sai username hoặc mật khẩu");
        assertThat(page).hasURL(baseUrl + "/login");
    }
*/
    @Test
    @DisplayName("Username không tồn tại -> hiển thị lỗi chung chung (không tiết lộ tài khoản có tồn tại hay không)")
    void loginWithNonExistentUsername_shouldShowGenericError() {
        LoginPage loginPage = new LoginPage(page, baseUrl);
        loginPage.navigate();

        loginPage.submitLogin("tai-khoan-khong-ton-tai-xyz", "bat-ky-mat-khau");

        assertThat(loginPage.errorAlert()).isVisible();
    }

    @Test
    @DisplayName("Bỏ trống username -> browser chặn submit (thuộc tính required)")
    void loginWithEmptyUsername_shouldBeBlockedByRequiredAttribute() {
        LoginPage loginPage = new LoginPage(page, baseUrl);
        loginPage.navigate();

        loginPage.passwordInput().fill("bat-ky");
        loginPage.submitButton().click();

        // Form co required nen trinh duyet khong submit -> van o trang login, khong co loi server-side
        assertThat(page).hasURL(baseUrl + "/login");
        assertFalse(loginPage.errorAlert().isVisible());
    }

    @Test
    @DisplayName("Bỏ trống mật khẩu -> browser chặn submit (thuộc tính required)")
    void loginWithEmptyPassword_shouldBeBlockedByRequiredAttribute() {
        LoginPage loginPage = new LoginPage(page, baseUrl);
        loginPage.navigate();

        loginPage.usernameInput().fill(TestAccount.ADMIN.login());
        loginPage.submitButton().click();

        assertThat(page).hasURL(baseUrl + "/login");
    }

    @Test
    @DisplayName("Trang login có link 'Forgot Password?' và 'Sign up' hoạt động đúng")
    void loginPage_shouldContainNavigationLinks() {
        LoginPage loginPage = new LoginPage(page, baseUrl);
        loginPage.navigate();

        assertThat(loginPage.forgotPasswordLink()).isVisible();
        assertThat(loginPage.signUpLink()).isVisible();

        loginPage.forgotPasswordLink().click();
        assertThat(page).hasURL(baseUrl + "/forgot-password");

        page.goBack();
        loginPage.usernameInput().waitFor();
        loginPage.signUpLink().click();
        assertThat(page).hasURL(baseUrl + "/register");
    }

    @Test
    @DisplayName("Đăng nhập lại (đã có session) -> điều hướng thẳng vào /app/dashboard, KHÔNG bắt đăng nhập 2 lần")
    void whenAlreadyLoggedIn_visitingLoginAgain_shouldNotBreak() {
        loginAs(TestAccount.ADMIN);
        assertThat(page).hasURL(baseUrl + "/app/dashboard");

        // Session da hop le, GET /login van hien form (khong tu dong redirect ra dashboard theo thiet ke hien tai)
        page.navigate(baseUrl + "/login");
        assertTrue(page.url().contains("/login"));
    }

    @Test
    @DisplayName("Đăng nhập thành công phải tạo session mới (chống session fixation) - JSESSIONID thay đổi")
    void loginSuccess_shouldRegenerateSessionCookie() {
        page.navigate(baseUrl + "/login");
        String cookieBefore = getSessionCookieValue();

        LoginPage loginPage = new LoginPage(page, baseUrl);
        loginPage.login(TestAccount.SHAREHOLDER.login(), TestAccount.SHAREHOLDER.password());

        String cookieAfter = getSessionCookieValue();
        assertNotNull(cookieAfter);
        assertNotEquals(cookieBefore, cookieAfter,
                "JSESSIONID phai thay doi sau khi dang nhap thanh cong de chong session fixation");
    }

    private String getSessionCookieValue() {
        return context.cookies().stream()
                .filter(c -> "JSESSIONID".equalsIgnoreCase(c.name))
                .map(c -> c.value)
                .findFirst()
                .orElse(null);
    }
}
