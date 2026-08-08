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

    /**
     * SHAREHOLDERS khong co cot status - status nam o USERS. Ban ghi SHAREHOLDERS duoc tao
     * ngay luc dang ky (truoc khi ADMIN duyet), nen findAll() se tra ve ca tai khoan con LOCKED.
     * Ham nay JOIN sang USERS va loc status = ACTIVE de dung cho man hinh "Da duyet".
     */
    @Override
    public List<Shareholder> findAllActive() throws SQLException {
        List<Shareholder> list = new ArrayList<>();
        String sql = "SELECT s.shareholder_id, s.user_id, s.full_name, s.citizen_id, s.phone, " +
                "s.address, s.birth_date, s.nationality, s.created_at " +
                "FROM SHAREHOLDERS s " +
                "JOIN USERS u ON u.user_id = s.user_id " +
                "WHERE u.status = 'ACTIVE' " +
                "ORDER BY s.shareholder_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Shareholder> searchActive(Integer id, String fullName, String citizenId, String phone)
            throws SQLException {
        List<Shareholder> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT s.shareholder_id, s.user_id, s.full_name, s.citizen_id, s.phone, " +
                "s.address, s.birth_date, s.nationality, s.created_at " +
                "FROM SHAREHOLDERS s " +
                "JOIN USERS u ON u.user_id = s.user_id " +
                "WHERE u.status = 'ACTIVE'");
        List<Object> params = new ArrayList<>();

        if (id != null) {
            sql.append(" AND s.shareholder_id = ?");
            params.add(id);
        }
        if (fullName != null && !fullName.isBlank()) {
            sql.append(" AND s.full_name LIKE ? ESCAPE '\\'");
            params.add("%" + escapeLike(fullName.trim()) + "%");
        }
        if (citizenId != null && !citizenId.isBlank()) {
            sql.append(" AND s.citizen_id LIKE ? ESCAPE '\\'");
            params.add("%" + escapeLike(citizenId.trim()) + "%");
        }
        if (phone != null && !phone.isBlank()) {
            sql.append(" AND s.phone LIKE ? ESCAPE '\\'");
            params.add("%" + escapeLike(phone.trim()) + "%");
        }
        sql.append(" ORDER BY s.shareholder_id");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /** Thoat cac ky tu dai dien cua LIKE (% va _) de tim kiem khong bi hieu nham thanh wildcard. */
    private String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
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
        String sql = "UPDATE SHAREHOLDERS SET full_name = ?, citizen_id = ?, phone = ?, address = ?, " +
                "birth_date = ?, nationality = ? WHERE shareholder_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sh.getFullName());
            ps.setString(2, sh.getCitizenId());
            ps.setString(3, sh.getPhone());
            ps.setString(4, sh.getAddress());
            if (sh.getBirthDate() != null) ps.setDate(5, Date.valueOf(sh.getBirthDate()));
            else ps.setNull(5, Types.DATE);
            ps.setString(6, sh.getNationality());
            ps.setInt(7, sh.getShareholderId());
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
