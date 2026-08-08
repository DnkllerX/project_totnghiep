package vinscape.tests.auth;

import com.microsoft.playwright.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import vinscape.base.BaseTest;
import vinscape.config.TestAccount;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test kiem soat truy cap theo role, dua tren bang ROLE_RULES trong AuthFilter.java:
 *  - Chua dang nhap  -> redirect ve /login
 *  - Dang nhap nhung SAI role duoc phep -> HTTP 403 Forbidden
 *  - Dang nhap DUNG role -> truy cap thanh cong (HTTP 200)
 */
@DisplayName("Kiểm soát truy cập theo phân quyền (AuthFilter)")
class AccessControlTest extends BaseTest {

    @ParameterizedTest(name = "Chưa đăng nhập truy cập {0} -> bị đá về /login")
    @ValueSource(strings = {
            "/app/dashboard",
            "/app/admin/shareholders",
            "/app/it/user-management",
            "/app/shareholder/profile"
    })
    void unauthenticatedAccess_shouldRedirectToLogin(String path) {
        page.navigate(baseUrl + path);
        assertThat(page).hasURL(baseUrl + "/login");
    }

    @Test
    @DisplayName("SHAREHOLDER truy cập trang chỉ dành cho ADMIN -> HTTP 403 Forbidden")
    void shareholderAccessingAdminOnlyPage_shouldReturn403() {
        loginAs(TestAccount.SHAREHOLDER);

        Response response = page.navigate(baseUrl + "/app/admin/shareholders");

        assertEquals(403, response.status());
    }

    @Test
    @DisplayName("SHAREHOLDER truy cập trang chỉ dành cho IT -> HTTP 403 Forbidden")
    void shareholderAccessingItOnlyPage_shouldReturn403() {
        loginAs(TestAccount.SHAREHOLDER);

        Response response = page.navigate(baseUrl + "/app/it/user-management");

        assertEquals(403, response.status());
    }

    @Test
    @DisplayName("ADMIN truy cập trang chỉ dành cho SHAREHOLDER -> HTTP 403 Forbidden")
    void adminAccessingShareholderOnlyPage_shouldReturn403() {
        loginAs(TestAccount.ADMIN);

        Response response = page.navigate(baseUrl + "/app/shareholder/vote");

        assertEquals(403, response.status());
    }

    @Test
    @DisplayName("ADMIN truy cập trang chỉ dành cho IT -> HTTP 403 Forbidden")
    void adminAccessingItOnlyPage_shouldReturn403() {
        loginAs(TestAccount.ADMIN);

        Response response = page.navigate(baseUrl + "/app/it/user-management");

        assertEquals(403, response.status());
    }

    @Test
    @DisplayName("IT truy cập trang chỉ dành cho ADMIN (share-adjust) -> HTTP 403 Forbidden")
    void itAccessingAdminOnlyPage_shouldReturn403() {
        loginAs(TestAccount.IT);

        Response response = page.navigate(baseUrl + "/app/admin/share-adjust");

        assertEquals(403, response.status());
    }

    @Test
    @DisplayName("ADMIN truy cập đúng các trang của mình -> HTTP 200")
    void adminAccessingOwnPages_shouldSucceed() {
        loginAs(TestAccount.ADMIN);

        for (String path : new String[]{
                "/app/admin/shareholders", "/app/admin/transfer-approval", "/app/admin/share-issue",
                "/app/admin/resolution", "/app/admin/share-adjust", "/app/admin/documents",
                "/app/admin/financial-reports/manage"
        }) {
            Response response = page.navigate(baseUrl + path);
            assertEquals(200, response.status(), "Trang " + path + " phai tra ve 200 cho ADMIN");
        }
    }

    @Test
    @DisplayName("IT truy cập đúng các trang của mình -> HTTP 200")
    void itAccessingOwnPages_shouldSucceed() {
        loginAs(TestAccount.IT);

        for (String path : new String[]{"/app/it/user-management", "/app/it/documents"}) {
            Response response = page.navigate(baseUrl + path);
            assertEquals(200, response.status(), "Trang " + path + " phai tra ve 200 cho IT");
        }
    }

    @Test
    @DisplayName("SHAREHOLDER truy cập đúng các trang của mình -> HTTP 200")
    void shareholderAccessingOwnPages_shouldSucceed() {
        loginAs(TestAccount.SHAREHOLDER);

        for (String path : new String[]{
                "/app/shareholder/notifications", "/app/shareholder/sign", "/app/shareholder/vote",
                "/app/shareholder/profile", "/app/shareholder/transfer-request",
                "/app/shareholder/documents", "/app/shareholder/financial-reports"
        }) {
            Response response = page.navigate(baseUrl + path);
            assertEquals(200, response.status(), "Trang " + path + " phai tra ve 200 cho SHAREHOLDER");
        }
    }

    @Test
    @DisplayName("Session không hợp lệ (cookie giả) -> vẫn bị chặn, không truy cập được /app/*")
    void invalidSessionCookie_shouldBeBlocked() {
        addInvalidSessionCookie();
        page.navigate(baseUrl + "/app/dashboard");
        assertThat(page).hasURL(baseUrl + "/login");
    }

    @Test
    @DisplayName("Response header của mọi trang /app/* phải có Cache-Control: no-store (chặn bfcache)")
    void protectedPages_shouldHaveNoCacheHeaders() {
        loginAs(TestAccount.ADMIN);
        Response response = page.navigate(baseUrl + "/app/dashboard");

        String cacheControl = response.headers().get("cache-control");
        assertThat(page).hasURL(baseUrl + "/app/dashboard");
        org.junit.jupiter.api.Assertions.assertNotNull(cacheControl);
        org.junit.jupiter.api.Assertions.assertTrue(cacheControl.contains("no-store"));
    }
}
