package com.shareholder.service;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.*;
import com.shareholder.dao.impl.*;
import com.shareholder.model.*;
import com.shareholder.model.enums.*;
import com.shareholder.util.PasswordUtil;
import com.shareholder.util.ValidationUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Luong dung: nguoi dung tu dang ky tai khoan (registerShareholderAccount) voi role=SHAREHOLDER
 * nhung status=LOCKED ngay tu dau (khong the dang nhap - AuthService chan tai khoan LOCKED).
 * ADMIN xem danh sach cho duyet (findLockedShareholders) -> duyet + cap so co phan khoi tao
 * (approveShareholder) -> status chuyen LOCKED -> ACTIVE, luc do moi dang nhap va dung duoc he thong.
 */
public class ShareholderService {

    private final UserDAO userDAO = new UserDAOImpl();
    private final ShareholderDAO shareholderDAO = new ShareholderDAOImpl();
    private final ShareDAO shareDAO = new ShareDAOImpl();
    private final ShareTransactionDAO shareTransactionDAO = new ShareTransactionDAOImpl();
    private final AuditLogDAO auditLogDAO = new AuditLogDAOImpl();
    private final EmailService emailService = new EmailService();
    private static final Logger LOGGER = Logger.getLogger(ShareholderService.class.getName());

    public static class ValidationException extends Exception {
        public ValidationException(String message) { super(message); }
    }

    /** DTO ghep User + Shareholder de JSP co du shareholder_id can thiet cho form duyet. */
    public static class PendingApproval {
        public final int shareholderId;
        public final int userId;
        public final String username;
        public final String email;
        public final String fullName;

        public PendingApproval(int shareholderId, int userId, String username, String email, String fullName) {
            this.shareholderId = shareholderId;
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.fullName = fullName;
        }

        public int getShareholderId() { return shareholderId; }
        public int getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getFullName() { return fullName; }
    }

    /**
     * Tu dang ky: tao USERS (role=SHAREHOLDER, status=LOCKED) -> tao SHAREHOLDERS (ho so) ->
     * tao SHARES = 0, tat ca trong 1 transaction. Tai khoan KHONG dang nhap duoc cho den khi
     * ADMIN duyet (AuthService.login tra ACCOUNT_LOCKED cho tai khoan status=LOCKED).
     */
    public int registerShareholderAccount(String username, String email, String plainPassword,
                                           String fullName, String citizenId, String phone, String address,
                                           LocalDate birthDate, String nationality,
                                           String userAgent) throws SQLException, ValidationException {
        // actorUserId = null -> ben trong se tu dung chinh userId vua tao (tu dang ky cong khai,
        // chua co ai khac thao tac). Xem overload ben duoi cho truong hop IT tao thay.
        return registerShareholderAccount(username, email, plainPassword, fullName, citizenId, phone,
                address, birthDate, nationality, null, userAgent);
    }

