package com.shareholder.service;

import com.shareholder.dao.AuditLogDAO;
import com.shareholder.dao.UserDAO;
import com.shareholder.dao.impl.AuditLogDAOImpl;
import com.shareholder.dao.impl.UserDAOImpl;
import com.shareholder.model.AuditLog;
import com.shareholder.model.User;
import com.shareholder.model.enums.AuditAction;
import com.shareholder.model.enums.EntityType;
import com.shareholder.model.enums.UserStatus;
import com.shareholder.util.PasswordUtil;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {

    private final UserDAO userDAO = new UserDAOImpl();
    private final AuditLogDAO auditLogDAO = new AuditLogDAOImpl();

    public enum LoginResult { SUCCESS, INVALID_CREDENTIALS, ACCOUNT_LOCKED }

    public static class LoginOutcome {
        public final LoginResult result;
        public final User user; // null neu that bai

        public LoginOutcome(LoginResult result, User user) {
            this.result = result;
            this.user = user;
        }
    }

    /**
     * Luon tra ve thong bao chung "sai username/mat khau" cho ca 2 truong hop user khong ton tai
     * hoac sai mat khau - tranh lo thong tin user co ton tai hay khong (user enumeration).
     */
    public LoginOutcome login(String usernameOrEmail, String plainPassword, String userAgent) throws SQLException {
        Optional<User> userOpt = usernameOrEmail.contains("@")
                ? userDAO.findByEmail(usernameOrEmail)
                : userDAO.findByUsername(usernameOrEmail);

        if (userOpt.isEmpty() || !PasswordUtil.verify(plainPassword, userOpt.get().getPasswordHash())) {
            return new LoginOutcome(LoginResult.INVALID_CREDENTIALS, null);
        }

        User user = userOpt.get();

        if (user.getStatus() == UserStatus.LOCKED) {
            logAudit(user.getUserId(), AuditAction.LOGIN, userAgent); // van log de phat hien brute-force
            return new LoginOutcome(LoginResult.ACCOUNT_LOCKED, null);
        }

        logAudit(user.getUserId(), AuditAction.LOGIN, userAgent);
        return new LoginOutcome(LoginResult.SUCCESS, user);
    }

    public void logout(int userId, String userAgent) throws SQLException {
        logAudit(userId, AuditAction.LOGOUT, userAgent);
    }

    private void logAudit(int userId, AuditAction action, String userAgent) throws SQLException {
        AuditLog log = new AuditLog(action, EntityType.USER, userId, userId, userAgent);
        auditLogDAO.insert(log);
    }
}
