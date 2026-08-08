package vinscape.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page Object cho phan "khung" chung cua ung dung sau khi dang nhap:
 * sidebar (menu dieu huong theo role) + topbar (ten user, nut dang xuat).
 * Tuong ung voi common/header.jsp.
 */
public class AppShellPage {

    protected final Page page;
    protected final String baseUrl;

    public AppShellPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public Locator sidebar() {
        return page.locator(".app-sidebar");
    }

    public Locator navItem(String visibleText) {
        return page.locator(".nav-item", new Page.LocatorOptions().setHasText(visibleText));
    }

    public Locator activeNavItem() {
        return page.locator(".nav-item.active");
    }

    public Locator topbarUserLabel() {
        return page.locator(".tb-user");
    }

    public Locator logoutButton() {
        return page.locator(".btn-logout");
    }

    public void logout() {
        logoutButton().click();
        page.waitForURL("**/login");
    }

    public void goToNav(String visibleText) {
        navItem(visibleText).click();
    }

    /** Kiem tra da dang nhap thanh cong voi dung ten + role hien thi tren topbar. */
    public boolean isLoggedInAs(String username, String role) {
        String text = topbarUserLabel().textContent();
        return text != null && text.contains(username) && text.contains(role);
    }
}