    /**
     * Bien the danh cho truong hop 1 nhan vien IT tao tai khoan SHAREHOLDER thay cho khach hang
     * (trang /app/it/user-management?tab=create), KHAC voi tu dang ky cong khai qua /register.
     * Van tao du USERS(status=LOCKED)+SHAREHOLDERS+SHARES(0) va CHO ADMIN DUYET giong het luong tu
     * dang ky (khong active ngay) - dung y het logic, chi khac o cho audit log ghi dung IT nao la
     * nguoi thuc su tao (thay vi ghi "tu tao" nhu truong hop tu dang ky).
     */
    public int registerShareholderAccount(String username, String email, String plainPassword,
                                           String fullName, String citizenId, String phone, String address,
                                           LocalDate birthDate, String nationality,
                                           Integer createdByActorUserId, String userAgent)
            throws SQLException, ValidationException {

        if (!ValidationUtil.isValidUsername(username)) throw new ValidationException("Username khong hop le");
        if (!ValidationUtil.isValidEmail(email)) throw new ValidationException("Email khong hop le");
        if (!ValidationUtil.isPasswordNonEmpty(plainPassword)) throw new ValidationException("Mat khau khong duoc de trong");
        if (!ValidationUtil.isValidCitizenId(citizenId)) throw new ValidationException("So CCCD/CMND khong hop le");
        if (!ValidationUtil.isValidPhone(phone)) throw new ValidationException("So dien thoai khong hop le");
        if (userDAO.existsByUsername(username)) throw new ValidationException("Username da ton tai");
        if (userDAO.existsByEmail(email)) throw new ValidationException("Email da ton tai");
        if (shareholderDAO.existsByCitizenId(citizenId)) throw new ValidationException("CCCD/CMND da ton tai");

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            User user = new User();
            user.setUsername(ValidationUtil.sanitizeText(username, 50));
            user.setEmail(ValidationUtil.sanitizeText(email, 100));
            user.setPasswordHash(PasswordUtil.hash(plainPassword));
            user.setRole(UserRole.SHAREHOLDER);
            user.setStatus(UserStatus.LOCKED);
            int userId = userDAO.insert(conn, user);

            Shareholder sh = new Shareholder();
            sh.setUserId(userId);
            sh.setFullName(ValidationUtil.sanitizeText(fullName, 150));
            sh.setCitizenId(citizenId);
            sh.setPhone(phone);
            sh.setAddress(ValidationUtil.sanitizeText(address, 255));
            sh.setBirthDate(birthDate);
            sh.setNationality(ValidationUtil.sanitizeText(nationality, 50));
            int shareholderId = shareholderDAO.insert(conn, sh);

            Share share = new Share();
            share.setShareholderId(shareholderId);
            share.setQuantity(0);
            shareDAO.insert(conn, share);

            // actorUserId = createdByActorUserId (IT tao thay) neu co, khong thi la chinh userId
            // vua tao (tu dang ky cong khai, chua co ai khac thao tac).
            // entityId = userId (khop voi entityType=USER, dong nhat voi cach AuthService dang lam
            // cho LOGIN/LOGOUT - truoc day nham gan shareholderId vao day, gay sai lech du lieu)
            int auditActorId = createdByActorUserId != null ? createdByActorUserId : userId;
            AuditLog log = new AuditLog(AuditAction.CREATE, EntityType.USER, userId, auditActorId, userAgent);
            auditLogDAO.insert(conn, log);

            conn.commit();
            return shareholderId;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            // Race condition (TOCTOU): 2 nguoi dang ky cung username/email/CCCD gan nhu dong thoi,
            // ca 2 deu qua duoc cac check existsBy...() o tren (chua ai insert xong luc do), nhung
            // chi 1 nguoi insert truoc duoc, nguoi con lai vi pham UNIQUE constraint o day.
            // Bat lai thanh loi nghiep vu ro rang thay vi de lo ra loi he thong 500 kho hieu.
            if (isUniqueViolation(e)) {
                String msg = e.getMessage() != null ? e.getMessage() : "";
                if (msg.contains("UQ_Users_Username")) {
                    throw new ValidationException("Username da ton tai");
                } else if (msg.contains("UQ_Users_Email")) {
                    throw new ValidationException("Email da ton tai");
                } else if (msg.contains("UQ_Shareholders_CitizenId")) {
                    throw new ValidationException("CCCD/CMND da ton tai");
                }
                throw new ValidationException("Thong tin dang ky bi trung voi tai khoan khac, vui long thu lai");
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    private boolean isUniqueViolation(SQLException e) {
        // SQL Server: 2627 = Violation of UNIQUE KEY constraint, 2601 = Cannot insert duplicate key
        return e.getErrorCode() == 2627 || e.getErrorCode() == 2601;
    }

    /**
     * Danh sach tai khoan dang cho duyet (role=SHAREHOLDER, status=LOCKED), kem shareholder_id.
     * Luu y: cung danh sach nay se hien thi ca tai khoan bi ADMIN/IT khoa sau nay (vi he thong
     * dung chung 1 status cho ca 2 truong hop, theo thiet ke da thong nhat).
     */
    public List<PendingApproval> findLockedShareholders() throws SQLException {
        List<PendingApproval> result = new java.util.ArrayList<>();
        for (User u : userDAO.findLockedShareholders()) {
            Optional<Shareholder> shOpt = shareholderDAO.findByUserId(u.getUserId());
            if (shOpt.isPresent()) {
                result.add(new PendingApproval(shOpt.get().getShareholderId(), u.getUserId(),
                        u.getUsername(), u.getEmail(), shOpt.get().getFullName()));
            }
        }
        return result;
    }

    /**
     * ADMIN duyet 1 tai khoan: chuyen status LOCKED -> ACTIVE, cap so co phan khoi tao (neu > 0).
     * Dieu kien "status hien tai phai la LOCKED" (updateStatusIfCurrentStatus) chan viec duyet 2 lan.
     * Neu initialQuantity = 0, van duyet (mo khoa) nhung khong sinh giao dich INITIAL rac.
     */
    public void approveShareholder(int shareholderId, int initialQuantity, int actorUserId, String userAgent)
            throws SQLException, ValidationException {

        if (initialQuantity < 0) throw new ValidationException("So co phan khoi tao khong the am");

        Optional<Shareholder> shOpt = shareholderDAO.findById(shareholderId);
        if (shOpt.isEmpty()) throw new ValidationException("Khong tim thay co dong");
        int userId = shOpt.get().getUserId();

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            boolean unlocked = userDAO.updateStatusIfCurrentStatus(conn, userId,
                    UserStatus.LOCKED, UserStatus.ACTIVE);
            if (!unlocked) {
                conn.rollback();
                throw new ValidationException("Tai khoan nay khong o trang thai LOCKED (co the da duoc duyet truoc do)");
            }

            if (initialQuantity > 0) {
                shareDAO.setQuantity(conn, shareholderId, initialQuantity);

                ShareTransaction tx = new ShareTransaction();
                tx.setFromShareholderId(null);
                tx.setToShareholderId(shareholderId);
                tx.setQuantity(initialQuantity);
                tx.setTxType(TxType.INITIAL);
                tx.setStatus(TxStatus.COMPLETED);
                shareTransactionDAO.insert(conn, tx);
            }

            // entityId = userId (khop voi entityType=USER, khong dung shareholderId o day)
            AuditLog log = new AuditLog(AuditAction.UPDATE, EntityType.USER, userId, actorUserId, userAgent);
            auditLogDAO.insert(conn, log);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }

        // Gui email THONG BAO SAU KHI da commit thanh cong - loi SMTP (neu co) chi duoc log,
        // KHONG duoc lam that bai/rollback viec duyet tai khoan (nghiep vu chinh da xong).
        try {
            Optional<User> userOpt = userDAO.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                emailService.sendAccountApprovedEmail(user.getEmail(), shOpt.get().getFullName(), user.getUsername());
            }
        } catch (SQLException | EmailService.EmailException e) {
            LOGGER.log(Level.WARNING, "Gui email thong bao duyet tai khoan that bai cho shareholderId="
                    + shareholderId, e);
        }
    }
}
