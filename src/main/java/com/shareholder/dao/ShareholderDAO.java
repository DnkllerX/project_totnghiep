package com.shareholder.dao;

import com.shareholder.model.Shareholder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ShareholderDAO {
    Optional<Shareholder> findById(int shareholderId) throws SQLException;
    Optional<Shareholder> findByUserId(int userId) throws SQLException;
    Optional<Shareholder> findByCitizenId(String citizenId) throws SQLException;
    List<Shareholder> findAll() throws SQLException;
    int insert(Shareholder shareholder) throws SQLException;
    int insert(Connection conn, Shareholder shareholder) throws SQLException;
    boolean update(Shareholder shareholder) throws SQLException;
    boolean existsByCitizenId(String citizenId) throws SQLException;
    boolean delete(int shareholderId) throws SQLException;
}
