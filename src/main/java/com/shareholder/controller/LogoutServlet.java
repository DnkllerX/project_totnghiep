package com.shareholder.controller;

import com.shareholder.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/app/logout")
public class LogoutServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId != null) {
                try {
                    authService.logout(userId, req.getHeader("User-Agent"));
                } catch (SQLException e) {
                    getServletContext().log("Loi ghi audit log khi logout", e);
                }
            }
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/");
    }
}
