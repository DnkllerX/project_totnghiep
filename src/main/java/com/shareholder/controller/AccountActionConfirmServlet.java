package com.shareholder.controller;

import com.shareholder.service.UserAccountService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Trang xac nhan hanh dong nhay cam (step-up confirmation) - CONG KHAI, KHONG yeu cau dang nhap
 * (nam ngoai /app/*, khong bi AuthFilter chan), giong het pattern cua ResetPasswordServlet.
 *
 * Nguoi bam vao day la CHINH CHU tai khoan bi tac dong (ADMIN/IT), lay tu link trong email do
 * UserAccountService.checkStepUp() sinh ra khi 1 IT khac yeu cau dat lai mat khau/khoa tai khoan
 * ho. GET chi HIEN thi trang xac nhan (khong tu dong thuc hien), POST (bam nut) moi that su goi
 * UserAccountService.confirmPendingAction() de thuc hien hanh dong - tranh truong hop link bi
 * trinh duyet/scanner email tu dong "ghe tham" (prefetch) lam kich hoat nham hanh dong nhay cam.
 */
@WebServlet("/confirm-account-action")
public class AccountActionConfirmServlet extends HttpServlet {

    private final UserAccountService userAccountService = new UserAccountService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String token = req.getParameter("token");
        if (token == null || token.isBlank()) {
            req.setAttribute("error", "Thieu token xac nhan");
        } else {
            req.setAttribute("token", token);
        }
        req.getRequestDispatcher("/WEB-INF/views/confirm-account-action.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String token = req.getParameter("token");
        String userAgent = req.getHeader("User-Agent");

        if (token == null || token.isBlank()) {
            req.setAttribute("error", "Thieu token xac nhan");
            req.getRequestDispatcher("/WEB-INF/views/confirm-account-action.jsp").forward(req, resp);
            return;
        }

        try {
            userAccountService.confirmPendingAction(token, userAgent);
            req.setAttribute("success",
                    "Da xac nhan thanh cong. Hanh dong da duoc thuc hien. Neu la yeu cau dat lai " +
                    "mat khau, mat khau tam moi (neu gui email thanh cong) da duoc gui toi email cua ban.");
        } catch (UserAccountService.ValidationException e) {
            req.setAttribute("error", e.getMessage());
        } catch (SQLException e) {
            getServletContext().log("Loi xac nhan hanh dong tai khoan", e);
            req.setAttribute("error", "He thong dang gap su co, vui long thu lai sau");
        }
        req.getRequestDispatcher("/WEB-INF/views/confirm-account-action.jsp").forward(req, resp);
    }
}
