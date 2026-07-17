package com.shareholder.controller;

import com.shareholder.model.FinancialReport;
import com.shareholder.service.FinancialReportService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet({"/app/admin/financial-reports/manage", "/app/shareholder/financial-reports"})
public class FinancialReportServlet extends HttpServlet {

    private final FinancialReportService reportService = new FinancialReportService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        boolean isAdminView = req.getRequestURI().contains("/admin/");
        try {
            req.setAttribute("reports", reportService.listAll());
        } catch (SQLException e) {
            getServletContext().log("Loi tai danh sach bao cao tai chinh", e);
        }
        String view = isAdminView
                ? "/WEB-INF/views/admin/financial-reports.jsp"
                : "/WEB-INF/views/shareholder/financial-reports.jsp";
        req.getRequestDispatcher(view).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            FinancialReport r = new FinancialReport();
            r.setReportYear(Integer.parseInt(req.getParameter("reportYear")));
            r.setReportQuarter(Integer.parseInt(req.getParameter("reportQuarter")));
            r.setRevenue(parseDecimalOrNull(req.getParameter("revenue")));
            r.setProfitBeforeTax(parseDecimalOrNull(req.getParameter("profitBeforeTax")));
            r.setProfitAfterTax(parseDecimalOrNull(req.getParameter("profitAfterTax")));
            r.setShortTermDebt(parseDecimalOrNull(req.getParameter("shortTermDebt")));
            r.setLongTermDebt(parseDecimalOrNull(req.getParameter("longTermDebt")));
            r.setEps(parseDecimalOrNull(req.getParameter("eps")));
            r.setPe(parseDecimalOrNull(req.getParameter("pe")));
            r.setRoe(parseDecimalOrNull(req.getParameter("roe")));
            r.setRoa(parseDecimalOrNull(req.getParameter("roa")));

            reportService.addReport(r);
            resp.sendRedirect(req.getContextPath() + "/app/admin/financial-reports/manage");
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Du lieu nhap khong hop le");
            doGet(req, resp);
        } catch (FinancialReportService.ValidationException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Loi them bao cao tai chinh", e);
            req.setAttribute("error", "He thong dang gap su co, vui long thu lai sau");
            doGet(req, resp);
        }
    }

    private BigDecimal parseDecimalOrNull(String value) {
        return (value == null || value.isBlank()) ? null : new BigDecimal(value);
    }
}
