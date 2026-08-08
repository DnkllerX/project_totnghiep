package com.shareholder.dao.impl;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.DocumentDAO;
import com.shareholder.model.Document;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DocumentDAOImpl implements DocumentDAO {

    private static final String SELECT_BASE =
            "SELECT document_id, title, description, file_url, uploaded_at, created_by FROM DOCUMENTS ";

    private Document mapRow(ResultSet rs) throws SQLException {
        Document d = new Document();
        d.setDocumentId(rs.getInt("document_id"));
        d.setTitle(rs.getString("title"));
        d.setDescription(rs.getString("description"));
        d.setFileUrl(rs.getString("file_url"));
        Timestamp ts = rs.getTimestamp("uploaded_at");
        if (ts != null) d.setUploadedAt(ts.toLocalDateTime());
        int createdBy = rs.getInt("created_by");
        if (!rs.wasNull()) d.setCreatedBy(createdBy);
        return d;
    }

    @Override
    public Optional<Document> findById(int documentId) throws SQLException {
        String sql = SELECT_BASE + "WHERE document_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, documentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Document> findAll() throws SQLException {
        List<Document> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY uploaded_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public int insert(Document document) throws SQLException {
        String sql = "INSERT INTO DOCUMENTS (title, description, file_url, uploaded_at, created_by) " +
                     "VALUES (?, ?, ?, SYSDATETIME(), ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, document.getTitle());
            ps.setString(2, document.getDescription());
            ps.setString(3, document.getFileUrl());
            if (document.getCreatedBy() != null) {
                ps.setInt(4, document.getCreatedBy());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Khong lay duoc document_id vua tao");
    }

    @Override
    public boolean delete(int documentId) throws SQLException {
        String sql = "DELETE FROM DOCUMENTS WHERE document_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, documentId);
            return ps.executeUpdate() > 0;
        }
    }
}
