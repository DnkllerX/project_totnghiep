package com.shareholder.dao.impl;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.ResolutionDAO;
import com.shareholder.model.Resolution;
import com.shareholder.model.enums.ResolutionStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResolutionDAOImpl implements ResolutionDAO {

    private static final String SELECT_BASE =
            "SELECT resolution_id, title, description, status, start_time, end_time, created_at " +
            "FROM RESOLUTIONS ";

    private Resolution mapRow(ResultSet rs) throws SQLException {
        Resolution r = new Resolution();
        r.setResolutionId(rs.getInt("resolution_id"));
        r.setTitle(rs.getString("title"));
        r.setDescription(rs.getString("description"));
        r.setStatus(ResolutionStatus.valueOf(rs.getString("status")));
        Timestamp start = rs.getTimestamp("start_time");
        if (start != null) r.setStartTime(start.toLocalDateTime());
        Timestamp end = rs.getTimestamp("end_time");
        if (end != null) r.setEndTime(end.toLocalDateTime());
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) r.setCreatedAt(created.toLocalDateTime());
        return r;
    }

    @Override
    public Optional<Resolution> findById(int resolutionId) throws SQLException {
        String sql = SELECT_BASE + "WHERE resolution_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, resolutionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Resolution> findAll() throws SQLException {
        List<Resolution> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY start_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public List<Resolution> findEndedNotClosed() throws SQLException {
        List<Resolution> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE end_time <= ? AND status = 'OPEN'";
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
    public int insert(Resolution r) throws SQLException {
        String sql = "INSERT INTO RESOLUTIONS (title, description, status, start_time, end_time, created_at) " +
                     "VALUES (?, ?, 'OPEN', ?, ?, SYSDATETIME())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getTitle());
            ps.setString(2, r.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(r.getStartTime()));
            ps.setTimestamp(4, Timestamp.valueOf(r.getEndTime()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Khong lay duoc resolution_id vua tao");
    }

    @Override
    public boolean update(Resolution r) throws SQLException {
        String sql = "UPDATE RESOLUTIONS SET title = ?, description = ?, start_time = ?, end_time = ? " +
                     "WHERE resolution_id = ? AND status = 'OPEN'"; // khong cho sua sau khi da CLOSED
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getTitle());
            ps.setString(2, r.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(r.getStartTime()));
            ps.setTimestamp(4, Timestamp.valueOf(r.getEndTime()));
            ps.setInt(5, r.getResolutionId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean closeResolution(int resolutionId) throws SQLException {
        String sql = "UPDATE RESOLUTIONS SET status = 'CLOSED' WHERE resolution_id = ? AND status = 'OPEN'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, resolutionId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int resolutionId) throws SQLException {
        String sql = "DELETE FROM RESOLUTIONS WHERE resolution_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, resolutionId);
            return ps.executeUpdate() > 0;
        }
    }
}
