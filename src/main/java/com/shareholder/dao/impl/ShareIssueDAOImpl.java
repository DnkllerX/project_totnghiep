package com.shareholder.dao.impl;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.ShareIssueDAO;
import com.shareholder.model.ShareIssue;
import com.shareholder.model.enums.IssueType;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShareIssueDAOImpl implements ShareIssueDAO {

    private static final String SELECT_BASE =
            "SELECT issue_id, title, issue_type, issue_date, snapshot_date, share_quantity, issue_ratio, " +
            "start_date, end_date, description, created_at FROM SHARE_ISSUES ";

    private ShareIssue mapRow(ResultSet rs) throws SQLException {
        ShareIssue i = new ShareIssue();
        i.setIssueId(rs.getInt("issue_id"));
        i.setTitle(rs.getString("title"));
        i.setIssueType(IssueType.valueOf(rs.getString("issue_type")));
        Date issueDate = rs.getDate("issue_date");
        if (issueDate != null) i.setIssueDate(issueDate.toLocalDate());
        Timestamp snapshotDate = rs.getTimestamp("snapshot_date");
        if (snapshotDate != null) i.setSnapshotDate(snapshotDate.toLocalDateTime());
        i.setShareQuantity(rs.getInt("share_quantity"));
        BigDecimal ratio = rs.getBigDecimal("issue_ratio");
        i.setIssueRatio(ratio);
        Timestamp start = rs.getTimestamp("start_date");
        if (start != null) i.setStartDate(start.toLocalDateTime());
        Timestamp end = rs.getTimestamp("end_date");
        if (end != null) i.setEndDate(end.toLocalDateTime());
        i.setDescription(rs.getString("description"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) i.setCreatedAt(created.toLocalDateTime());
        return i;
    }

    @Override
    public Optional<ShareIssue> findById(int issueId) throws SQLException {
        String sql = SELECT_BASE + "WHERE issue_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, issueId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ShareIssue> findAll() throws SQLException {
        List<ShareIssue> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY start_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<ShareIssue> findEndedNotProcessed() throws SQLException {
        List<ShareIssue> list = new ArrayList<>();
        String sql = SELECT_BASE +
                "WHERE end_date <= ? AND NOT EXISTS ( " +
                "  SELECT 1 FROM SCHEDULED_EVENTS se " +
                "  WHERE se.issue_id = SHARE_ISSUES.issue_id " +
                "    AND se.event_type = 'PROCESS_SHARE_ISSUE' " +
                "    AND se.status = 'COMPLETED' " +
                ") ORDER BY end_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public int insert(Connection conn, ShareIssue issue) throws SQLException {
        String sql = "INSERT INTO SHARE_ISSUES " +
                "(title, issue_type, issue_date, snapshot_date, share_quantity, issue_ratio, " +
                "start_date, end_date, description, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATETIME())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, issue.getTitle());
            ps.setString(2, issue.getIssueType().name());
            ps.setDate(3, Date.valueOf(issue.getIssueDate()));
            ps.setTimestamp(4, Timestamp.valueOf(issue.getSnapshotDate()));
            ps.setInt(5, issue.getShareQuantity());
            if (issue.getIssueRatio() != null) ps.setBigDecimal(6, issue.getIssueRatio());
            else ps.setNull(6, Types.DECIMAL);
            ps.setTimestamp(7, Timestamp.valueOf(issue.getStartDate()));
            ps.setTimestamp(8, Timestamp.valueOf(issue.getEndDate()));
            ps.setString(9, issue.getDescription());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Khong lay duoc issue_id vua tao");
    }
}
