package com.shareholder.dao.impl;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.ShareIssueDetailDAO;
import com.shareholder.model.ShareIssueDetail;
import com.shareholder.model.enums.IssueDetailStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShareIssueDetailDAOImpl implements ShareIssueDetailDAO {

    private static final String SELECT_BASE =
            "SELECT id, issue_id, snapshot_id, shareholder_id, eligible_quantity, received_quantity, " +
            "signature_url, signed_at, status FROM SHARE_ISSUE_DETAILS ";

    private ShareIssueDetail mapRow(ResultSet rs) throws SQLException {
        ShareIssueDetail d = new ShareIssueDetail();
        d.setId(rs.getInt("id"));
        d.setIssueId(rs.getInt("issue_id"));
        d.setSnapshotId(rs.getInt("snapshot_id"));
        d.setShareholderId(rs.getInt("shareholder_id"));
        d.setEligibleQuantity(rs.getInt("eligible_quantity"));
        d.setReceivedQuantity(rs.getInt("received_quantity"));
        d.setSignatureUrl(rs.getString("signature_url"));
        Timestamp signedAt = rs.getTimestamp("signed_at");
        if (signedAt != null) d.setSignedAt(signedAt.toLocalDateTime());
        d.setStatus(IssueDetailStatus.valueOf(rs.getString("status")));
        return d;
    }

    @Override
    public Optional<ShareIssueDetail> findById(int id) throws SQLException {
        String sql = SELECT_BASE + "WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ShareIssueDetail> findByIssueAndShareholder(int issueId, int shareholderId) throws SQLException {
        String sql = SELECT_BASE + "WHERE issue_id = ? AND shareholder_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            ps.setInt(2, shareholderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ShareIssueDetail> findByIssueId(int issueId) throws SQLException {
        List<ShareIssueDetail> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE issue_id = ? ORDER BY shareholder_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<ShareIssueDetail> findByShareholderId(int shareholderId) throws SQLException {
        List<ShareIssueDetail> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE shareholder_id = ? ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, shareholderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<ShareIssueDetail> findByIssueIdAndStatus(int issueId, IssueDetailStatus status) throws SQLException {
        List<ShareIssueDetail> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE issue_id = ? AND status = ? ORDER BY shareholder_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            ps.setString(2, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * eligible_quantity = share_quantity (tai snapshot) * ratio, lam tron xuong (khong phat qua so du).
     * Insert ... SELECT trong 1 cau lenh de tranh vong lap N+1 tren tang ung dung.
     */
    @Override
    public int generateFromSnapshot(Connection conn, int issueId, int snapshotId, BigDecimal ratio)
            throws SQLException {
        String sql = "INSERT INTO SHARE_ISSUE_DETAILS " +
                "(issue_id, snapshot_id, shareholder_id, eligible_quantity, received_quantity, status) " +
                "SELECT ?, snapshot_id, shareholder_id, " +
                "       CAST(FLOOR(share_quantity * ?) AS INT), 0, 'PENDING' " +
                "FROM SHARE_SNAPSHOT_DETAILS WHERE snapshot_id = ? AND share_quantity > 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            ps.setBigDecimal(2, ratio.setScale(6, RoundingMode.DOWN));
            ps.setInt(3, snapshotId);
            return ps.executeUpdate();
        }
    }

    @Override
    public boolean signAccept(int id, int shareholderId, String signatureUrl) throws SQLException {
        // dieu kien "shareholder_id = ?" va "status = 'PENDING'" dam bao khong cho nguoi khac ky ho
        // hoac ky lai ban ghi da xu ly - chan IDOR / race condition o tang DB
        String sql = "UPDATE SHARE_ISSUE_DETAILS SET signature_url = ?, signed_at = SYSDATETIME(), " +
                     "status = 'ACCEPTED' WHERE id = ? AND shareholder_id = ? AND status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, signatureUrl);
            ps.setInt(2, id);
            ps.setInt(3, shareholderId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean markCompleted(Connection conn, int id) throws SQLException {
        String sql = "UPDATE SHARE_ISSUE_DETAILS SET received_quantity = eligible_quantity, status = 'COMPLETED' " +
                     "WHERE id = ? AND status = 'ACCEPTED'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public int expireStalePending(Connection conn, int issueId) throws SQLException {
        String sql = "UPDATE SHARE_ISSUE_DETAILS SET status = 'EXPIRED' WHERE issue_id = ? AND status = 'PENDING'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            return ps.executeUpdate();
        }
    }
}
