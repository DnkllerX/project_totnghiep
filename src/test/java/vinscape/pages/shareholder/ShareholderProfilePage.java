package vinscape.pages.shareholder;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import vinscape.pages.AppShellPage;

/**
 * Page Object cho /app/shareholder/profile (shareholder/profile.jsp)
 * Trang gom 2 form doc lap: cap nhat thong tin ca nhan va doi mat khau.
 */
public class ShareholderProfilePage extends AppShellPage {

    public ShareholderProfilePage(Page page, String baseUrl) {
        super(page, baseUrl);
    }

    public String url() {
        return baseUrl + "/app/shareholder/profile";
    }

    public void navigate() {
        page.navigate(url());
        sidebar().waitFor();
    }

    // --- Form cap nhat thong tin ---
    public Locator fullNameInput() {
        return page.locator("#fullName");
    }

    public Locator citizenIdInput() {
        return page.locator("#citizenId");
    }

    public Locator phoneInput() {
        return page.locator("#phone");
    }

    public Locator addressInput() {
        return page.locator("#address");
    }

    public Locator saveInfoButton() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Lưu thông tin"));
    }

    public void updateInfo(String fullName, String citizenId, String phone, String address) {
        fullNameInput().fill(fullName);
        citizenIdInput().fill(citizenId);
        phoneInput().fill(phone);
        addressInput().fill(address);
        saveInfoButton().click();
    }

    // --- Form doi mat khau ---
    public Locator currentPasswordInput() {
        return page.locator("#currentPassword");
    }

    public Locator newPasswordInput() {
        return page.locator("#newPassword");
    }

    public Locator confirmNewPasswordInput() {
        return page.locator("#confirmNewPassword");
    }

    public Locator changePasswordButton() {
        return page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Đổi mật khẩu"));
    }

    public void changePassword(String currentPassword, String newPassword, String confirmNewPassword) {
        currentPasswordInput().fill(currentPassword);
        newPasswordInput().fill(newPassword);
        confirmNewPasswordInput().fill(confirmNewPassword);
        changePasswordButton().click();
    }

    // --- Thong bao ket qua ---
    public Locator errorBox() {
        return page.locator(".error-box");
    }

    public Locator successBox() {
        return page.locator(".success-box");
    }
}
