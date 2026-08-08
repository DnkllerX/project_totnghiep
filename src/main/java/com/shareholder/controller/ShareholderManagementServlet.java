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

        String idParam = req.getParameter("searchId");
        String fullName = req.getParameter("searchFullName");
        String citizenId = req.getParameter("searchCitizenId");
        String phone = req.getParameter("searchPhone");

        Integer id = null;
        if (idParam != null && !idParam.isBlank()) {
            try {
                id = Integer.parseInt(idParam.trim());
            } catch (NumberFormatException e) {
                req.setAttribute("error", "ID phai la so");
            }
        }

        // Giu lai gia tri da nhap de hien thi lai trong form tim kiem sau khi submit
        req.setAttribute("searchId", idParam);
        req.setAttribute("searchFullName", fullName);
        req.setAttribute("searchCitizenId", citizenId);
        req.setAttribute("searchPhone", phone);

        try {
            req.setAttribute("pendingApprovals", shareholderService.findLockedShareholders());
            boolean hasSearch = id != null || (fullName != null && !fullName.isBlank())
                    || (citizenId != null && !citizenId.isBlank()) || (phone != null && !phone.isBlank());
            req.setAttribute("shareholders", hasSearch
                    ? shareholderDAO.searchActive(id, fullName, citizenId, phone)
                    : shareholderDAO.findAllActive());
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
