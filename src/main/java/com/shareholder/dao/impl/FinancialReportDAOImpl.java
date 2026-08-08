package com.shareholder.dao.impl;

import com.shareholder.config.DBConnection;
import com.shareholder.dao.FinancialReportDAO;
import com.shareholder.model.FinancialReport;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FinancialReportDAOImpl implements FinancialReportDAO {

    private static final String SELECT_BASE =
            "SELECT report_id, report_year, report_quarter, revenue, profit_before_tax, profit_after_tax, " +
            "short_term_debt, long_term_debt, eps, pe, roe, roa, created_at, created_by FROM FINANCIAL_REPORTS ";

    private FinancialReport mapRow(ResultSet rs) throws SQLException {
        FinancialReport f = new FinancialReport();
        f.setReportId(rs.getInt("report_id"));
        f.setReportYear(rs.getInt("report_year"));
        f.setReportQuarter(rs.getInt("report_quarter"));
        f.setRevenue(rs.getBigDecimal("revenue"));
        f.setProfitBeforeTax(rs.getBigDecimal("profit_before_tax"));
        f.setProfitAfterTax(rs.getBigDecimal("profit_after_tax"));
        f.setShortTermDebt(rs.getBigDecimal("short_term_debt"));
        f.setLongTermDebt(rs.getBigDecimal("long_term_debt"));
        f.setEps(rs.getBigDecimal("eps"));
        f.setPe(rs.getBigDecimal("pe"));
        f.setRoe(rs.getBigDecimal("roe"));
        f.setRoa(rs.getBigDecimal("roa"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) f.setCreatedAt(ts.toLocalDateTime());
        int createdBy = rs.getInt("created_by");
        if (!rs.wasNull()) f.setCreatedBy(createdBy);
        return f;
    }

    @Override
    public Optional<FinancialReport> findByYearQuarter(int year, int quarter) throws SQLException {
        String sql = SELECT_BASE + "WHERE report_year = ? AND report_quarter = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, quarter);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<FinancialReport> findById(int reportId) throws SQLException {
        String sql = SELECT_BASE + "WHERE report_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reportId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<FinancialReport> findAll() throws SQLException {
        List<FinancialReport> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY report_year DESC, report_quarter DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    public int insert(FinancialReport r) throws SQLException {
        String sql = "INSERT INTO FINANCIAL_REPORTS " +
                "(report_year, report_quarter, revenue, profit_before_tax, profit_after_tax, " +
                "short_term_debt, long_term_debt, eps, pe, roe, roa, created_at, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATETIME(), ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getReportYear());
            ps.setInt(2, r.getReportQuarter());
            ps.setBigDecimal(3, r.getRevenue());
            ps.setBigDecimal(4, r.getProfitBeforeTax());
            ps.setBigDecimal(5, r.getProfitAfterTax());
            ps.setBigDecimal(6, r.getShortTermDebt());
            ps.setBigDecimal(7, r.getLongTermDebt());
            ps.setBigDecimal(8, r.getEps());
            ps.setBigDecimal(9, r.getPe());
            ps.setBigDecimal(10, r.getRoe());
            ps.setBigDecimal(11, r.getRoa());
            if (r.getCreatedBy() != null) {
                ps.setInt(12, r.getCreatedBy());
            } else {
                ps.setNull(12, Types.INTEGER);
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Khong lay duoc report_id vua tao");
    }

    @Override
    public void update(FinancialReport r) throws SQLException {
        // Y CHINH: KHONG dua created_by vao cau UPDATE - giu nguyen nguoi tao ban dau du
        // ADMIN nao khac dang sua lai bao cao nay, dung nhu thiet ke da thong nhat.
        String sql = "UPDATE FINANCIAL_REPORTS SET report_year = ?, report_quarter = ?, revenue = ?, " +
                "profit_before_tax = ?, profit_after_tax = ?, short_term_debt = ?, long_term_debt = ?, " +
                "eps = ?, pe = ?, roe = ?, roa = ? WHERE report_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, r.getReportYear());
            ps.setInt(2, r.getReportQuarter());
            ps.setBigDecimal(3, r.getRevenue());
            ps.setBigDecimal(4, r.getProfitBeforeTax());
            ps.setBigDecimal(5, r.getProfitAfterTax());
            ps.setBigDecimal(6, r.getShortTermDebt());
            ps.setBigDecimal(7, r.getLongTermDebt());
            ps.setBigDecimal(8, r.getEps());
            ps.setBigDecimal(9, r.getPe());
            ps.setBigDecimal(10, r.getRoe());
            ps.setBigDecimal(11, r.getRoa());
            ps.setInt(12, r.getReportId());
            int affected = ps.executeUpdate();
            if (affected == 0) throw new SQLException("Khong tim thay bao cao de cap nhat (report_id=" + r.getReportId() + ")");
        }
    }

    @Override
    public boolean existsByYearQuarter(int year, int quarter) throws SQLException {
        String sql = "SELECT 1 FROM FINANCIAL_REPORTS WHERE report_year = ? AND report_quarter = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, quarter);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public boolean existsByYearQuarterExcludingId(int year, int quarter, int excludeReportId) throws SQLException {
        String sql = "SELECT 1 FROM FINANCIAL_REPORTS WHERE report_year = ? AND report_quarter = ? AND report_id <> ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, quarter);
            ps.setInt(3, excludeReportId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
