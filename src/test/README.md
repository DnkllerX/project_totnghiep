# VinScape — Automated UI Test (Playwright + Java)

Bộ test tự động hóa giao diện (UI) cho hệ thống Quản lý Cổ đông **VinScape**
(`shareholder-system`), viết bằng **Playwright for Java + JUnit 5**, theo mô hình
**Page Object Model (POM)**.

## 1. Yêu cầu môi trường

- JDK 17+
- Maven 3.8+
- Ứng dụng VinScape (WAR `shareholder-system`) đang **chạy sẵn** trên Tomcat,
  mặc định tại `http://localhost:8080/shareholder-system`
- Đã seed dữ liệu `snapshot01db.sql` với các tài khoản test bên dưới

## 2. Cài đặt

```bash
cd vinscape-playwright-tests
mvn install

# Tải trình duyệt cho Playwright (chỉ cần chạy 1 lần đầu tiên)
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

> Nếu không có plugin `exec`, có thể chạy trực tiếp:
> `mvn dependency:build-classpath -Dmdep.outputFile=cp.txt` rồi
> `java -cp "target/classes:$(cat cp.txt)" com.microsoft.playwright.CLI install`

## 3. Cấu hình

Sửa file `src/test/resources/config.properties` nếu cần (URL, tài khoản, headless...):

```properties
base.url=http://localhost:8080/shareholder-system
browser=chromium      # chromium | firefox | webkit
headless=true         # false de xem trinh duyet chay truc tiep khi debug
```

Có thể override nhanh khi chạy lệnh, không cần sửa file:

```bash
mvn test -Dheadless=false -Dbase.url=http://localhost:8080/shareholder-system
```

### Tài khoản test đang dùng (dữ liệu seed `snapshot01db`)

| Vai trò     | Đăng nhập bằng               | Mật khẩu           |
|-------------|-------------------------------|---------------------|
| ADMIN       | `admin@snapshot01.local`      | `Admin@123`         |
| IT          | `it@snapshot01.local`         | `it@snapshot01.local` |
| SHAREHOLDER | `user2`                       | `user2`              |

## 4. Chạy test

```bash
# Chạy toàn bộ
mvn test

# Chỉ chạy 1 class
mvn test -Dtest=LoginTest

# Chỉ chạy 1 method
mvn test -Dtest=LoginTest#loginWithWrongPassword_shouldShowError

# Chạy theo package (ví dụ chỉ test của SHAREHOLDER)
mvn test -Dtest="vinscape.tests.shareholder.*"
```

Ảnh chụp màn hình của các test **thất bại** sẽ tự động lưu vào `target/screenshots/`.

## 5. Cấu trúc thư mục

```
src/test/java/vinscape/
├── config/
│   ├── ConfigReader.java         # Đọc config.properties
│   └── TestAccount.java          # Enum 3 tài khoản test (ADMIN/IT/SHAREHOLDER)
├── base/
│   ├── BaseTest.java             # Vòng đời Playwright/Browser/Context/Page
│   └── ScreenshotOnFailureExtension.java
├── pages/                        # PAGE OBJECTS (1 class = 1 trang JSP)
│   ├── LoginPage.java
│   ├── RegisterPage.java
│   ├── ForgotPasswordPage.java
│   ├── AppShellPage.java         # Sidebar + topbar dùng chung (header.jsp)
│   ├── admin/                    # 7 trang chức năng của ADMIN
│   ├── it/                       # 2 trang chức năng của IT
│   └── shareholder/               # 7 trang chức năng của SHAREHOLDER
└── tests/                        # TEST CASES
    ├── auth/
    │   ├── LoginTest.java
    │   ├── LogoutTest.java
    │   ├── AccessControlTest.java             # Test AuthFilter (phân quyền)
    │   └── RegisterAndForgotPasswordTest.java
    ├── admin/
    │   ├── AdminNavigationTest.java
    │   ├── AdminShareholdersTest.java         # Bao gồm test bảo mật (SQLi/XSS payload)
    │   └── AdminFeaturePagesTest.java
    ├── it/
    │   ├── ITNavigationTest.java
    │   └── ITUserManagementTest.java
    ├── shareholder/
    │   ├── ShareholderNavigationTest.java
    │   ├── ShareholderProfileTest.java
    │   ├── ShareholderTransferRequestTest.java
    │   └── ShareholderFeaturePagesTest.java
    └── misc/
        └── AIChatboxTest.java
```

## 6. Nguyên tắc thiết kế quan trọng — AN TOÀN DỮ LIỆU

Một số test **cố ý** chỉ kiểm tra đường lỗi (validation) thay vì "happy path" tạo/sửa
dữ liệu thật, để **không phá hỏng dữ liệu seed** dùng chung cho các lần chạy sau:

- `ShareholderProfileTest`: KHÔNG đổi mật khẩu thành công thật (sẽ khiến `user2/user2`
  không còn đăng nhập được ở lần chạy sau). Chỉ test các trường hợp lỗi.
- `ShareholderTransferRequestTest`: KHÔNG tạo yêu cầu chuyển nhượng thật (sẽ sinh dữ
  liệu chờ duyệt trong DB, ảnh hưởng tới `AdminTransferApprovalTest` và số dư cổ phần).
- `AdminFeaturePagesTest` (share-adjust, financial-reports): dùng ID/giá trị không hợp lệ
  hoặc biên để test lỗi, tránh sửa số liệu tài chính/cổ phần thật của dữ liệu seed.

Nếu muốn bổ sung test "happy path" tạo dữ liệu thật, khuyến nghị chạy trên một
**CSDL test riêng** (restore lại `snapshot01db.sql` trước mỗi lần chạy CI) thay vì
DB đang dùng để phát triển.

## 7. Test bao phủ những gì

- **Đăng nhập / Đăng xuất**: 3 role, sai mật khẩu, tài khoản không tồn tại, session
  fixation (JSESSIONID đổi sau login), bfcache sau logout, các trường bắt buộc.
- **Phân quyền (AuthFilter)**: chưa đăng nhập → redirect `/login`; sai role → HTTP 403;
  đúng role → HTTP 200; cookie session giả; header `Cache-Control: no-store`.
- **Điều hướng sidebar**: đúng menu hiển thị theo từng role, class `active`, ẩn menu
  không thuộc quyền.
- **Bảo mật**: payload SQL injection / XSS trên ô tìm kiếm cổ đông (kỳ vọng an toàn
  nhờ `PreparedStatement` + escape khi hiển thị).
- **Từng trang chức năng** của ADMIN / IT / SHAREHOLDER: hiển thị đúng form/field,
  validate dữ liệu đầu vào theo đúng message lấy từ các `*Service.java` tương ứng.
- **Widget AI Chatbox**: mở/đóng panel, xuất hiện trên mọi trang kể cả khi chưa đăng nhập.

## 8. Gợi ý mở rộng

- Thêm test tạo dữ liệu "happy path" đầy đủ (phát hành cổ phần → ký nhận → biểu quyết)
  chạy trên CSDL test riêng, dùng `@Order` hoặc 1 luồng kịch bản E2E xuyên suốt.
- Tích hợp Allure/JUnit HTML report để xem báo cáo trực quan hơn.
- Thêm CI pipeline (GitHub Actions) chạy `mvn test -Dheadless=true` mỗi khi push code,
  kèm bước restore `snapshot01db.sql` trước khi chạy.
