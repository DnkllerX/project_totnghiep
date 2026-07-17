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

            // actorUserId = chinh userId vua tao (tu dang ky, chua co ADMIN thao tac)
            AuditLog log = new AuditLog(AuditAction.CREATE, EntityType.USER, shareholderId, userId, userAgent);
            auditLogDAO.insert(conn, log);

            conn.commit();
            return shareholderId;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
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

            AuditLog log = new AuditLog(AuditAction.UPDATE, EntityType.USER, shareholderId, actorUserId, userAgent);
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
    }
}
