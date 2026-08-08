package com.shareholder.dao;

import com.shareholder.model.Document;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DocumentDAO {
    Optional<Document> findById(int documentId) throws SQLException;
    List<Document> findAll() throws SQLException;
    int insert(Document document) throws SQLException;
    boolean delete(int documentId) throws SQLException;
}
