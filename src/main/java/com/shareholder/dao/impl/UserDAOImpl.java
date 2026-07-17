package com.shareholder.dao.impl;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.UserDAO;
import com.shareholder.model.User;
import com.shareholder.model.enums.UserRole;
import com.shareholder.model.enums.UserSortOption;
import com.shareholder.model.enums.UserStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Tat ca cau lenh SQL trong lop nay deu dung PreparedStatement voi tham so "?".
 * Khong bao gio noi chuoi SQL truc tiep tu du lieu nguoi dung -> chong SQL Injection.
 */
public class UserDAOImpl implements UserDAO {

    private static final String SELECT_BASE =
            "SELECT user_id, username, email, password_hash, role, status, created_at FROM USERS ";

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(UserRole.valueOf(rs.getString("role")));
        u.setStatus(UserStatus.valueOf(rs.getString("status")));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) u.setCreatedAt(ts.toLocalDateTime());
        return u;
    }

    @Override
    public Optional<User> findById(int userId) throws SQLException {
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
    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = SELECT_BASE + "WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = SELECT_BASE + "WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<User> findByRole(UserRole role) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE role = ? ORDER BY user_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<User> findLockedShareholders() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE role = ? AND status = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, UserRole.SHAREHOLDER.name());
            ps.setString(2, UserStatus.LOCKED.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Xay dung WHERE dong dua tren cac dieu kien optional. Cau truc cau lenh (ten cot, toan tu)
     * la hang so co dinh trong code - chi GIA TRI thuc te (do nguoi dung nhap) di qua tham so "?".
     */
    @Override
    public List<User> search(String usernameContains, String emailContains, UserRole role, UserStatus status,
                              UserSortOption sort) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_BASE + "WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (usernameContains != null && !usernameContains.isBlank()) {
            sql.append("AND username LIKE ? ESCAPE '\\' ");
            params.add("%" + escapeLike(usernameContains.trim()) + "%");
        }
        if (emailContains != null && !emailContains.isBlank()) {
            sql.append("AND email LIKE ? ESCAPE '\\' ");
            params.add("%" + escapeLike(emailContains.trim()) + "%");
        }
        if (role != null) {
            sql.append("AND role = ? ");
            params.add(role.name());
        }
        if (status != null) {
            sql.append("AND status = ? ");
            params.add(status.name());
        }

        if (sort == UserSortOption.USERNAME_ASC) {
            sql.append("ORDER BY username ASC");
        } else {
            sql.append("ORDER BY created_at DESC"); // mac dinh / NEWEST_FIRST
        }

        List<User> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, (String) params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /** Escape ky tu dac biet cua LIKE (% va _) de nguoi dung khong the tu y mo rong pattern tim kiem. */
    private String escapeLike(String input) {
        return input.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    @Override
    public boolean updateStatusIfCurrentStatus(Connection conn, int userId, UserStatus fromStatus,
                                                UserStatus toStatus) throws SQLException {
        String sql = "UPDATE USERS SET status = ? WHERE user_id = ? AND status = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, toStatus.name());
            ps.setInt(2, userId);
            ps.setString(3, fromStatus.name());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY user_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private static final String INSERT_SQL =
            "INSERT INTO USERS (username, email, password_hash, role, status, created_at) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private void bindInsertParams(PreparedStatement ps, User user) throws SQLException {
        ps.setString(1, user.getUsername());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getPasswordHash());
        ps.setString(4, user.getRole().name());
        ps.setString(5, user.getStatus().name());
        ps.setTimestamp(6, Timestamp.valueOf(
                user.getCreatedAt() != null ? user.getCreatedAt() : LocalDateTime.now()));
    }

    @Override
    public int insert(User user) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            bindInsertParams(ps, user);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Khong lay duoc user_id vua tao");
    }

    @Override
    public int insert(Connection conn, User user) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            bindInsertParams(ps, user);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Khong lay duoc user_id vua tao");
    }

    @Override
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE USERS SET username = ?, email = ?, role = ?, status = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getRole().name());
            ps.setString(4, user.getStatus().name());
            ps.setInt(5, user.getUserId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStatus(int userId, UserStatus status) throws SQLException {
        String sql = "UPDATE USERS SET status = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updatePasswordHash(int userId, String passwordHash) throws SQLException {
        String sql = "UPDATE USERS SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT 1 FROM USERS WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public boolean existsByEmail(String email) throws SQLException {
        String sql = "SELECT 1 FROM USERS WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public boolean delete(int userId) throws SQLException {
        String sql = "DELETE FROM USERS WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        }
    }
}
