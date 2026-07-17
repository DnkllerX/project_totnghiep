package com.shareholder.dao.impl;

import com.shareholder.dao.ShareSnapshotDetailDAO;
import com.shareholder.config.DBConnection;
import com.shareholder.model.ShareSnapshotDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShareSnapshotDetailDAOImpl implements ShareSnapshotDetailDAO {

    private static final String SELECT_BASE =
            "SELECT id, snapshot_id, shareholder_id, share_quantity FROM SHARE_SNAPSHOT_DETAILS ";

    private ShareSnapshotDetail mapRow(ResultSet rs) throws SQLException {
        ShareSnapshotDetail d = new ShareSnapshotDetail();
        d.setId(rs.getInt("id"));
        d.setSnapshotId(rs.getInt("snapshot_id"));
        d.setShareholderId(rs.getInt("shareholder_id"));
        d.setShareQuantity(rs.getInt("share_quantity"));
        return d;
    }

    @Override
    public List<ShareSnapshotDetail> findBySnapshotId(int snapshotId) throws SQLException {
        List<ShareSnapshotDetail> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE snapshot_id = ? ORDER BY shareholder_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, snapshotId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<ShareSnapshotDetail> findByShareholderId(int shareholderId) throws SQLException {
        List<ShareSnapshotDetail> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE shareholder_id = ? ORDER BY snapshot_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, shareholderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Chup so du hien tai (SHARES) cua toan bo co dong dang co quantity > 0 vao SHARE_SNAPSHOT_DETAILS.
     * Dung INSERT ... SELECT (khong noi chuoi tu input nguoi dung, snapshotId la int nen an toan,
     * van dung PreparedStatement de nhat quan).
     */
    @Override
    public int snapshotAllCurrentShares(Connection conn, int snapshotId) throws SQLException {
        String sql = "INSERT INTO SHARE_SNAPSHOT_DETAILS (snapshot_id, shareholder_id, share_quantity) " +
                     "SELECT ?, shareholder_id, quantity FROM SHARES WHERE quantity > 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, snapshotId);
            return ps.executeUpdate();
        }
    }
}
