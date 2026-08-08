package com.shareholder.dao;

import com.shareholder.model.AuditLog;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface AuditLogDAO {
    List<AuditLog> findByUserId(int userId) throws SQLException;
    List<AuditLog> findAll(int limit) throws SQLException;

    /** Log dung connection rieng (fire-and-forget, khong nam trong transaction nghiep vu chinh). */
    int insert(AuditLog log) throws SQLException;

    /** Log trong cung transaction voi thao tac nghiep vu (vd: transfer, adjust) de dam bao tinh nhat quan. */
    int insert(Connection conn, AuditLog log) throws SQLException;
}
