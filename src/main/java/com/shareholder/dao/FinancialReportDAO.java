package com.shareholder.dao;

import com.shareholder.model.FinancialReport;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface FinancialReportDAO {
    Optional<FinancialReport> findByYearQuarter(int year, int quarter) throws SQLException;
    Optional<FinancialReport> findById(int reportId) throws SQLException;
    List<FinancialReport> findAll() throws SQLException;
    int insert(FinancialReport report) throws SQLException;
    void update(FinancialReport report) throws SQLException;
    boolean existsByYearQuarter(int year, int quarter) throws SQLException;
    boolean existsByYearQuarterExcludingId(int year, int quarter, int excludeReportId) throws SQLException;
}
