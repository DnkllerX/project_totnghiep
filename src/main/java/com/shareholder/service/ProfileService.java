package com.shareholder.service;

import com.shareholder.dao.AuditLogDAO;
import com.shareholder.dao.ShareholderDAO;
import com.shareholder.dao.UserDAO;
import com.shareholder.dao.impl.AuditLogDAOImpl;
import com.shareholder.dao.impl.ShareholderDAOImpl;
import com.shareholder.dao.impl.UserDAOImpl;
import com.shareholder.model.AuditLog;
import com.shareholder.model.Shareholder;
import com.shareholder.model.User;
import com.shareholder.model.enums.AuditAction;
import com.shareholder.model.enums.EntityType;
import com.shareholder.util.PasswordUtil;
import com.shareholder.util.ValidationUtil;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Nghiep vu "tai khoan cua toi" - KHAC voi UserAccountService (danh cho IT quan tri tai khoan nguoi khac).
 * doi mat khau o day BAT BUOC phai nhap dung mat khau CU truoc - khong giong resetPassword() cua IT
 * (IT dat lai ho, khong can biet mat khau cu). Tach rieng de tranh nham lan quyen han giua 2 luong.
 */
public class ProfileService {

    private final UserDAO userDAO = new UserDAOImpl();
    private final ShareholderDAO shareholderDAO = new ShareholderDAOImpl();
    private final AuditLogDAO auditLogDAO = new AuditLogDAOImpl();

    public static class ValidationException extends Exception {
        public ValidationException(String message) { super(message); }
    }

    public static class ProfileInfo {
        public final User user;
        public final Shareholder shareholder; // null neu tai khoan khong phai SHAREHOLDER / chua co ho so
        public ProfileInfo(User user, Shareholder shareholder) {
            this.user = user;
            this.shareholder = shareholder;
        }
        public User getUser() { return user; }
        public Shareholder getShareholder() { return shareholder; }
    }

    public ProfileInfo getProfile(int userId) throws SQLException, ValidationException {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new ValidationException("Khong tim thay tai khoan"));
        Shareholder shareholder = shareholderDAO.findByUserId(userId).orElse(null);
        return new ProfileInfo(user, shareholder);
    }

    /**
     * Doi mat khau tu-phuc-vu: xac thuc mat khau HIEN TAI truoc khi cho doi, tranh truong hop
     * ke gian chiem duoc session (vd may dung chung) tu doi mat khau ma khong biet mat khau that.
     */
    public void changeOwnPassword(int userId, String currentPassword, String newPassword,
                                   String confirmNewPassword, String userAgent)
            throws SQLException, ValidationException {

        if (!ValidationUtil.isPasswordNonEmpty(newPassword)) {
            throw new ValidationException("Mat khau moi khong duoc de trong");
        }
        if (!newPassword.equals(confirmNewPassword)) {
            throw new ValidationException("Xac nhan mat khau moi khong khop");
        }
        if (!ValidationUtil.isStrongPassword(newPassword)) {
            throw new ValidationException("Mat khau moi phai co it nhat 8 ky tu, gom ca chu va so");
        }

        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isEmpty()) throw new ValidationException("Khong tim thay tai khoan");
        User user = userOpt.get();

        if (!PasswordUtil.verify(currentPassword, user.getPasswordHash())) {
            throw new ValidationException("Mat khau hien tai khong dung");
        }
        if (currentPassword.equals(newPassword)) {
            throw new ValidationException("Mat khau moi phai khac mat khau hien tai");
        }

        userDAO.updatePasswordHash(userId, PasswordUtil.hash(newPassword));

        AuditLog log = new AuditLog(AuditAction.UPDATE, EntityType.USER, userId, userId, userAgent);
        auditLogDAO.insert(log);
    }

    /**
     * Cho phep chinh chu tai khoan tu sua thong tin ca nhan (ho ten, CCCD/CMND, SDT, dia chi).
     * CCCD/CMND van duoc kiem tra trung (loai tru chinh ho so nay) de tranh 2 co dong trung so CCCD.
     * Luu y nghiep vu: doi CCCD/CMND anh huong den ho so phap ly xac minh quyen so huu co phan -
     * neu can chat che hon (vd bat buoc ADMIN duyet lai sau khi doi CCCD), can them buoc rieng,
     * hien tai dang cho phep sua truc tiep theo yeu cau.
     */
    public void updateProfile(int userId, String fullName, String citizenId, String phone, String address,
                               String userAgent) throws SQLException, ValidationException {
        if (fullName == null || fullName.isBlank()) {
            throw new ValidationException("Ho ten khong duoc de trong");
        }
        if (!ValidationUtil.isValidCitizenId(citizenId)) {
            throw new ValidationException("So CCCD/CMND khong hop le (9-12 chu so)");
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            throw new ValidationException("So dien thoai khong hop le");
        }

        Shareholder shareholder = shareholderDAO.findByUserId(userId)
                .orElseThrow(() -> new ValidationException("Tai khoan chua co ho so co dong"));

        if (!citizenId.equals(shareholder.getCitizenId()) && shareholderDAO.existsByCitizenId(citizenId)) {
            throw new ValidationException("So CCCD/CMND nay da duoc dung boi co dong khac");
        }

        shareholder.setFullName(ValidationUtil.sanitizeText(fullName, 150));
        shareholder.setCitizenId(citizenId);
        shareholder.setPhone(phone);
        shareholder.setAddress(ValidationUtil.sanitizeText(address, 255));
        shareholderDAO.update(shareholder);

        AuditLog log = new AuditLog(AuditAction.UPDATE, EntityType.USER, userId, userId, userAgent);
        auditLogDAO.insert(log);
    }
}
