package com.shareholder.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Dich cho <error-page><error-code>404</error-code>> trong web.xml. Khi nguoi dung go/sua URL
 * khong khop bat ky servlet nao (vd tu xoa bot doan URL, go sai duong dan), thay vi hien trang
 * loi 404 mac dinh cua Tomcat, chuyen huong thong minh:
 *   - Da dang nhap (con session hop le) -> ve /app/dashboard (DashboardServlet se tu forward
 *     dung trang dashboard theo role trong session - ADMIN/IT/SHAREHOLDER).
 *   - Chua dang nhap -> ve trang chu "/".
 * Servlet nay nam NGOAI /app/* nen KHONG bi AuthFilter chan (giong /login, /register).
 */
@WebServlet("/error-redirect")
public class ErrorRedirectServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        boolean loggedIn = session != null && session.getAttribute("userId") != null;

        resp.sendRedirect(req.getContextPath() + (loggedIn ? "/app/dashboard" : "/"));
    }
}
