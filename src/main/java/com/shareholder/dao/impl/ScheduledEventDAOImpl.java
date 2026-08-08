package com.shareholder.dao.impl;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.ScheduledEventDAO;
import com.shareholder.model.ScheduledEvent;
import com.shareholder.model.enums.EventStatus;
import com.shareholder.model.enums.EventType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ScheduledEventDAOImpl implements ScheduledEventDAO {

    private static final String SELECT_BASE =
            "SELECT event_id, event_key, event_type, issue_id, resolution_id, event_date, status, " +
            "processed_at, created_at FROM SCHEDULED_EVENTS ";

    private ScheduledEvent mapRow(ResultSet rs) throws SQLException {
        ScheduledEvent e = new ScheduledEvent();
        e.setEventId(rs.getInt("event_id"));
        e.setEventKey(rs.getString("event_key"));
        e.setEventType(EventType.valueOf(rs.getString("event_type")));
        int issueId = rs.getInt("issue_id");
        e.setIssueId(rs.wasNull() ? null : issueId);
        int resolutionId = rs.getInt("resolution_id");
        e.setResolutionId(rs.wasNull() ? null : resolutionId);
        Timestamp eventDate = rs.getTimestamp("event_date");
        if (eventDate != null) e.setEventDate(eventDate.toLocalDateTime());
        e.setStatus(EventStatus.valueOf(rs.getString("status")));
        Timestamp processedAt = rs.getTimestamp("processed_at");
        if (processedAt != null) e.setProcessedAt(processedAt.toLocalDateTime());
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) e.setCreatedAt(createdAt.toLocalDateTime());
        return e;
    }

    @Override
    public List<ScheduledEvent> findDuePending() throws SQLException {
        List<ScheduledEvent> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE status = 'PENDING' AND event_date <= ? ORDER BY event_date";
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
    public int insert(Connection conn, ScheduledEvent event) throws SQLException {
        String sql = "INSERT INTO SCHEDULED_EVENTS " +
                "(event_key, event_type, issue_id, resolution_id, event_date, status, created_at) " +
                "VALUES (?, ?, ?, ?, ?, 'PENDING', SYSDATETIME())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, event.getEventKey());
            ps.setString(2, event.getEventType().name());
            if (event.getIssueId() != null) ps.setInt(3, event.getIssueId());
            else ps.setNull(3, Types.INTEGER);
            if (event.getResolutionId() != null) ps.setInt(4, event.getResolutionId());
            else ps.setNull(4, Types.INTEGER);
            ps.setTimestamp(5, Timestamp.valueOf(event.getEventDate()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Khong lay duoc event_id vua tao");
    }

    @Override
    public boolean claimForProcessing(Connection conn, int eventId) throws SQLException {
        String sql = "UPDATE SCHEDULED_EVENTS SET status = 'PROCESSING' WHERE event_id = ? AND status = 'PENDING'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean markStatus(Connection conn, int eventId, EventStatus status) throws SQLException {
        String sql = "UPDATE SCHEDULED_EVENTS SET status = ?, processed_at = SYSDATETIME() WHERE event_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, eventId);
            return ps.executeUpdate() > 0;
        }
    }
}
