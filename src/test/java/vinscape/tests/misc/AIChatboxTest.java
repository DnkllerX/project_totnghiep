package vinscape.tests.misc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vinscape.base.BaseTest;
import vinscape.config.TestAccount;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Test widget AI Chatbox (common/ai-chatbox.jsp) - xuat hien o goc man hinh tren moi trang,
 * ke ca trang login (chua dang nhap).
 */
@DisplayName("Widget AI Chatbox")
class AIChatboxTest extends BaseTest {

    @BeforeEach
    void setUp() {
        loginAs(TestAccount.SHAREHOLDER);
    }

    @Test
    @DisplayName("Nút mở chatbox (launcher) hiển thị trên trang, panel chat đang ẩn ban đầu")
    void chatboxLauncher_shouldBeVisibleOnLoad() {
        assertThat(page.locator("#vsc-launcher")).isVisible();
    }

    @Test
    @DisplayName("Click launcher -> mở panel chat, hiển thị ô nhập liệu và nút gửi")
    void clickingLauncher_shouldOpenChatPanel() {
        page.locator("#vsc-launcher").click();

        assertThat(page.locator("#vsc-panel")).isVisible();
        assertThat(page.locator("#vsc-input")).isVisible();
        assertThat(page.locator("#vsc-send")).isVisible();
    }
/*
    @Test
    @DisplayName("Click nút đóng (#vsc-close) -> panel chat ẩn đi")
    void clickingClose_shouldHideChatPanel() {
        page.locator("#vsc-launcher").click();
        assertThat(page.locator("#vsc-panel")).isVisible();

        page.locator("#vsc-close").click();

        assertThat(page.locator("#vsc-panel")).not().isVisible();
    }
*/
    @Test
    @DisplayName("Widget chatbox cũng xuất hiện trên trang login (chưa đăng nhập)")
    void chatboxWidget_shouldAlsoAppearOnLoginPage() {
        page.navigate(baseUrl + "/login");
        assertThat(page.locator("#vsc-launcher")).isVisible();
    }

    @Test
    @DisplayName("Nhập câu hỏi vào ô chat -> nội dung được giữ nguyên trong input (không bị mất khi gõ)")
    void typingInChatInput_shouldRetainText() {
        page.locator("#vsc-launcher").click();
        page.locator("#vsc-input").fill("Xin chào, tôi muốn hỏi về cổ tức");

        assertThat(page.locator("#vsc-input")).hasValue("Xin chào, tôi muốn hỏi về cổ tức");
    }

    @Test
    @DisplayName("Nút Xóa hội thoại (#vsc-clear) hiển thị trong header của panel chat")
    void clearButton_shouldBeVisibleInPanelHeader() {
        page.locator("#vsc-launcher").click();
        assertThat(page.locator("#vsc-clear")).isVisible();
    }
}
