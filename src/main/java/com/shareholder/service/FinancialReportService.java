package com.shareholder.service;

import com.shareholder.dao.FinancialReportDAO;
import com.shareholder.dao.impl.FinancialReportDAOImpl;
import com.shareholder.model.FinancialReport;

import java.sql.SQLException;
import java.util.List;

public class FinancialReportService {

    private final FinancialReportDAO reportDAO = new FinancialReportDAOImpl();

    public static class ValidationException extends Exception {
        public ValidationException(String message) { super(message); }
    }

    public int addReport(FinancialReport report) throws SQLException, ValidationException {
        if (report.getReportYear() < 2000) throw new ValidationException("Nam bao cao khong hop le");
        if (report.getReportQuarter() < 1 || report.getReportQuarter() > 4) {
            throw new ValidationException("Quy bao cao phai tu 1 den 4");
        }
        if (reportDAO.existsByYearQuarter(report.getReportYear(), report.getReportQuarter())) {
            throw new ValidationException("Bao cao cho quy nay da ton tai");
        }
        return reportDAO.insert(report);
    }

    public List<FinancialReport> listAll() throws SQLException {
        return reportDAO.findAll();
    }
}
