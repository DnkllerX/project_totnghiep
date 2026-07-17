package com.shareholder.scheduler;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.*;
import com.shareholder.dao.impl.*;
import com.shareholder.model.AuditLog;
import com.shareholder.model.ScheduledEvent;
import com.shareholder.model.ShareIssueDetail;
import com.shareholder.model.enums.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Xu ly cac SCHEDULED_EVENTS da den event_date va con PENDING.
 * Goi tu SchedulerContextListener theo chu ky co dinh.
 *
 * Quy trinh PROCESS_SHARE_ISSUE (dung schema):
 *   Lay cac SHARE_ISSUE_DETAILS status=ACCEPTED cua issue nay
 *     -> cong received_quantity (= eligible_quantity) vao SHARES
 *     -> insert SHARE_TRANSACTIONS (tx_type=ISSUE, status=COMPLETED)
 *     -> chuyen SHARE_ISSUE_DETAILS status -> COMPLETED
 *   Cac ban ghi con PENDING (khong ky) -> EXPIRED
 *   Insert AUDIT_LOGS (PROCESS_ISSUE)
 *
 * Quy trinh CLOSE_RESOLUTION:
 *   RESOLUTIONS.status -> CLOSED
 *   Insert AUDIT_LOGS (UPDATE_RESOLUTION)
 */
public class ScheduledEventProcessor {

    private static final Logger LOGGER = Logger.getLogger(ScheduledEventProcessor.class.getName());

    private final ScheduledEventDAO scheduledEventDAO = new ScheduledEventDAOImpl();
    private final ShareIssueDetailDAO issueDetailDAO = new ShareIssueDetailDAOImpl();
    private final ShareDAO shareDAO = new ShareDAOImpl();
    private final ShareTransactionDAO shareTransactionDAO = new ShareTransactionDAOImpl();
    private final ResolutionDAO resolutionDAO = new ResolutionDAOImpl();
    private final AuditLogDAO auditLogDAO = new AuditLogDAOImpl();
    private final com.shareholder.service.ShareIssueService shareIssueService =
            new com.shareholder.service.ShareIssueService();

    public void processDueEvents() throws SQLException {
        List<ScheduledEvent> dueEvents = scheduledEventDAO.findDuePending();
        for (ScheduledEvent event : dueEvents) {
            try {
                processOneEvent(event);
            } catch (SQLException e) {
                // 1 event loi khong duoc lam dung ca vong lap - log lai va tiep tuc event khac
                LOGGER.log(Level.SEVERE, "Loi xu ly scheduled event_id=" + event.getEventId(), e);
                markFailedSafely(event.getEventId());
            }
        }
    }

    private void processOneEvent(ScheduledEvent event) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Claim truoc (PENDING -> PROCESSING) de tranh 2 instance scheduler dam vao nhau
            boolean claimed = scheduledEventDAO.claimForProcessing(conn, event.getEventId());
            if (!claimed) {
                conn.rollback();
                return; // event da bi instance khac xu ly truoc
            }

            if (event.getEventType() == EventType.OPEN_SHARE_ISSUE) {
                shareIssueService.openIssueForSigning(conn, event.getIssueId());
            } else if (event.getEventType() == EventType.PROCESS_SHARE_ISSUE) {
                processShareIssue(conn, event.getIssueId());
            } else if (event.getEventType() == EventType.CLOSE_RESOLUTION) {
                closeResolution(conn, event.getResolutionId());
            }

            scheduledEventDAO.markStatus(conn, event.getEventId(), EventStatus.COMPLETED);
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

    private void processShareIssue(Connection conn, int issueId) throws SQLException {
        List<ShareIssueDetail> accepted = issueDetailDAO.findByIssueIdAndStatus(issueId, IssueDetailStatus.ACCEPTED);

        for (ShareIssueDetail detail : accepted) {
            boolean added = shareDAO.addQuantity(conn, detail.getShareholderId(), detail.getEligibleQuantity());
            if (!added) continue; // khong nen xay ra vi ISSUE luon >= 0, nhung phong thu

            com.shareholder.model.ShareTransaction tx = new com.shareholder.model.ShareTransaction();
            tx.setFromShareholderId(null);
            tx.setToShareholderId(detail.getShareholderId());
            tx.setQuantity(detail.getEligibleQuantity());
            tx.setTxType(TxType.ISSUE);
            tx.setStatus(TxStatus.COMPLETED);
            shareTransactionDAO.insert(conn, tx);

            issueDetailDAO.markCompleted(conn, detail.getId());
        }

        // Cac ban ghi khong ky dung han -> EXPIRED
        issueDetailDAO.expireStalePending(conn, issueId);

        AuditLog log = new AuditLog(AuditAction.PROCESS_ISSUE, EntityType.ISSUE, issueId, null,
                "scheduler");
        auditLogDAO.insert(conn, log);
    }

    private void closeResolution(Connection conn, int resolutionId) throws SQLException {
        String sql = "UPDATE RESOLUTIONS SET status = 'CLOSED' WHERE resolution_id = ? AND status = 'OPEN'";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, resolutionId);
            ps.executeUpdate();
        }

        AuditLog log = new AuditLog(AuditAction.UPDATE_RESOLUTION, EntityType.RESOLUTION, resolutionId,
                null, "scheduler");
        auditLogDAO.insert(conn, log);
    }

    /** Best-effort: neu xu ly loi, chuyen event ve FAILED de khong bi ket qua "cham" lap lai vo han. */
    private void markFailedSafely(int eventId) {
        try (Connection conn = DBConnection.getConnection()) {
            scheduledEventDAO.markStatus(conn, eventId, EventStatus.FAILED);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the danh dau FAILED cho event_id=" + eventId, ex);
        }
    }
}
