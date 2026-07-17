package com.shareholder.dao.impl;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.ShareAdjustmentLogDAO;
import com.shareholder.model.ShareAdjustmentLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShareAdjustmentLogDAOImpl implements ShareAdjustmentLogDAO {

    private static final String SELECT_BASE =
            "SELECT id, shareholder_id, old_value, new_value, reason, adjusted_by, adjusted_at " +
            "FROM SHARE_ADJUSTMENT_LOGS ";

    private ShareAdjustmentLog mapRow(ResultSet rs) throws SQLException {
        ShareAdjustmentLog l = new ShareAdjustmentLog();
        l.setId(rs.getInt("id"));
        l.setShareholderId(rs.getInt("shareholder_id"));
        l.setOldValue(rs.getInt("old_value"));
        l.setNewValue(rs.getInt("new_value"));
        l.setReason(rs.getString("reason"));
        l.setAdjustedBy(rs.getInt("adjusted_by"));
        Timestamp ts = rs.getTimestamp("adjusted_at");
        if (ts != null) l.setAdjustedAt(ts.toLocalDateTime());
        return l;
    }

    @Override
    public List<ShareAdjustmentLog> findByShareholderId(int shareholderId) throws SQLException {
        List<ShareAdjustmentLog> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE shareholder_id = ? ORDER BY adjusted_at DESC";
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
    public List<ShareAdjustmentLog> findAll() throws SQLException {
        List<ShareAdjustmentLog> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY adjusted_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public int insert(Connection conn, ShareAdjustmentLog log) throws SQLException {
        String sql = "INSERT INTO SHARE_ADJUSTMENT_LOGS " +
                "(shareholder_id, old_value, new_value, reason, adjusted_by, adjusted_at) " +
                "VALUES (?, ?, ?, ?, ?, SYSDATETIME())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, log.getShareholderId());
            ps.setInt(2, log.getOldValue());
            ps.setInt(3, log.getNewValue());
            ps.setString(4, log.getReason());
            ps.setInt(5, log.getAdjustedBy());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Khong lay duoc adjustment log id vua tao");
    }
}
