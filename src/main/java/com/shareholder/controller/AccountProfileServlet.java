package com.shareholder.controller;

import com.shareholder.service.ProfileService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Trang "Tai khoan cua toi": xem thong tin ca nhan + doi mat khau tu-phuc-vu.
 * KHONG dung chung voi UserAccountServlet (/app/it/user-management, danh cho IT quan tri
 * TAI KHOAN NGUOI KHAC) - o day chi thao tac tren chinh userId dang dang nhap (lay tu session,
 * KHONG bao gio lay tu request param, tranh IDOR sua/xem tai khoan nguoi khac).
 */
@WebServlet("/app/shareholder/profile")
public class AccountProfileServlet extends HttpServlet {

    private final ProfileService profileService = new ProfileService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");
        try {
            req.setAttribute("profile", profileService.getProfile(userId));
        } catch (SQLException e) {
            getServletContext().log("Loi tai thong tin tai khoan", e);
            req.setAttribute("error", "Khong tai duoc thong tin tai khoan");
        } catch (ProfileService.ValidationException e) {
            req.setAttribute("error", e.getMessage());
        }
        req.getRequestDispatcher("/WEB-INF/views/shareholder/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");
        String userAgent = req.getHeader("User-Agent");
        String action = req.getParameter("action");

        try {
            if ("update-info".equals(action)) {
                profileService.updateProfile(
                        userId,
                        req.getParameter("fullName"),
                        req.getParameter("citizenId"),
                        req.getParameter("phone"),
                        req.getParameter("address"),
                        userAgent);
                req.setAttribute("success", "Cap nhat thong tin ca nhan thanh cong");
                doGet(req, resp);
                return;
            }

            // Mac dinh (hoac action=change-password): doi mat khau
            profileService.changeOwnPassword(
                    userId,
                    req.getParameter("currentPassword"),
                    req.getParameter("newPassword"),
                    req.getParameter("confirmNewPassword"),
                    userAgent);

            // Doi mat khau xong: invalidate session hien tai, bat dang nhap lai bang mat khau moi -
            // tranh truong hop token/session cu con hieu luc sau khi mat khau da doi (best practice).
            session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login?passwordChanged=1");
        } catch (ProfileService.ValidationException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Loi cap nhat tai khoan", e);
            req.setAttribute("error", "He thong dang gap su co, vui long thu lai sau");
            doGet(req, resp);
        }
    }
}
