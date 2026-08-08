package com.shareholder.service;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.*;
import com.shareholder.dao.impl.*;
import com.shareholder.model.*;
import com.shareholder.model.enums.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShareAdjustmentService {

    private static final Logger LOGGER = Logger.getLogger(ShareAdjustmentService.class.getName());

    private final ShareDAO shareDAO = new ShareDAOImpl();
    private final ShareTransactionDAO shareTransactionDAO = new ShareTransactionDAOImpl();
    private final ShareAdjustmentLogDAO adjustmentLogDAO = new ShareAdjustmentLogDAOImpl();
    private final AuditLogDAO auditLogDAO = new AuditLogDAOImpl();
    private final ShareholderDAO shareholderDAO = new ShareholderDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();
    private final EmailService emailService = new EmailService();

    public static class ValidationException extends Exception {
        public ValidationException(String message) { super(message); }
    }

    /**
     * ADMIN nhap so co phan MOI (khong phai delta) cho 1 co dong.
     * Quy trinh (1 transaction): update SHARES -> insert SHARE_TRANSACTIONS (ADJUSTMENT)
     *                              -> insert SHARE_ADJUSTMENT_LOGS -> insert AUDIT_LOGS
     */
    public void adjustShareQuantity(int shareholderId, int newQuantity, String reason,
                                     int actorUserId, String userAgent) throws SQLException, ValidationException {
        if (newQuantity < 0) throw new ValidationException("So co phan moi khong the am");
        if (reason == null || reason.isBlank()) throw new ValidationException("Phai nhap ly do dieu chinh");

        Connection conn = null;
        int oldQuantity;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Doc trong CUNG transaction + khoa dong (WITH UPDLOCK, ROWLOCK) - truoc day
            // findByShareholderId(shareholderId) tu mo 1 connection RIENG, tach roi hoan toan
            // khoi transaction ghi ben duoi, la khe ho race condition thuc su (khong chi la
            // "khong lock dong" ma con la 2 connection khac nhau, khong co gi rang buoc lien mach).
            Optional<Share> currentOpt = shareDAO.findByShareholderIdForUpdate(conn, shareholderId);
            if (currentOpt.isEmpty()) throw new ValidationException("Co dong khong ton tai so du co phan");
            oldQuantity = currentOpt.get().getQuantity();

            if (oldQuantity == newQuantity) {
                conn.rollback();
                return; // khong co gi thay doi, khong tao log/tx rac - cung khong gui email
            }

            boolean updated = shareDAO.setQuantity(conn, shareholderId, newQuantity);
            if (!updated) throw new SQLException("Cap nhat SHARES that bai");

            ShareTransaction tx = new ShareTransaction();
            tx.setFromShareholderId(null);
            tx.setToShareholderId(shareholderId);
            tx.setQuantity(Math.abs(newQuantity - oldQuantity));
            tx.setTxType(TxType.ADJUSTMENT);
            tx.setStatus(TxStatus.COMPLETED);
            shareTransactionDAO.insert(conn, tx);

            ShareAdjustmentLog log = new ShareAdjustmentLog();
            log.setShareholderId(shareholderId);
            log.setOldValue(oldQuantity);
            log.setNewValue(newQuantity);
            log.setReason(reason);
            log.setAdjustedBy(actorUserId);
            adjustmentLogDAO.insert(conn, log);

            AuditLog audit = new AuditLog(AuditAction.ADJUST_SHARE, EntityType.SHARE, shareholderId,
                    actorUserId, userAgent);
            auditLogDAO.insert(conn, audit);

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

        // Gui email SAU KHI commit thanh cong, NGOAI transaction DB (email cham/loi khong duoc
        // lam rollback thay doi so co phan da luu). Doi ten hien thi + email tu USERS/SHAREHOLDERS -
        // khong throw ra ngoai neu that bai, chi log lai (giong tinh than xuyen suot he thong:
        // khong de loi email lam fail nghiep vu chinh).
        try {
            Optional<Shareholder> shOpt = shareholderDAO.findById(shareholderId);
            if (shOpt.isPresent()) {
                Shareholder sh = shOpt.get();
                Optional<User> userOpt = userDAO.findById(sh.getUserId());
                if (userOpt.isPresent()) {
                    emailService.sendShareAdjustmentEmail(
                            userOpt.get().getEmail(), sh.getFullName(), oldQuantity, newQuantity, reason);
                }
            }
        } catch (SQLException | EmailService.EmailException e) {
            LOGGER.log(Level.WARNING, "Gui email thong bao dieu chinh co phan that bai cho shareholderId=" + shareholderId, e);
        }
    }
}
