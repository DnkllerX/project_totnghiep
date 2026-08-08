package com.shareholder.controller;

import com.shareholder.dao.ShareDAO;
import com.shareholder.dao.impl.ShareDAOImpl;
import com.shareholder.service.ShareAdjustmentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/app/admin/share-adjust")
public class ShareAdjustmentServlet extends HttpServlet {

    private final ShareAdjustmentService adjustmentService = new ShareAdjustmentService();
    private final ShareDAO shareDAO = new ShareDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("shares", shareDAO.findAll());
        } catch (SQLException e) {
            getServletContext().log("Loi tai danh sach co phan", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/share-adjust.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        int actorUserId = (Integer) session.getAttribute("userId");
        String userAgent = req.getHeader("User-Agent");

        try {
            int shareholderId = Integer.parseInt(req.getParameter("shareholderId"));
            int newQuantity = Integer.parseInt(req.getParameter("newQuantity"));
            String reason = req.getParameter("reason");

            adjustmentService.adjustShareQuantity(shareholderId, newQuantity, reason, actorUserId, userAgent);
            resp.sendRedirect(req.getContextPath() + "/app/admin/share-adjust");
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Du lieu nhap khong hop le");
            doGet(req, resp);
        } catch (ShareAdjustmentService.ValidationException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Loi dieu chinh co phan", e);
            req.setAttribute("error", "He thong dang gap su co, vui long thu lai sau");
            doGet(req, resp);
        }
    }
}
