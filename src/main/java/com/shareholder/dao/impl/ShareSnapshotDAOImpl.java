package com.shareholder.dao.impl;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.ShareSnapshotDAO;
import com.shareholder.model.ShareSnapshot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShareSnapshotDAOImpl implements ShareSnapshotDAO {

    private static final String SELECT_BASE =
            "SELECT snapshot_id, snapshot_date, reason, created_at FROM SHARE_SNAPSHOTS ";

    private ShareSnapshot mapRow(ResultSet rs) throws SQLException {
        ShareSnapshot s = new ShareSnapshot();
        s.setSnapshotId(rs.getInt("snapshot_id"));
        Timestamp sd = rs.getTimestamp("snapshot_date");
        if (sd != null) s.setSnapshotDate(sd.toLocalDateTime());
        s.setReason(rs.getString("reason"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) s.setCreatedAt(ts.toLocalDateTime());
        return s;
    }

    @Override
    public Optional<ShareSnapshot> findById(int snapshotId) throws SQLException {
        String sql = SELECT_BASE + "WHERE snapshot_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, snapshotId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ShareSnapshot> findAll() throws SQLException {
        List<ShareSnapshot> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY snapshot_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public int insert(Connection conn, ShareSnapshot snapshot) throws SQLException {
        String sql = "INSERT INTO SHARE_SNAPSHOTS (snapshot_date, reason, created_at) VALUES (?, ?, SYSDATETIME())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setTimestamp(1, Timestamp.valueOf(snapshot.getSnapshotDate()));
            ps.setString(2, snapshot.getReason());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Khong lay duoc snapshot_id vua tao");
    }

    @Override
    public Optional<ShareSnapshot> findLatestByResolutionId(int resolutionId) throws SQLException {
        // Prefix duoc ghep tu 1 so nguyen do he thong kiem soat (khong phai input tu nguoi dung),
        // van dung PreparedStatement + LIKE ? de dam bao an toan tuyet doi.
        String prefix = "Snapshot cho nghi quyet #" + resolutionId + ":%";
        String sql = SELECT_BASE + "WHERE reason LIKE ? ORDER BY snapshot_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prefix);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }
}
