package com.shareholder.dao;

import com.shareholder.model.Resolution;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ResolutionDAO {
    Optional<Resolution> findById(int resolutionId) throws SQLException;
    List<Resolution> findAll() throws SQLException;
    List<Resolution> findEndedNotClosed() throws SQLException;
    int insert(Resolution resolution) throws SQLException;
    boolean update(Resolution resolution) throws SQLException;
    boolean closeResolution(int resolutionId) throws SQLException;
    boolean delete(int resolutionId) throws SQLException;
}
