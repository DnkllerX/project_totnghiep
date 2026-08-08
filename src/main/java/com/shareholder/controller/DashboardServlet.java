package com.shareholder.controller;

import com.shareholder.dao.ShareTransactionDAO;
import com.shareholder.dao.ShareholderDAO;
import com.shareholder.dao.impl.ShareTransactionDAOImpl;
import com.shareholder.dao.impl.ShareholderDAOImpl;
import com.shareholder.model.enums.TxStatus;
import com.shareholder.service.ShareholderService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/app/dashboard")
public class DashboardServlet extends HttpServlet {

    private final ShareholderDAO shareholderDAO = new ShareholderDAOImpl();
    private final ShareholderService shareholderService = new ShareholderService();
    private final ShareTransactionDAO shareTransactionDAO = new ShareTransactionDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String role = session != null ? (String) session.getAttribute("role") : null;

        String view = switch (role == null ? "" : role) {
            case "ADMIN" -> "/WEB-INF/views/admin/dashboard.jsp";
            case "IT" -> "/WEB-INF/views/it/dashboard.jsp";
            case "SHAREHOLDER" -> "/WEB-INF/views/shareholder/dashboard.jsp";
            default -> null;
        };

        if (view == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if ("ADMIN".equals(role)) {
            // Chi so he thong tren dashboard ADMIN - lay THAT tu DB thay vi de trong
            // (truoc day khong gan attribute nao nen JSP luon fallback ve 0).
            try {
                req.setAttribute("totalShareholders", shareholderDAO.findAllActive().size());
            } catch (SQLException e) {
                getServletContext().log("Dashboard: loi dem tong so co dong", e);
            }
            try {
                req.setAttribute("pendingUsersCount", shareholderService.findLockedShareholders().size());
            } catch (SQLException e) {
                getServletContext().log("Dashboard: loi dem co dong cho duyet", e);
            }
            try {
                req.setAttribute("pendingTransfersCount",
                        shareTransactionDAO.findByStatus(TxStatus.PENDING).size());
            } catch (SQLException e) {
                getServletContext().log("Dashboard: loi dem giao dich cho xu ly", e);
            }
        }

        req.getRequestDispatcher(view).forward(req, resp);
    }
}
