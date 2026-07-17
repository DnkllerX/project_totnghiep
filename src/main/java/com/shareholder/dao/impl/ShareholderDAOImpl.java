package com.shareholder.dao.impl;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.ShareholderDAO;
import com.shareholder.model.Shareholder;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShareholderDAOImpl implements ShareholderDAO {

    private static final String SELECT_BASE =
            "SELECT shareholder_id, user_id, full_name, citizen_id, phone, address, " +
            "birth_date, nationality, created_at FROM SHAREHOLDERS ";

    private Shareholder mapRow(ResultSet rs) throws SQLException {
        Shareholder s = new Shareholder();
        s.setShareholderId(rs.getInt("shareholder_id"));
        s.setUserId(rs.getInt("user_id"));
        s.setFullName(rs.getString("full_name"));
        s.setCitizenId(rs.getString("citizen_id"));
        s.setPhone(rs.getString("phone"));
        s.setAddress(rs.getString("address"));
        Date bd = rs.getDate("birth_date");
        if (bd != null) s.setBirthDate(bd.toLocalDate());
        s.setNationality(rs.getString("nationality"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) s.setCreatedAt(ts.toLocalDateTime());
        return s;
    }

    @Override
    public Optional<Shareholder> findById(int shareholderId) throws SQLException {
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
    public Optional<Shareholder> findByUserId(int userId) throws SQLException {
        String sql = SELECT_BASE + "WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Shareholder> findByCitizenId(String citizenId) throws SQLException {
        String sql = SELECT_BASE + "WHERE citizen_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, citizenId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Shareholder> findAll() throws SQLException {
        List<Shareholder> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY shareholder_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private static final String INSERT_SQL = "INSERT INTO SHAREHOLDERS " +
            "(user_id, full_name, citizen_id, phone, address, birth_date, nationality, created_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private void bindInsertParams(PreparedStatement ps, Shareholder sh) throws SQLException {
        ps.setInt(1, sh.getUserId());
        ps.setString(2, sh.getFullName());
        ps.setString(3, sh.getCitizenId());
        ps.setString(4, sh.getPhone());
        ps.setString(5, sh.getAddress());
        if (sh.getBirthDate() != null) ps.setDate(6, Date.valueOf(sh.getBirthDate()));
        else ps.setNull(6, Types.DATE);
        ps.setString(7, sh.getNationality());
        ps.setTimestamp(8, Timestamp.valueOf(
                sh.getCreatedAt() != null ? sh.getCreatedAt() : LocalDateTime.now()));
    }

    @Override
    public int insert(Shareholder sh) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            bindInsertParams(ps, sh);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Khong lay duoc shareholder_id vua tao");
    }

    @Override
    public int insert(Connection conn, Shareholder sh) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            bindInsertParams(ps, sh);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Khong lay duoc shareholder_id vua tao");
    }

    @Override
    public boolean update(Shareholder sh) throws SQLException {
        String sql = "UPDATE SHAREHOLDERS SET full_name = ?, phone = ?, address = ?, " +
                "birth_date = ?, nationality = ? WHERE shareholder_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sh.getFullName());
            ps.setString(2, sh.getPhone());
            ps.setString(3, sh.getAddress());
            if (sh.getBirthDate() != null) ps.setDate(4, Date.valueOf(sh.getBirthDate()));
            else ps.setNull(4, Types.DATE);
            ps.setString(5, sh.getNationality());
            ps.setInt(6, sh.getShareholderId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean existsByCitizenId(String citizenId) throws SQLException {
        String sql = "SELECT 1 FROM SHAREHOLDERS WHERE citizen_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, citizenId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public boolean delete(int shareholderId) throws SQLException {
        String sql = "DELETE FROM SHAREHOLDERS WHERE shareholder_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, shareholderId);
            return ps.executeUpdate() > 0;
        }
    }
}
