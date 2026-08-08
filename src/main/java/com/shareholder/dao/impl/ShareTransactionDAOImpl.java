package com.shareholder.dao.impl;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.ShareTransactionDAO;
import com.shareholder.model.ShareTransaction;
import com.shareholder.model.enums.TxStatus;
import com.shareholder.model.enums.TxType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShareTransactionDAOImpl implements ShareTransactionDAO {

    private static final String SELECT_BASE =
            "SELECT tx_id, from_shareholder_id, to_shareholder_id, quantity, tx_type, status, created_at " +
            "FROM SHARE_TRANSACTIONS ";

    private ShareTransaction mapRow(ResultSet rs) throws SQLException {
        ShareTransaction t = new ShareTransaction();
        t.setTxId(rs.getInt("tx_id"));
        int from = rs.getInt("from_shareholder_id");
        t.setFromShareholderId(rs.wasNull() ? null : from);
        int to = rs.getInt("to_shareholder_id");
        t.setToShareholderId(rs.wasNull() ? null : to);
        t.setQuantity(rs.getInt("quantity"));
        t.setTxType(TxType.valueOf(rs.getString("tx_type")));
        t.setStatus(TxStatus.valueOf(rs.getString("status")));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) t.setCreatedAt(ts.toLocalDateTime());
        return t;
    }

    @Override
    public Optional<ShareTransaction> findById(int txId) throws SQLException {
        String sql = SELECT_BASE + "WHERE tx_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, txId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ShareTransaction> findByShareholderId(int shareholderId) throws SQLException {
        List<ShareTransaction> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE from_shareholder_id = ? OR to_shareholder_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, shareholderId);
            ps.setInt(2, shareholderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<ShareTransaction> findByStatus(TxStatus status) throws SQLException {
        List<ShareTransaction> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE status = ? ORDER BY created_at";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<ShareTransaction> findTransferHistory() throws SQLException {
        List<ShareTransaction> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE tx_type = 'TRANSFER' AND status <> 'PENDING' " +
                "ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<ShareTransaction> findAll() throws SQLException {
        List<ShareTransaction> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public int insert(Connection conn, ShareTransaction tx) throws SQLException {
        String sql = "INSERT INTO SHARE_TRANSACTIONS " +
                "(from_shareholder_id, to_shareholder_id, quantity, tx_type, status, created_at) " +
                "VALUES (?, ?, ?, ?, ?, SYSDATETIME())";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (tx.getFromShareholderId() != null) ps.setInt(1, tx.getFromShareholderId());
            else ps.setNull(1, Types.INTEGER);
            if (tx.getToShareholderId() != null) ps.setInt(2, tx.getToShareholderId());
            else ps.setNull(2, Types.INTEGER);
            ps.setInt(3, tx.getQuantity());
            ps.setString(4, tx.getTxType().name());
            ps.setString(5, tx.getStatus().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Khong lay duoc tx_id vua tao");
    }

    @Override
    public boolean updateStatus(int txId, TxStatus status) throws SQLException {
        String sql = "UPDATE SHARE_TRANSACTIONS SET status = ? WHERE tx_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, txId);
            return ps.executeUpdate() > 0;
        }
    }
}
