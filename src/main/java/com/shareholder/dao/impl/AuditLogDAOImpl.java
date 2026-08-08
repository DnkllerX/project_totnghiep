package com.shareholder.dao.impl;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.AuditLogDAO;
import com.shareholder.model.AuditLog;
import com.shareholder.model.enums.AuditAction;
import com.shareholder.model.enums.EntityType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAOImpl implements AuditLogDAO {

    private static final String SELECT_BASE =
            "SELECT log_id, user_id, action, entity_type, entity_id, user_agent, created_at FROM AUDIT_LOGS ";

    private static final String INSERT_SQL =
            "INSERT INTO AUDIT_LOGS (user_id, action, entity_type, entity_id, user_agent, created_at) " +
            "VALUES (?, ?, ?, ?, ?, SYSDATETIME())";

    private AuditLog mapRow(ResultSet rs) throws SQLException {
        AuditLog l = new AuditLog();
        l.setLogId(rs.getInt("log_id"));
        int uid = rs.getInt("user_id");
        l.setUserId(rs.wasNull() ? null : uid);
        l.setAction(AuditAction.valueOf(rs.getString("action")));
        l.setEntityType(EntityType.valueOf(rs.getString("entity_type")));
        int eid = rs.getInt("entity_id");
        l.setEntityId(rs.wasNull() ? null : eid);
        l.setUserAgent(rs.getString("user_agent"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) l.setCreatedAt(ts.toLocalDateTime());
        return l;
    }

    private void bindParams(PreparedStatement ps, AuditLog log) throws SQLException {
        if (log.getUserId() != null) ps.setInt(1, log.getUserId());
        else ps.setNull(1, Types.INTEGER);
        ps.setString(2, log.getAction().name());
        ps.setString(3, log.getEntityType().name());
        if (log.getEntityId() != null) ps.setInt(4, log.getEntityId());
        else ps.setNull(4, Types.INTEGER);
        // user_agent co the do nguoi dung goi tuy y -> gioi han do dai de tranh log bi phinh to bat thuong
        String ua = log.getUserAgent();
        ps.setString(5, ua != null && ua.length() > 255 ? ua.substring(0, 255) : ua);
    }

    @Override
    public List<AuditLog> findByUserId(int userId) throws SQLException {
        List<AuditLog> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<AuditLog> findAll(int limit) throws SQLException {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT TOP (?) log_id, user_id, action, entity_type, entity_id, user_agent, created_at " +
                     "FROM AUDIT_LOGS ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public int insert(AuditLog log) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            bindParams(ps, log);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Khong lay duoc log_id vua tao");
    }

    @Override
    public int insert(Connection conn, AuditLog log) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            bindParams(ps, log);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Khong lay duoc log_id vua tao");
    }
}
