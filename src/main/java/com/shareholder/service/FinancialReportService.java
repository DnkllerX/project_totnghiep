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

    public int addReport(FinancialReport report, int actorUserId) throws SQLException, ValidationException {
        if (report.getReportYear() < 2000) throw new ValidationException("Nam bao cao khong hop le");
        if (report.getReportQuarter() < 1 || report.getReportQuarter() > 4) {
            throw new ValidationException("Quy bao cao phai tu 1 den 4");
        }
        if (reportDAO.existsByYearQuarter(report.getReportYear(), report.getReportQuarter())) {
            throw new ValidationException("Bao cao cho quy nay da ton tai");
        }
        // actorUserId luon la ADMIN o day (FinancialReportServlet da chan role truoc khi goi vao
        // ham nay) nen gan thang lam nguoi tao, khong can kiem tra lai role.
        report.setCreatedBy(actorUserId);
        return reportDAO.insert(report);
    }

    public void updateReport(FinancialReport report) throws SQLException, ValidationException {
        if (report.getReportId() <= 0) throw new ValidationException("Thieu ma bao cao can cap nhat");
        if (report.getReportYear() < 2000) throw new ValidationException("Nam bao cao khong hop le");
        if (report.getReportQuarter() < 1 || report.getReportQuarter() > 4) {
            throw new ValidationException("Quy bao cao phai tu 1 den 4");
        }
        if (reportDAO.existsByYearQuarterExcludingId(report.getReportYear(), report.getReportQuarter(), report.getReportId())) {
            throw new ValidationException("Da co bao cao khac cho nam/quy nay");
        }
        reportDAO.update(report);
    }

    public List<FinancialReport> listAll() throws SQLException {
        return reportDAO.findAll();
    }
}
