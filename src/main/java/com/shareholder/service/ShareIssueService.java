package com.shareholder.service;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.*;
import com.shareholder.dao.impl.*;
import com.shareholder.model.*;
import com.shareholder.model.enums.*;
import com.shareholder.util.SignatureUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Luong: ADMIN tao dot phat hanh (createIssue) chi luu SHARE_ISSUES + lich 2 su kien:
 *   OPEN_SHARE_ISSUE luc snapshot_date  -> Scheduler goi openIssueForSigning() de chot snapshot + sinh SHARE_ISSUE_DETAILS
 *   PROCESS_SHARE_ISSUE luc end_date    -> Scheduler cong co phan cho cac ban ghi ACCEPTED (khong doi, xem ScheduledEventProcessor)
 */
public class ShareIssueService {

    private final ShareIssueDAO shareIssueDAO = new ShareIssueDAOImpl();
    private final ShareSnapshotDAO shareSnapshotDAO = new ShareSnapshotDAOImpl();
    private final ShareSnapshotDetailDAO snapshotDetailDAO = new ShareSnapshotDetailDAOImpl();
    private final ShareIssueDetailDAO issueDetailDAO = new ShareIssueDetailDAOImpl();
    private final ScheduledEventDAO scheduledEventDAO = new ScheduledEventDAOImpl();
    private final AuditLogDAO auditLogDAO = new AuditLogDAOImpl();

    /** Thu muc goc luu anh chu ky, nam NGOAI webapp (khong truy cap truc tiep qua URL cong khai). */
    private static final String SIGNATURE_STORAGE_ROOT = System.getProperty(
            "signature.storage.root", "/var/shareholder-system/signatures");

    public static class ValidationException extends Exception {
        public ValidationException(String message) { super(message); }
    }

