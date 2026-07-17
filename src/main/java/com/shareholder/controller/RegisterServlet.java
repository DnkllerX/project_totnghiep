package com.shareholder.controller;

import com.shareholder.service.ShareholderService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Trang dang ky tai khoan cong khai (khong yeu cau dang nhap, khong nam trong /app/*).
 * Tai khoan tao ra co role=SHAREHOLDER nhung status=LOCKED nen KHONG dang nhap duoc
 * cho den khi ADMIN duyet qua /app/admin/shareholders (status chuyen sang ACTIVE).
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final ShareholderService shareholderService = new ShareholderService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String userAgent = req.getHeader("User-Agent");
        try {
            String birthDateStr = req.getParameter("birthDate");
            LocalDate birthDate = (birthDateStr != null && !birthDateStr.isBlank())
                    ? LocalDate.parse(birthDateStr) : null;

            shareholderService.registerShareholderAccount(
                    req.getParameter("username"),
                    req.getParameter("email"),
                    req.getParameter("password"),
                    req.getParameter("fullName"),
                    req.getParameter("citizenId"),
                    req.getParameter("phone"),
                    req.getParameter("address"),
                    birthDate,
                    req.getParameter("nationality"),
                    userAgent);

            req.setAttribute("success",
                    "Dang ky thanh cong! Tai khoan cua ban dang cho ADMIN duyet truoc khi su dung duoc.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        } catch (ShareholderService.ValidationException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Loi dang ky tai khoan", e);
            req.setAttribute("error", "He thong dang gap su co, vui long thu lai sau");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }
}
