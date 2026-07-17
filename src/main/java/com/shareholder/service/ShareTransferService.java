package com.shareholder.service;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.*;
import com.shareholder.dao.impl.*;
import com.shareholder.model.*;
import com.shareholder.model.enums.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class ShareTransferService {

    private final ShareDAO shareDAO = new ShareDAOImpl();
    private final ShareTransactionDAO shareTransactionDAO = new ShareTransactionDAOImpl();
    private final AuditLogDAO auditLogDAO = new AuditLogDAOImpl();

    public static class ValidationException extends Exception {
        public ValidationException(String message) { super(message); }
    }

    /** Co dong tao yeu cau chuyen nhuong - chi tao ban ghi PENDING, chua dong cham SHARES. */
    public int createTransferRequest(int fromShareholderId, int toShareholderId, int quantity,
                                      int actorUserId, String userAgent) throws SQLException, ValidationException {
        if (fromShareholderId == toShareholderId) {
            throw new ValidationException("Khong the chuyen nhuong cho chinh minh");
        }
        if (quantity <= 0) throw new ValidationException("So luong phai lon hon 0");

        try (Connection conn = DBConnection.getConnection()) {
            ShareTransaction tx = new ShareTransaction();
            tx.setFromShareholderId(fromShareholderId);
            tx.setToShareholderId(toShareholderId);
            tx.setQuantity(quantity);
            tx.setTxType(TxType.TRANSFER);
            tx.setStatus(TxStatus.PENDING);
            int txId = shareTransactionDAO.insert(conn, tx);

            AuditLog log = new AuditLog(AuditAction.TRANSFER, EntityType.TRANSACTION, txId, actorUserId, userAgent);
            auditLogDAO.insert(conn, log);
            return txId;
        }
    }

    /**
     * ADMIN duyet yeu cau. Kiem tra du co phan NGAY TRONG transaction (khong doc truoc roi cap nhat sau)
     * de tranh race condition neu co 2 yeu cau chuyen cung luc.
     */
    public boolean approveTransfer(int txId, int actorUserId, String userAgent)
            throws SQLException, ValidationException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            Optional<ShareTransaction> txOpt = shareTransactionDAO.findById(txId);
            if (txOpt.isEmpty()) throw new ValidationException("Khong tim thay yeu cau chuyen nhuong");
            ShareTransaction tx = txOpt.get();
            if (tx.getStatus() != TxStatus.PENDING) {
                throw new ValidationException("Yeu cau khong o trang thai cho duyet");
            }

            // Tru cua nguoi chuyen - dieu kien "quantity + ? >= 0" trong addQuantity se chan neu khong du
            boolean deducted = shareDAO.addQuantity(conn, tx.getFromShareholderId(), -tx.getQuantity());
            if (!deducted) {
                shareTransactionDAO.updateStatus(txId, TxStatus.REJECTED);
                conn.commit();
                return false; // khong du co phan -> tu dong REJECTED, khong nem loi
            }

            shareDAO.addQuantity(conn, tx.getToShareholderId(), tx.getQuantity());

            String updateStatusSql = "UPDATE SHARE_TRANSACTIONS SET status = 'COMPLETED' WHERE tx_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateStatusSql)) {
                ps.setInt(1, txId);
                ps.executeUpdate();
            }

            AuditLog log = new AuditLog(AuditAction.TRANSFER, EntityType.TRANSACTION, txId, actorUserId, userAgent);
            auditLogDAO.insert(conn, log);

            conn.commit();
            return true;
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

    public void rejectTransfer(int txId, int actorUserId, String userAgent) throws SQLException {
        shareTransactionDAO.updateStatus(txId, TxStatus.REJECTED);
        AuditLog log = new AuditLog(AuditAction.TRANSFER, EntityType.TRANSACTION, txId, actorUserId, userAgent);
        auditLogDAO.insert(log);
    }
}