    /**
     * Tao dot phat hanh/co tuc: chi insert SHARE_ISSUES + 2 SCHEDULED_EVENTS (OPEN_SHARE_ISSUE luc
     * snapshot_date, PROCESS_SHARE_ISSUE luc end_date). KHONG chup snapshot ngay - viec nay se do
     * Scheduler thuc hien dung luc snapshot_date (xem openIssueForSigning). Tat ca trong 1 transaction.
     */
    public int createIssue(ShareIssue issue, int actorUserId, String userAgent)
            throws SQLException, ValidationException {

        if (!issue.isPriceValid()) {
            throw new ValidationException(
                    "issue_price khong hop le: DIVIDEND phai NULL, ISSUE phai > 0");
        }
        if (issue.getIssueType() == IssueType.DIVIDEND && issue.getIssueRatio() == null) {
            throw new ValidationException("Co tuc (DIVIDEND) phai co ty le phat hanh (issue_ratio)");
        }
        if (!issue.isDateOrderValid()) {
            throw new ValidationException(
                    "Thu tu ngay khong hop le: issue_date <= snapshot_date <= start_date <= end_date");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int issueId = shareIssueDAO.insert(conn, issue);

            ScheduledEvent openEvent = new ScheduledEvent();
            openEvent.setEventKey("OPEN_ISSUE_" + issueId);
            openEvent.setEventType(EventType.OPEN_SHARE_ISSUE);
            openEvent.setIssueId(issueId);
            openEvent.setResolutionId(null);
            openEvent.setEventDate(issue.getSnapshotDate());
            scheduledEventDAO.insert(conn, openEvent);

            ScheduledEvent processEvent = new ScheduledEvent();
            processEvent.setEventKey("PROCESS_ISSUE_" + issueId);
            processEvent.setEventType(EventType.PROCESS_SHARE_ISSUE);
            processEvent.setIssueId(issueId);
            processEvent.setResolutionId(null);
            processEvent.setEventDate(issue.getEndDate());
            scheduledEventDAO.insert(conn, processEvent);

            AuditLog log = new AuditLog(AuditAction.CREATE_ISSUE, EntityType.ISSUE, issueId,
                    actorUserId, userAgent);
            auditLogDAO.insert(conn, log);

            conn.commit();
            return issueId;
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
     * Goi boi Scheduler dung luc snapshot_date (event OPEN_SHARE_ISSUE): chot SHARE_SNAPSHOTS +
     * SHARE_SNAPSHOT_DETAILS + sinh SHARE_ISSUE_DETAILS (PENDING) theo ty le phat hanh. Nam trong
     * transaction cua ScheduledEventProcessor (dung chung Connection).
     */
    public void openIssueForSigning(Connection conn, int issueId) throws SQLException {
        Optional<ShareIssue> issueOpt = shareIssueDAO.findById(issueId);
        if (issueOpt.isEmpty()) return; // khong nen xay ra vi FK, nhung phong thu
        ShareIssue issue = issueOpt.get();

        ShareSnapshot snapshot = new ShareSnapshot();
        snapshot.setSnapshotDate(LocalDateTime.now());
        snapshot.setReason("Snapshot cho dot phat hanh #" + issueId + ": " + issue.getTitle());
        int snapshotId = shareSnapshotDAO.insert(conn, snapshot);

        snapshotDetailDAO.snapshotAllCurrentShares(conn, snapshotId);

        BigDecimal ratio = issue.getIssueRatio() != null ? issue.getIssueRatio() : BigDecimal.ONE;
        issueDetailDAO.generateFromSnapshot(conn, issueId, snapshotId, ratio);
    }

    /**
     * Co dong ky nhan bang chu ky TAY (canvas -> base64 -> decode -> luu file -> luu duong dan).
     * signAccept() o DAO da tu chan: khong cho ky ho nguoi khac, khong cho ky lai ban ghi da xu ly.
     * Ngoai ra chan ky ngoai khoang [start_date, end_date] cua dot phat hanh (khop nghiep vu
     * "Cho phep ky (start_date -> end_date)").
     */
    public boolean signIssueDetail(int issueDetailId, int shareholderId, String base64Signature,
                                    String userAgent)
            throws SQLException, ValidationException {

        Optional<ShareIssueDetail> detailOpt = issueDetailDAO.findById(issueDetailId);
        if (detailOpt.isEmpty()) throw new ValidationException("Khong tim thay ban ghi ky nhan");
        ShareIssueDetail detail = detailOpt.get();

        if (detail.getShareholderId() != shareholderId) {
            throw new ValidationException("Ban khong co quyen ky ban ghi nay");
        }
        if (detail.getStatus() != IssueDetailStatus.PENDING) {
            throw new ValidationException("Ban ghi nay khong o trang thai cho ky (co the da ky hoac da het han)");
        }

        Optional<ShareIssue> issueOpt = shareIssueDAO.findById(detail.getIssueId());
        if (issueOpt.isEmpty()) throw new ValidationException("Dot phat hanh khong ton tai");
        ShareIssue issue = issueOpt.get();
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(issue.getStartDate()) || now.isAfter(issue.getEndDate())) {
            throw new ValidationException("Ngoai thoi gian cho phep ky (tu " + issue.getStartDate()
                    + " den " + issue.getEndDate() + ")");
        }

        String signatureUrl;
        try {
            signatureUrl = SignatureUtil.saveHandwrittenSignature(
                    base64Signature, SIGNATURE_STORAGE_ROOT, detail.getIssueId(), shareholderId);
        } catch (SignatureUtil.InvalidSignatureException e) {
            throw new ValidationException("Chu ky khong hop le: " + e.getMessage());
        } catch (java.io.IOException e) {
            throw new SQLException("Loi luu file chu ky: " + e.getMessage(), e);
        }

        boolean signed = issueDetailDAO.signAccept(issueDetailId, shareholderId, signatureUrl);
        if (signed) {
            AuditLog log = new AuditLog(AuditAction.SIGN_ISSUE, EntityType.ISSUE, issueDetailId,
                    shareholderId, userAgent);
            auditLogDAO.insert(log);
        }
        return signed;
    }
}
