package com.shareholder.dao;

import com.shareholder.model.ShareAdjustmentLog;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ShareAdjustmentLogDAO {
    List<ShareAdjustmentLog> findByShareholderId(int shareholderId) throws SQLException;
    List<ShareAdjustmentLog> findAll() throws SQLException;
    int insert(Connection conn, ShareAdjustmentLog log) throws SQLException;
}
