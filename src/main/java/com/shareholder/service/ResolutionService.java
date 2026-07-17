package com.shareholder.service;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.*;
import com.shareholder.dao.impl.*;
import com.shareholder.model.*;
import com.shareholder.model.enums.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public class ResolutionService {

    private final ResolutionDAO resolutionDAO = new ResolutionDAOImpl();
    private final ShareSnapshotDAO snapshotDAO = new ShareSnapshotDAOImpl();
    private final ShareSnapshotDetailDAO snapshotDetailDAO = new ShareSnapshotDetailDAOImpl();
    private final ScheduledEventDAO scheduledEventDAO = new ScheduledEventDAOImpl();
    private final VoteDAO voteDAO = new VoteDAOImpl();
    private final ShareholderDAO shareholderDAO = new ShareholderDAOImpl();
    private final AuditLogDAO auditLogDAO = new AuditLogDAOImpl();

    public static class ValidationException extends Exception {
        public ValidationException(String message) { super(message); }
    }

    /**
     * Tao nghi quyet: RESOLUTIONS -> SHARE_SNAPSHOTS (chup danh sach co dong duoc quyen vote)
     * -> SCHEDULED_EVENTS (CLOSE_RESOLUTION luc end_time). Tra ve [resolutionId, snapshotId].
     */
    public int[] createResolutionWithSnapshot(Resolution resolution, int actorUserId, String userAgent)
            throws SQLException, ValidationException {

        if (resolution.getEndTime() == null || resolution.getStartTime() == null
                || !resolution.getEndTime().isAfter(resolution.getStartTime())) {
            throw new ValidationException("end_time phai sau start_time");
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            resolution.setStatus(ResolutionStatus.OPEN);
            int resolutionId = insertResolution(conn, resolution);

            ShareSnapshot snapshot = new ShareSnapshot();
            snapshot.setSnapshotDate(LocalDateTime.now());
            snapshot.setReason("Snapshot cho nghi quyet #" + resolutionId + ": " + resolution.getTitle());
            int snapshotId = snapshotDAO.insert(conn, snapshot);
            snapshotDetailDAO.snapshotAllCurrentShares(conn, snapshotId);

            ScheduledEvent event = new ScheduledEvent();
            event.setEventKey("CLOSE_RESOLUTION_" + resolutionId);
            event.setEventType(EventType.CLOSE_RESOLUTION);
            event.setIssueId(null);
            event.setResolutionId(resolutionId);
            event.setEventDate(resolution.getEndTime());
            scheduledEventDAO.insert(conn, event);

            AuditLog log = new AuditLog(AuditAction.CREATE_RESOLUTION, EntityType.RESOLUTION,
                    resolutionId, actorUserId, userAgent);
            auditLogDAO.insert(conn, log);

            conn.commit();
            return new int[]{resolutionId, snapshotId};
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

    private int insertResolution(Connection conn, Resolution r) throws SQLException {
        String sql = "INSERT INTO RESOLUTIONS (title, description, status, start_time, end_time, created_at) " +
                     "VALUES (?, ?, 'OPEN', ?, ?, SYSDATETIME())";
        try (var ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getTitle());
            ps.setString(2, r.getDescription());
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(r.getStartTime()));
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(r.getEndTime()));
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Khong lay duoc resolution_id vua tao");
    }

    /**
     * Co dong bieu quyet. Rule "1 lan, khong sua": kiem tra hasVoted() truoc (UX tot, thong bao ro rang),
     * nhung lop bao ve THAT SU la UQ_Votes(resolution_id, shareholder_id) o tang DB - neu 2 request
     * cung luc vuot qua check nay, INSERT thu 2 se bi DB tu choi do vi pham UNIQUE constraint.
     */
    public void castVote(int resolutionId, int shareholderId, int snapshotId, VoteValue value,
                          String userAgent) throws SQLException, ValidationException {

        Optional<Resolution> resOpt = resolutionDAO.findById(resolutionId);
        if (resOpt.isEmpty()) throw new ValidationException("Nghi quyet khong ton tai");
        Resolution resolution = resOpt.get();
        if (resolution.getStatus() != ResolutionStatus.OPEN) {
            throw new ValidationException("Nghi quyet da dong, khong the bieu quyet");
        }
        if (voteDAO.hasVoted(resolutionId, shareholderId)) {
            throw new ValidationException("Ban da bieu quyet cho nghi quyet nay roi");
        }

        Vote vote = new Vote();
        vote.setResolutionId(resolutionId);
        vote.setShareholderId(shareholderId);
        vote.setSnapshotId(snapshotId);
        vote.setVoteValue(value);

        try {
            int voteId = voteDAO.insert(vote);
            AuditLog log = new AuditLog(AuditAction.VOTE, EntityType.RESOLUTION, resolutionId,
                    shareholderId, userAgent);
            auditLogDAO.insert(log);
        } catch (SQLException e) {
            // Vi pham UQ_Votes (da vote roi, do race condition) -> bao loi nghiep vu ro rang thay vi 500
            if (isUniqueViolation(e)) {
                throw new ValidationException("Ban da bieu quyet cho nghi quyet nay roi");
            }
            throw e;
        }
    }

    private boolean isUniqueViolation(SQLException e) {
        // SQL Server: 2627 = Violation of UNIQUE KEY constraint, 2601 = Cannot insert duplicate key
        return e.getErrorCode() == 2627 || e.getErrorCode() == 2601;
    }

    public Map<String, Integer> getVoteResult(int resolutionId) throws SQLException {
        return voteDAO.countByResolutionId(resolutionId);
    }
}
