package com.shareholder.service;

import com.shareholder.dao.AuditLogDAO;
import com.shareholder.dao.UserDAO;
import com.shareholder.dao.impl.AuditLogDAOImpl;
import com.shareholder.dao.impl.UserDAOImpl;
import com.shareholder.model.AuditLog;
import com.shareholder.model.User;
import com.shareholder.model.enums.AuditAction;
import com.shareholder.model.enums.EntityType;
import com.shareholder.model.enums.UserRole;
import com.shareholder.model.enums.UserSortOption;
import com.shareholder.model.enums.UserStatus;
import com.shareholder.util.PasswordUtil;
import com.shareholder.util.ValidationUtil;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * Nghiep vu danh cho IT: tao/khoa/mo khoa/reset mat khau tai khoan.
 * IT KHONG duoc dieu chinh co phan, tao phat hanh, bieu quyet, bao cao tai chinh (theo bang phan quyen)
 * - cac rule nay duoc AuthFilter chan o tang duong dan, o day chi tap trung nghiep vu tai khoan.
 */
public class UserAccountService {

    private final UserDAO userDAO = new UserDAOImpl();
    private final AuditLogDAO auditLogDAO = new AuditLogDAOImpl();
    private final SecureRandom secureRandom = new SecureRandom();

    public static class ValidationException extends Exception {
        public ValidationException(String message) { super(message); }
    }

    public int createAccount(String username, String email, String plainPassword, UserRole role,
                              int actorUserId, String userAgent) throws SQLException, ValidationException {
        if (!ValidationUtil.isValidUsername(username)) {
            throw new ValidationException("Username khong hop le (4-50 ky tu, chi chu/so/gach duoi)");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new ValidationException("Email khong hop le");
        }
        if (!ValidationUtil.isPasswordNonEmpty(plainPassword)) {
            throw new ValidationException("Mat khau khong duoc de trong");
        }
        if (userDAO.existsByUsername(username)) {
            throw new ValidationException("Username da ton tai");
        }
        if (userDAO.existsByEmail(email)) {
            throw new ValidationException("Email da ton tai");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(PasswordUtil.hash(plainPassword));
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);

        int newUserId = userDAO.insert(user);
        logAudit(actorUserId, AuditAction.CREATE, newUserId, userAgent);
        return newUserId;
    }

    /** Tim kiem/loc/sort danh sach tai khoan cho trang quan ly. Tat ca tham so optional (null = bo qua). */
    public List<User> search(String usernameContains, String emailContains, UserRole role, UserStatus status,
                              UserSortOption sort) throws SQLException {
        return userDAO.search(usernameContains, emailContains, role, status, sort);
    }

    /**
     * Sua thong tin tai khoan (username/email/role). KHONG dung de doi status - dung lockAccount/
     * unlockAccount rieng cho viec do, tranh 1 form lam nhieu viec de gay nham lan khi review.
     */
    public void updateAccount(int targetUserId, String username, String email, UserRole role,
                               int actorUserId, String userAgent) throws SQLException, ValidationException {
        if (!ValidationUtil.isValidUsername(username)) {
            throw new ValidationException("Username khong hop le (4-50 ky tu, chi chu/so/gach duoi)");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new ValidationException("Email khong hop le");
        }

        Optional<User> currentOpt = userDAO.findById(targetUserId);
        if (currentOpt.isEmpty()) throw new ValidationException("Khong tim thay tai khoan");
        User current = currentOpt.get();

        // Chi kiem tra trung neu gia tri thuc su thay doi (tranh tu bao trung voi chinh minh)
        if (!username.equals(current.getUsername()) && userDAO.existsByUsername(username)) {
            throw new ValidationException("Username da ton tai");
        }
        if (!email.equals(current.getEmail()) && userDAO.existsByEmail(email)) {
            throw new ValidationException("Email da ton tai");
        }

        current.setUsername(username);
        current.setEmail(email);
        current.setRole(role);
        // status giu nguyen (current.getStatus() khong doi) - update() se ghi lai dung status hien tai
        userDAO.update(current);
        logAudit(actorUserId, AuditAction.UPDATE, targetUserId, userAgent);
    }

    /** Luu y nghiep vu: khoa tai khoan chi doi status, khong lien quan gi den role. */
    public void lockAccount(int targetUserId, int actorUserId, String userAgent) throws SQLException {
        userDAO.updateStatus(targetUserId, UserStatus.LOCKED);
        logAudit(actorUserId, AuditAction.UPDATE, targetUserId, userAgent);
    }

    public void unlockAccount(int targetUserId, int actorUserId, String userAgent) throws SQLException {
        userDAO.updateStatus(targetUserId, UserStatus.ACTIVE);
        logAudit(actorUserId, AuditAction.UPDATE, targetUserId, userAgent);
    }

    /** Sinh mat khau tam ngau nhien (khong doan duoc), tra ve plaintext DUY NHAT 1 LAN de gui cho user. */
    public String resetPassword(int targetUserId, int actorUserId, String userAgent) throws SQLException {
        byte[] randomBytes = new byte[9];
        secureRandom.nextBytes(randomBytes);
        String tempPassword = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        userDAO.updatePasswordHash(targetUserId, PasswordUtil.hash(tempPassword));
        logAudit(actorUserId, AuditAction.UPDATE, targetUserId, userAgent);
        return tempPassword;
    }

    private void logAudit(int actorUserId, AuditAction action, int targetUserId, String userAgent)
            throws SQLException {
        AuditLog log = new AuditLog(action, EntityType.USER, targetUserId, actorUserId, userAgent);
        auditLogDAO.insert(log);
    }
}
