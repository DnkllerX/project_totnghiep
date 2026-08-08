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
import com.shareholder.model.enums.UserRole;
import com.shareholder.model.enums.UserSortOption;
import com.shareholder.model.enums.UserStatus;
import com.shareholder.util.PasswordUtil;
import com.shareholder.util.ValidationUtil;
import com.shareholder.util.JwtUtil;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Nghiep vu danh cho IT: tao/khoa/mo khoa/reset mat khau tai khoan.
 * IT KHONG duoc dieu chinh co phan, tao phat hanh, bieu quyet, bao cao tai chinh (theo bang phan quyen)
 * - cac rule nay duoc AuthFilter chan o tang duong dan, o day chi tap trung nghiep vu tai khoan.
 */
public class UserAccountService {

    private final UserDAO userDAO = new UserDAOImpl();
    private final ShareholderDAO shareholderDAO = new ShareholderDAOImpl();
    private final AuditLogDAO auditLogDAO = new AuditLogDAOImpl();
    private final EmailService emailService = new EmailService();
    private final SecureRandom secureRandom = new SecureRandom();
    private static final Logger LOGGER = Logger.getLogger(UserAccountService.class.getName());

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
        // BUG DA SUA: ban dau chi chan role == IT ("leo thang ngang hang"), nhung LAI DE HO
        // role == ADMIN ("leo thang doc/vertical privilege escalation") - la huong nguy hiem HON
        // NHIEU vi ADMIN la quyen cao nhat he thong. Kiem tra dung phai la ROLE CUA ACTOR (nguoi
        // dang thuc hien), khong phai chi nhin vao role muon gan: neu actor la IT thi KHONG duoc
        // tao tai khoan ADMIN lan IT (chi duoc tao SHAREHOLDER). Neu actor la ADMIN thi khong bi
        // gioi han nay (ADMIN la quyen cao nhat, tu no tao ADMIN/IT khac la hop le).
        User actor = userDAO.findById(actorUserId)
                .orElseThrow(() -> new ValidationException("Khong tim thay tai khoan thuc hien"));
        if (actor.getRole() == UserRole.IT && role != UserRole.SHAREHOLDER) {
            throw new ValidationException("Tai khoan IT chi duoc tao tai khoan SHAREHOLDER tu man hinh nay");
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

        // Chan IT sua username/email/role cua tai khoan ADMIN hoac IT (bao gom ca chinh minh) -
        // day la duong leo thang nguy hiem HON CA reset mat khau: neu IT tu do doi duoc EMAIL cua
        // ADMIN thanh email IT kiem soat, thi chi can bam "Quen mat khau" o trang login la chiem
        // duoc ADMIN ngay, khong can qua co che xac nhan step-up cua reset-password/lock nua. Chan
        // cung (khong cho sua) o day, khac voi reset-password/lock (cho phep nhung phai qua email
        // xac nhan cua chinh chu) vi sua email/role KHONG the "xac nhan qua email cu" mot cach an
        // toan (chinh email la thu dang bi thay doi).
        User actor = userDAO.findById(actorUserId)
                .orElseThrow(() -> new ValidationException("Khong tim thay tai khoan thuc hien"));
        if (actor.getRole() == UserRole.IT
                && (current.getRole() == UserRole.ADMIN || current.getRole() == UserRole.IT)) {
            throw new ValidationException(
                    "Tai khoan IT khong duoc phep sua thong tin (username/email/quyen) cua tai khoan ADMIN hoac IT");
        }

        // BUG DA SUA: ban dau chi chan gan role == IT, de ho role == ADMIN - dung y het loi da sua
        // o createAccount() (chan nham huong "leo thang ngang hang" IT->IT, bo sot huong nguy hiem
        // hon la "leo thang doc" IT->ADMIN). Neu actor la IT, target sau khi sua PHAI van la
        // SHAREHOLDER (khong duoc doi thanh ADMIN hoac IT tu man hinh nay, bat ke truoc do la gi).
        if (actor.getRole() == UserRole.IT && role != UserRole.SHAREHOLDER) {
            throw new ValidationException("Tai khoan IT khong duoc gan quyen ADMIN hoac IT tu man hinh nay");
        }

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

    /**
     * Luu y nghiep vu: khoa tai khoan chi doi status, khong lien quan gi den role.
     * Gui email thong bao TRUC TIEP cho chinh nguoi dung (khong can ho xac nhan gi ca) - khac voi
     * truong hop khoa ADMIN/IT (phai qua checkStepUp()/confirmPendingAction(), xem ben duoi), vi
     * day khong phai tinh huong leo thang dac quyen, chi la thong bao thong thuong.
     */
    public void lockAccount(int targetUserId, int actorUserId, String userAgent) throws SQLException {
        userDAO.updateStatus(targetUserId, UserStatus.LOCKED);
        logAudit(actorUserId, AuditAction.UPDATE, targetUserId, userAgent);

        userDAO.findById(targetUserId).ifPresent(target -> {
            try {
                emailService.sendAccountLockedEmail(target.getEmail(), resolveDisplayName(target), target.getUsername());
            } catch (EmailService.EmailException | SQLException e) {
                LOGGER.log(Level.WARNING, "Gui email thong bao khoa tai khoan that bai cho userId=" + targetUserId, e);
                // Khong throw - khoa tai khoan van co hieu luc du gui email that bai, chi la
                // nguoi dung se khong duoc bao truoc (giong tinh than "khong lam fail nghiep vu
                // chinh chi vi email" da ap dung xuyen suot he thong).
            }
        });
    }

    private String resolveDisplayName(User user) throws SQLException {
        return shareholderDAO.findByUserId(user.getUserId())
                .map(Shareholder::getFullName).orElse(user.getUsername());
    }

    public void unlockAccount(int targetUserId, int actorUserId, String userAgent) throws SQLException {
        userDAO.updateStatus(targetUserId, UserStatus.ACTIVE);
        logAudit(actorUserId, AuditAction.UPDATE, targetUserId, userAgent);
    }

    // =========================================================================================
    // STEP-UP CONFIRMATION (action-level MFA) - chan IT leo thang chiem ADMIN/IT khac
    // =========================================================================================
    // Bug da sua: truoc day resetPassword()/lockAccount() khong kiem tra role cua targetUserId,
    // nen 1 tai khoan IT co the dat lai mat khau HOAC khoa BAT KY tai khoan ADMIN/IT nao khac ma
    // KHONG can biet mat khau cu - neu SMTP loi, code fallback con hien thang mat khau tam len man
    // hinh cua chinh IT do (chiem quyen ADMIN ngay lap tuc).
    //
    // Cach sua: neu actor la IT va target la ADMIN/IT khac, KHONG thuc hien ngay - thay vao do
    // sinh 1 token va gui link xac nhan toi EMAIL CUA CHINH TARGET (khong phai actor). Hanh dong
    // chi thuc su co hieu luc khi target tu bam xac nhan (xem confirmPendingAction()). Day la dang
    // rut gon cua step-up authentication, chi ap dung dung cho thao tac rui ro cao (IT -> ADMIN/IT),
    // giu nguyen trai nghiem tuc thi cho thao tac it rui ro (IT -> SHAREHOLDER).
    //
    // GIOI HAN DA BIET: neu chinh chu ADMIN mat quyen truy cap CA web lan email cung luc (vd nghi
    // viec dot xuat, mat thiet bi), luong nay bi ket hoan toan - can quy trinh xu ly thu cong qua
    // DB truc tiep (ngoai pham vi code, co ghi log rieng ben ngoai he thong).

    public enum PendingAction { RESET_PASSWORD, LOCK }

    /** Ket qua goi resetPassword()/requestLock(): pendingConfirmation=true nghia la CHUA co gi thay doi that su, chi moi gui email xac nhan. */
    public static class StepUpOutcome {
        private final boolean pendingConfirmation;
        public StepUpOutcome(boolean pendingConfirmation) { this.pendingConfirmation = pendingConfirmation; }
        public boolean isPendingConfirmation() { return pendingConfirmation; }
    }

    /** Token + thong tin can thiet de Servlet (co HttpServletRequest) tu dung link va goi EmailService. */
    public static class StepUpEmailInfo {
        public final String token;
        public final String targetEmail;
        public final String targetDisplayName;
        public final String actorUsername;
        public final String actionLabelVi;
        public StepUpEmailInfo(String token, String targetEmail, String targetDisplayName,
                                String actorUsername, String actionLabelVi) {
            this.token = token;
            this.targetEmail = targetEmail;
            this.targetDisplayName = targetDisplayName;
            this.actorUsername = actorUsername;
            this.actionLabelVi = actionLabelVi;
        }
    }

    /** True neu actor la IT va target la ADMIN hoac IT (bao gom ca 2 tai khoan IT thao tac len nhau). */
    private boolean requiresStepUp(User actor, User target) {
        return actor.getRole() == UserRole.IT
                && (target.getRole() == UserRole.ADMIN || target.getRole() == UserRole.IT);
    }

    /**
     * Kiem tra xem thao tac (RESET_PASSWORD/LOCK) len targetUserId co can xac nhan qua email
     * truoc khi thuc hien khong. Neu can, sinh san token + thong tin de servlet gui email, KHONG
     * dong thoi thuc hien gi ca (chua doi mat khau, chua khoa). Neu khong can, tra ve Optional.empty()
     * - ben goi (servlet) tu goi tiep resetPassword()/lockAccount() nhu binh thuong.
     */
    public Optional<StepUpEmailInfo> checkStepUp(PendingAction action, int targetUserId, int actorUserId)
            throws SQLException, ValidationException {
        if (action == PendingAction.LOCK && targetUserId == actorUserId) {
            throw new ValidationException("Ban khong the tu khoa chinh tai khoan cua minh");
        }
        User actor = userDAO.findById(actorUserId)
                .orElseThrow(() -> new ValidationException("Khong tim thay tai khoan thuc hien"));
        User target = userDAO.findById(targetUserId)
                .orElseThrow(() -> new ValidationException("Khong tim thay tai khoan"));

        if (!requiresStepUp(actor, target)) return Optional.empty();

        String anchor = action == PendingAction.RESET_PASSWORD
                ? target.getPasswordHash()
                : target.getStatus().name();
        String token = JwtUtil.generateActionConfirmToken(targetUserId, actorUserId, action.name(), anchor);

        String displayName = shareholderDAO.findByUserId(targetUserId)
                .map(Shareholder::getFullName).orElse(target.getUsername());
        String actionLabelVi = action == PendingAction.RESET_PASSWORD ? "dat lai mat khau" : "khoa";

        return Optional.of(new StepUpEmailInfo(token, target.getEmail(), displayName, actor.getUsername(), actionLabelVi));
    }

    /**
     * Xac nhan + THUC SU thuc hien hanh dong dang cho (goi tu servlet xu ly link trong email, luc
     * chinh chu target bam xac nhan). anchorClaim trong token duoc so sanh voi gia tri HIEN TAI
     * trong DB - neu da doi khac di (vd token da dung 1 lan, hoac status da doi tu luc gui email),
     * token tu dong het hieu luc du chua qua 30 phut.
     */
    public void confirmPendingAction(String token, String userAgent) throws SQLException, ValidationException {
        JwtUtil.ActionConfirmPayload payload = JwtUtil.parseActionConfirmToken(token);
        if (payload == null) {
            throw new ValidationException("Link xac nhan khong hop le hoac da het han");
        }
        User target = userDAO.findById(payload.targetUserId)
                .orElseThrow(() -> new ValidationException("Khong tim thay tai khoan"));

        if ("RESET_PASSWORD".equals(payload.action)) {
            if (!payload.anchorClaim.equals(target.getPasswordHash())) {
                throw new ValidationException("Link nay da duoc su dung hoac khong con hop le");
            }
            byte[] randomBytes = new byte[9];
            secureRandom.nextBytes(randomBytes);
            String tempPassword = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
            userDAO.updatePasswordHash(target.getUserId(), PasswordUtil.hash(tempPassword));
            logAudit(payload.actorUserId, AuditAction.UPDATE, target.getUserId(), userAgent);

            String displayName = shareholderDAO.findByUserId(target.getUserId())
                    .map(Shareholder::getFullName).orElse(target.getUsername());
            try {
                emailService.sendPasswordResetEmail(target.getEmail(), displayName, target.getUsername(), tempPassword);
            } catch (EmailService.EmailException e) {
                // Khong con IT nao dang xem man hinh o day de fallback hien mat khau tam (day la
                // link tu chinh target bam, khong phai request tu servlet co session cua admin).
                // Ghi log de IT chu dong lien he target neu can, KHONG the lam gi khac o day.
                LOGGER.log(Level.WARNING,
                        "Xac nhan reset mat khau thanh cong nhung gui email that bai cho userId="
                        + target.getUserId() + " - can IT lien he truc tiep de cung cap mat khau tam.", e);
            }
        } else if ("LOCK".equals(payload.action)) {
            if (!payload.anchorClaim.equals(target.getStatus().name())) {
                throw new ValidationException("Link nay da duoc su dung hoac khong con hop le");
            }
            userDAO.updateStatus(target.getUserId(), UserStatus.LOCKED);
            logAudit(payload.actorUserId, AuditAction.UPDATE, target.getUserId(), userAgent);
        } else {
            throw new ValidationException("Loai hanh dong khong hop le");
        }
    }

    /** Ket qua reset mat khau: neu email gui thanh cong, KHONG lo mat khau cho admin xem qua man hinh. */
    public static class ResetPasswordResult {
        private final boolean emailSent;
        private final String tempPasswordFallback; // chi khac null khi gui email that bai

        public ResetPasswordResult(boolean emailSent, String tempPasswordFallback) {
            this.emailSent = emailSent;
            this.tempPasswordFallback = tempPasswordFallback;
        }
        public boolean isEmailSent() { return emailSent; }
        public String getTempPasswordFallback() { return tempPasswordFallback; }
    }

    /**
     * Sinh mat khau tam ngau nhien (khong doan duoc) va GUI QUA EMAIL cho chinh nguoi dung, thay vi
     * hien thi cho admin xem - tranh truong hop admin/IT biet duoc mat khau cua nguoi khac.
     * Neu SMTP loi (vd chua cau hinh, mail server down), fallback: tra ve mat khau tam de admin con
     * cach cung cap cho nguoi dung (vd doc qua dien thoai), tranh khoa nguoi dung hoan toan.
     */
    public ResetPasswordResult resetPassword(int targetUserId, int actorUserId, String userAgent)
            throws SQLException, ValidationException {
        User user = userDAO.findById(targetUserId)
                .orElseThrow(() -> new ValidationException("Khong tim thay tai khoan"));

        byte[] randomBytes = new byte[9];
        secureRandom.nextBytes(randomBytes);
        String tempPassword = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        userDAO.updatePasswordHash(targetUserId, PasswordUtil.hash(tempPassword));
        logAudit(actorUserId, AuditAction.UPDATE, targetUserId, userAgent);

        String displayName = shareholderDAO.findByUserId(targetUserId)
                .map(Shareholder::getFullName).orElse(user.getUsername());

        try {
            emailService.sendPasswordResetEmail(user.getEmail(), displayName, user.getUsername(), tempPassword);
            return new ResetPasswordResult(true, null);
        } catch (EmailService.EmailException e) {
            LOGGER.log(Level.WARNING, "Gui email reset mat khau that bai cho userId=" + targetUserId, e);
            return new ResetPasswordResult(false, tempPassword);
        }
    }

    private void logAudit(int actorUserId, AuditAction action, int targetUserId, String userAgent)
            throws SQLException {
        AuditLog log = new AuditLog(action, EntityType.USER, targetUserId, actorUserId, userAgent);
        auditLogDAO.insert(log);
    }
}
