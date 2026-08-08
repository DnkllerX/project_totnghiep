package vinscape.base;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import vinscape.config.ConfigReader;
import vinscape.config.TestAccount;
import vinscape.pages.LoginPage;

import java.util.List;

/**
 * Lop cha cho toan bo test UI.
 * - 1 Playwright + 1 Browser duoc dung chung cho ca class test (nhanh hon, do khoi tao browser ton thoi gian).
 * - Moi test (@BeforeEach) duoc cap 1 BrowserContext + Page RIENG (coi nhu 1 trinh duyet an danh moi)
 *   de dam bao test nay khong lam anh huong (cookie/session) toi test kia.
 */
@ExtendWith(ScreenshotOnFailureExtension.class)
public abstract class BaseTest {

    protected static Playwright playwright;
    protected static Browser browser;

    protected BrowserContext context;
    protected Page page;

    protected final String baseUrl = ConfigReader.baseUrl();

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();

        String browserName = ConfigReader.get("browser", "chromium").toLowerCase();
        boolean headless = ConfigReader.getBoolean("headless", true);
        double slowMo = ConfigReader.getInt("slowmo", 0);

        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(slowMo)
                .setArgs(List.of("--start-maximized"));

        browser = switch (browserName) {
            case "firefox" -> playwright.firefox().launch(options);
            case "webkit" -> playwright.webkit().launch(options);
            default -> playwright.chromium().launch(options);
        };
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1440, 900)
                .setLocale("vi-VN")
                .setIgnoreHTTPSErrors(true));
        context.setDefaultTimeout(ConfigReader.getInt("default.timeout", 10000));
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        if (context != null) context.close();
    }

    public Page getPage() {
        return page;
    }

    // ---------------------------------------------------------------
    // Helper dung chung cho cac lop test con
    // ---------------------------------------------------------------

    /** Dang nhap bang mot tai khoan test co san va cho toi khi dashboard tai xong. */
    protected void loginAs(TestAccount account) {
        LoginPage loginPage = new LoginPage(page, baseUrl);
        loginPage.navigate();
        loginPage.login(account.login(), account.password());
    }

    /** Xoa toan bo cookie hien tai (mo phong nguoi dung chua dang nhap / da dang xuat). */
    protected void clearSession() {
        context.clearCookies();
    }

    /** Tao 1 cookie rac de test AuthFilter (session khong hop le). */
    protected void addInvalidSessionCookie() {
        context.addCookies(List.of(new Cookie("JSESSIONID", "invalid-session-id-00000")
                .setUrl(baseUrl)));
    }
}
