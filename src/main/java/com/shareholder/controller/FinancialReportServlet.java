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
        // CHI ADMIN duoc them bao cao - servlet nay dung chung URL cho ca ADMIN (quan ly, /manage)
        // va SHAREHOLDER (chi xem, /app/shareholder/financial-reports). AuthFilter.ROLE_RULES chi
        // chan theo URL path, KHONG phan biet duoc GET (xem) vs POST (them) trong cung 1 URL, nen
        // phai tu kiem tra role o day - neu khong, SHAREHOLDER co the tu POST thang vao
        // /app/shareholder/financial-reports (ho duoc phep truy cap URL nay) de tao bao cao gia.
        HttpSession session = req.getSession(false);
        if (!"ADMIN".equals(session.getAttribute("role"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Chi ADMIN moi duoc them bao cao tai chinh");
            return;
        }

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

            // Neu form gui kem reportId (> 0) tuc la dang SUA bao cao co san, nguoc lai la THEM moi.
            String reportIdParam = req.getParameter("reportId");
            boolean isEdit = reportIdParam != null && !reportIdParam.isBlank() && Integer.parseInt(reportIdParam) > 0;

            if (isEdit) {
                r.setReportId(Integer.parseInt(reportIdParam));
                reportService.updateReport(r);
            } else {
                int actorUserId = (Integer) session.getAttribute("userId");
                reportService.addReport(r, actorUserId);
            }
            resp.sendRedirect(req.getContextPath() + "/app/admin/financial-reports/manage");
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Du lieu nhap khong hop le");
            doGet(req, resp);
        } catch (FinancialReportService.ValidationException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Loi luu bao cao tai chinh", e);
            req.setAttribute("error", "He thong dang gap su co, vui long thu lai sau");
            doGet(req, resp);
        }
    }

    private BigDecimal parseDecimalOrNull(String value) {
        return (value == null || value.isBlank()) ? null : new BigDecimal(value);
    }
}
