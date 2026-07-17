package com.shareholder.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/app/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String role = session != null ? (String) session.getAttribute("role") : null;

        String view = switch (role == null ? "" : role) {
            case "ADMIN" -> "/WEB-INF/views/admin/dashboard.jsp";
            case "IT" -> "/WEB-INF/views/it/dashboard.jsp";
            case "SHAREHOLDER" -> "/WEB-INF/views/shareholder/dashboard.jsp";
            default -> "/login.jsp";
        };

        if (view.equals("/login.jsp")) {
            resp.sendRedirect(req.getContextPath() + view);
            return;
        }
        req.getRequestDispatcher(view).forward(req, resp);
    }
}
