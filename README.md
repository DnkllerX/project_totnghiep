# He thong Quan ly Co dong (Shareholder Management System)

Java Maven Web (Jakarta EE 10) - Tomcat 10.1 - JDK 17 - SQL Server

## 1. Chuan bi database
Chay file `snapshot01db.sql` (gui kem rieng, ngoai project nay) tren SQL Server de tao schema.

## 2. Cau hinh ket noi DB
Sua file `src/main/resources/db.properties`:
```
db.url=jdbc:sqlserver://localhost:1433;databaseName=snapshot01db;encrypt=true;trustServerCertificate=true
db.username=sa
db.password=<mat khau that>
```

## 3. Cau hinh JWT secret (khuyen nghi cho production)
Neu khong cau hinh, he thong tu sinh secret ngau nhien moi lan Tomcat khoi dong (token se
mat hieu luc sau restart - chi phu hop moi truong dev). De co secret co dinh, set truoc khi chay Tomcat:
- Bien moi truong: `JWT_SECRET=<chuoi it nhat 32 ky tu>`
- Hoac JVM property: `-Djwt.secret=<chuoi it nhat 32 ky tu>`

## 4. Thu muc luu file (chu ky va tai lieu)
Mac dinh:
- Chu ky tay: `/var/shareholder-system/signatures`
- Tai lieu: `/var/shareholder-system/documents`

Doi lai bang JVM property neu can (vd tren Windows):
```
-Dsignature.storage.root=C:\shareholder-system\signatures
-Ddocument.storage.root=C:\shareholder-system\documents
```
Dam bao tai khoan chay Tomcat co quyen ghi vao thu muc nay, va thu muc nay NAM NGOAI webapp
(khong nam trong webapps/ROOT hay thu muc deploy) de tranh truy cap file truc tiep qua URL.

## 5. Import vao Eclipse
1. File -> Import -> Maven -> Existing Maven Projects -> chon thu muc goc project nay
2. Chuot phai project -> Properties -> Project Facets -> bat "Dynamic Web Module" 6.0 va "Java" 17
3. Neu Eclipse bao thieu server runtime: Window -> Preferences -> Server -> Runtime Environments
   -> Add -> Apache Tomcat v10.1
4. Chuot phai project -> Run As -> Run on Server -> chon Tomcat 10.1

## 6. Tai khoan dau tien
Schema khong co san du lieu mau. Insert truc tiep 1 user IT bang SQL de co the dang nhap lan dau
va tu tao cac tai khoan khac qua giao dien (mat khau phai duoc bam bang BCrypt - co the dung
lop `PasswordUtil.hash()` trong 1 doan code test nhanh de sinh hash roi insert thu cong):
```sql
INSERT INTO USERS (username, email, password_hash, role, status, created_at)
VALUES ('it_admin', 'it@company.com', '<bcrypt-hash-tu-PasswordUtil.hash>', 'IT', 'ACTIVE', SYSDATETIME());
```

## 7. Cau hinh AI Chatbox (Gemini)
Widget chat AI noi goc phai man hinh, hien tren toan bo trang GUEST (trang chu, dang nhap,
dang ky, quen/dat lai mat khau) va trang USER da dang nhap (ADMIN/IT/SHAREHOLDER).

Sua file `src/main/resources/gemini.properties` (copy tu `gemini.properties.example` neu chua co):
```
gemini.enabled=true
gemini.api.key=<API key that, lay tai https://aistudio.google.com/app/apikey>
gemini.model=gemini-3.1-flash-lite
```
Mac dinh `gemini.enabled=false` - khi do widget van hien nhung se bao "chua duoc cau hinh"
thay vi loi xau. API key CHI nam server-side (khong bao gio nhung vao JSP/JS); frontend chi
goi ve endpoint noi bo `POST /chatbot` (xem `ChatbotServlet` -> `ChatbotService`).

Co the doi model, system prompt, gioi han lich su/toc do phan hoi, so tin nhan toi da moi
phien (chong spam) ngay trong file properties nay ma khong can sua code.

## 8. Kien truc
```
Filter (AuthFilter - session + phan quyen theo role)
  -> Servlet (Controller - com.shareholder.controller.*)
    -> Service (Business logic - com.shareholder.service.*)
      -> DAO (JDBC/PreparedStatement - com.shareholder.dao.*)
        -> Model (POJO - com.shareholder.model.*)
  -> JSP (View - src/main/webapp/WEB-INF/views/*, dung JSTL <c:out> chong XSS)

Scheduler (com.shareholder.scheduler.*) chay doc lap qua ScheduledExecutorService,
duoc khoi dong boi SchedulerContextListener luc app deploy, kiem tra SCHEDULED_EVENTS
moi 60 giay de tu dong xu ly PROCESS_SHARE_ISSUE va CLOSE_RESOLUTION.
```

## 9. Cac diem bao mat da ap dung
- 100% PreparedStatement, khong noi chuoi SQL tu input nguoi dung (chong SQL Injection)
- Mat khau bam bang BCrypt (work factor 12), khong bao gio luu/so sanh plaintext
- Chu ky tay (canvas base64) duoc kiem tra magic bytes thuc te truoc khi luu, gioi han 2MB,
  luu ten file bang UUID (khong dung ten/duong dan tu client) de tranh path traversal
- File tai lieu upload: kiem tra duoi file hop le, luu ten UUID, gioi han 20MB
- JSP dung JSTL `<c:out>` de tu dong escape HTML (chong XSS)
- Session dang nhap duoc sinh lai (invalidate + tao moi) sau khi login thanh cong (chong session fixation)
- Rang buoc UNIQUE o tang DB (VOTES, SHARE_ISSUE_DETAILS...) la lop bao ve cuoi cung chong
  race condition, khong chi dua vao kiem tra o tang ung dung
- Loi he thong (SQLException...) khong lo chi tiet ra ngoai cho nguoi dung, chi ghi log noi bo
- Endpoint `/chatbot`: API key Gemini chi song server-side, khong tra ve client; gioi han
  do dai tin nhan va so tin nhan/phien (session) de chong lam dung chi phi API
