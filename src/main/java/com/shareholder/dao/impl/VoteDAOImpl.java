package com.shareholder.dao.impl;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.VoteDAO;
import com.shareholder.model.Vote;
import com.shareholder.model.enums.VoteValue;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VoteDAOImpl implements VoteDAO {

    private static final String SELECT_BASE =
            "SELECT vote_id, resolution_id, shareholder_id, snapshot_id, vote_value, voted_at FROM VOTES ";

    private Vote mapRow(ResultSet rs) throws SQLException {
        Vote v = new Vote();
        v.setVoteId(rs.getInt("vote_id"));
        v.setResolutionId(rs.getInt("resolution_id"));
        v.setShareholderId(rs.getInt("shareholder_id"));
        v.setSnapshotId(rs.getInt("snapshot_id"));
        v.setVoteValue(VoteValue.valueOf(rs.getString("vote_value")));
        Timestamp ts = rs.getTimestamp("voted_at");
        if (ts != null) v.setVotedAt(ts.toLocalDateTime());
        return v;
    }

    @Override
    public Optional<Vote> findByResolutionAndShareholder(int resolutionId, int shareholderId) throws SQLException {
        String sql = SELECT_BASE + "WHERE resolution_id = ? AND shareholder_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, resolutionId);
            ps.setInt(2, shareholderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Vote> findByResolutionId(int resolutionId) throws SQLException {
        List<Vote> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE resolution_id = ? ORDER BY voted_at";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, resolutionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<Vote> findByShareholderId(int shareholderId) throws SQLException {
        List<Vote> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE shareholder_id = ? ORDER BY voted_at DESC";
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
    public boolean hasVoted(int resolutionId, int shareholderId) throws SQLException {
        String sql = "SELECT 1 FROM VOTES WHERE resolution_id = ? AND shareholder_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, resolutionId);
            ps.setInt(2, shareholderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public int insert(Vote vote) throws SQLException {
        // UQ_Votes(resolution_id, shareholder_id) o tang DB se nem SQLException (vi pham unique)
        // neu co dong co gang vote lan 2 - day la lop bao ve cuoi cung, khong chi dua vao check tang service.
        String sql = "INSERT INTO VOTES (resolution_id, shareholder_id, snapshot_id, vote_value, voted_at) " +
                     "VALUES (?, ?, ?, ?, SYSDATETIME())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, vote.getResolutionId());
            ps.setInt(2, vote.getShareholderId());
            ps.setInt(3, vote.getSnapshotId());
            ps.setString(4, vote.getVoteValue().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Khong lay duoc vote_id vua tao");
    }

    @Override
    public Map<String, Integer> countByResolutionId(int resolutionId) throws SQLException {
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("AGREE", 0);
        result.put("DISAGREE", 0);
        result.put("ABSTAIN", 0);
        // 1 co phan pho thong = 1 phieu bieu quyet (dung Luat Doanh nghiep), KHONG PHAI 1 nguoi = 1 phieu.
        // JOIN voi SHARE_SNAPSHOT_DETAILS de lay dung so co phan co dong nam giu TAI THOI DIEM chot
        // snapshot cua nghi quyet nay (v.snapshot_id) - khong dung so co phan hien tai, vi co dong co
        // the da mua/ban them sau khi bieu quyet.
        String sql = "SELECT v.vote_value, SUM(d.share_quantity) AS cnt " +
                     "FROM VOTES v " +
                     "JOIN SHARE_SNAPSHOT_DETAILS d " +
                     "  ON d.snapshot_id = v.snapshot_id AND d.shareholder_id = v.shareholder_id " +
                     "WHERE v.resolution_id = ? " +
                     "GROUP BY v.vote_value";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, resolutionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("vote_value"), rs.getInt("cnt"));
                }
            }
        }
        return result;
    }
}
