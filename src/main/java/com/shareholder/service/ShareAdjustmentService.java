package com.shareholder.service;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.*;
import com.shareholder.dao.impl.*;
import com.shareholder.model.*;
import com.shareholder.model.enums.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class ShareAdjustmentService {

    private final ShareDAO shareDAO = new ShareDAOImpl();
    private final ShareTransactionDAO shareTransactionDAO = new ShareTransactionDAOImpl();
    private final ShareAdjustmentLogDAO adjustmentLogDAO = new ShareAdjustmentLogDAOImpl();
    private final AuditLogDAO auditLogDAO = new AuditLogDAOImpl();

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
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            Optional<Share> currentOpt = shareDAO.findByShareholderId(shareholderId);
            if (currentOpt.isEmpty()) throw new ValidationException("Co dong khong ton tai so du co phan");
            int oldQuantity = currentOpt.get().getQuantity();

            if (oldQuantity == newQuantity) {
                conn.rollback();
                return; // khong co gi thay doi, khong tao log/tx rac
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
    }
}
