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
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShareTransferService {

    private final ShareDAO shareDAO = new ShareDAOImpl();
    private final ShareTransactionDAO shareTransactionDAO = new ShareTransactionDAOImpl();
    private final ShareholderDAO shareholderDAO = new ShareholderDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();
    private final AuditLogDAO auditLogDAO = new AuditLogDAOImpl();
    private final EmailService emailService = new EmailService();
    private static final Logger LOGGER = Logger.getLogger(ShareTransferService.class.getName());

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
            notifyTransferCompleted(tx);
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

    /**
     * Gui email cho CA 2 BEN (nguoi chuyen + nguoi nhan) SAU KHI da commit thanh cong.
     * Loi SMTP (neu co) chi duoc log, KHONG duoc anh huong den ket qua duyet chuyen nhuong
     * (nghiep vu chinh - tru/cong co phan - da xong va khong the/khong nen rollback vi ly do email).
     */
    private void notifyTransferCompleted(ShareTransaction tx) {
        try {
            Optional<Shareholder> fromSh = shareholderDAO.findById(tx.getFromShareholderId());
            Optional<Shareholder> toSh = shareholderDAO.findById(tx.getToShareholderId());
            if (fromSh.isEmpty() || toSh.isEmpty()) return;

            Optional<User> fromUser = userDAO.findById(fromSh.get().getUserId());
            Optional<User> toUser = userDAO.findById(toSh.get().getUserId());

            if (fromUser.isPresent()) {
                emailService.sendTransferCompletedEmail(fromUser.get().getEmail(), fromSh.get().getFullName(),
                        "nguoi chuyen", tx.getQuantity(), toSh.get().getFullName());
            }
            if (toUser.isPresent()) {
                emailService.sendTransferCompletedEmail(toUser.get().getEmail(), toSh.get().getFullName(),
                        "nguoi nhan", tx.getQuantity(), fromSh.get().getFullName());
            }
        } catch (SQLException | EmailService.EmailException e) {
            LOGGER.log(Level.WARNING, "Gui email thong bao chuyen nhuong that bai cho txId=" + tx.getTxId(), e);
        }
    }

    public void rejectTransfer(int txId, int actorUserId, String userAgent) throws SQLException {
        shareTransactionDAO.updateStatus(txId, TxStatus.REJECTED);
        AuditLog log = new AuditLog(AuditAction.TRANSFER, EntityType.TRANSACTION, txId, actorUserId, userAgent);
        auditLogDAO.insert(log);
    }
}
