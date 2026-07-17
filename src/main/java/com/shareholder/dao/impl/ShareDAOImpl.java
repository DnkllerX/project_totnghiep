package com.shareholder.dao.impl;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.ShareDAO;
import com.shareholder.model.Share;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShareDAOImpl implements ShareDAO {

    private static final String SELECT_BASE =
            "SELECT share_id, shareholder_id, quantity, updated_at FROM SHARES ";

    private Share mapRow(ResultSet rs) throws SQLException {
        Share s = new Share();
        s.setShareId(rs.getInt("share_id"));
        s.setShareholderId(rs.getInt("shareholder_id"));
        s.setQuantity(rs.getInt("quantity"));
        Timestamp ts = rs.getTimestamp("updated_at");
        if (ts != null) s.setUpdatedAt(ts.toLocalDateTime());
        return s;
    }

    @Override
    public Optional<Share> findByShareholderId(int shareholderId) throws SQLException {
        String sql = SELECT_BASE + "WHERE shareholder_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, shareholderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Share> findAll() throws SQLException {
        List<Share> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY shareholder_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private static final String INSERT_SQL =
            "INSERT INTO SHARES (shareholder_id, quantity, updated_at) VALUES (?, ?, SYSDATETIME())";

    @Override
    public int insert(Share share) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, share.getShareholderId());
            ps.setInt(2, share.getQuantity());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Khong lay duoc share_id vua tao");
    }

    @Override
    public int insert(Connection conn, Share share) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, share.getShareholderId());
            ps.setInt(2, share.getQuantity());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Khong lay duoc share_id vua tao");
    }

    /**
     * delta co the am hoac duong. Dung SQL "quantity = quantity + ?" de tranh race condition
     * (khong doc-sua-ghi o tang ung dung). Ham nhan Connection tu ben ngoai de Service co the
     * gop nhieu thao tac (SHARES + SHARE_TRANSACTIONS + AUDIT_LOGS) vao 1 transaction.
     */
    @Override
    public boolean addQuantity(Connection conn, int shareholderId, int delta) throws SQLException {
        String sql = "UPDATE SHARES SET quantity = quantity + ?, updated_at = SYSDATETIME() " +
                     "WHERE shareholder_id = ? AND quantity + ? >= 0"; // chan am so (CHK_Shares_Quantity)
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, shareholderId);
            ps.setInt(3, delta);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean setQuantity(Connection conn, int shareholderId, int newQuantity) throws SQLException {
        if (newQuantity < 0) return false;
        String sql = "UPDATE SHARES SET quantity = ?, updated_at = SYSDATETIME() WHERE shareholder_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, shareholderId);
            return ps.executeUpdate() > 0;
        }
    }
}
