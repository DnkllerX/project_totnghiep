package vinscape.base;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.ScreenshotType;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

/**
 * Tu dong chup anh man hinh khi mot test that bai, luu vao target/screenshots/
 * Giup debug nhanh khi chay test tren CI (khong co man hinh de xem truc tiep).
 */
public class ScreenshotOnFailureExtension implements TestWatcher {

    private static final Path OUTPUT_DIR = Paths.get("target", "screenshots");

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        Object testInstance = context.getRequiredTestInstance();
        if (!(testInstance instanceof BaseTest baseTest)) {
            return;
        }
        Page page = baseTest.getPage();
        if (page == null || page.isClosed()) {
            return;
        }
        try {
            OUTPUT_DIR.toFile().mkdirs();
            String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
            String fileName = context.getRequiredTestClass().getSimpleName()
                    + "_" + context.getRequiredTestMethod().getName()
                    + "_" + timestamp + ".png";
            Path target = OUTPUT_DIR.resolve(fileName);
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(target)
                    .setFullPage(true)
                    .setType(ScreenshotType.PNG));
            System.out.println("[Screenshot] Test that bai, da luu anh man hinh: " + target.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("[Screenshot] Khong the chup anh man hinh: " + e.getMessage());
        }
    }
}
