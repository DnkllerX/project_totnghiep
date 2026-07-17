package com.shareholder.controller;

import com.shareholder.dao.ShareholderDAO;
import com.shareholder.dao.impl.ShareholderDAOImpl;
import com.shareholder.service.ShareholderService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Trang ADMIN: xem danh sach tai khoan status=LOCKED cho duyet + danh sach co dong da duoc duyet.
 * Duyet 1 tai khoan se chuyen status LOCKED -> ACTIVE va cap so co phan khoi tao (neu co).
 */
@WebServlet("/app/admin/shareholders")
public class ShareholderManagementServlet extends HttpServlet {

    private final ShareholderService shareholderService = new ShareholderService();
    private final ShareholderDAO shareholderDAO = new ShareholderDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("pendingApprovals", shareholderService.findLockedShareholders());
            req.setAttribute("shareholders", shareholderDAO.findAll());
        } catch (SQLException e) {
            getServletContext().log("Loi tai danh sach co dong", e);
            req.setAttribute("error", "Khong tai duoc danh sach co dong");
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/shareholders.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        int actorUserId = (Integer) session.getAttribute("userId");
        String userAgent = req.getHeader("User-Agent");

        try {
            int shareholderId = Integer.parseInt(req.getParameter("shareholderId"));
            String qtyStr = req.getParameter("initialQuantity");
            int initialQuantity = (qtyStr != null && !qtyStr.isBlank()) ? Integer.parseInt(qtyStr) : 0;

            shareholderService.approveShareholder(shareholderId, initialQuantity, actorUserId, userAgent);
            resp.sendRedirect(req.getContextPath() + "/app/admin/shareholders");
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Du lieu nhap khong hop le");
            doGet(req, resp);
        } catch (ShareholderService.ValidationException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Loi duyet co dong", e);
            req.setAttribute("error", "He thong dang gap su co, vui long thu lai sau");
            doGet(req, resp);
        }
    }
}
