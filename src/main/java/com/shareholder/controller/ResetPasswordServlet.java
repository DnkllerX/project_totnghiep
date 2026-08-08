package com.shareholder.controller;

import com.shareholder.dao.UserDAO;
import com.shareholder.dao.impl.UserDAOImpl;
import com.shareholder.model.User;
import com.shareholder.util.JwtUtil;
import com.shareholder.util.PasswordUtil;
import com.shareholder.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Trang dat lai mat khau qua link JWT (tu ForgotPasswordServlet gui qua email). Cong khai, KHONG
 * yeu cau dang nhap, nam ngoai /app/* nen khong bi AuthFilter chan.
 */
@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String token = req.getParameter("token");
        Integer userId = validate(token);

        if (userId == null) {
            req.setAttribute("error",
                    "Link dat lai mat khau khong hop le, da het han, hoac da duoc su dung roi. " +
                    "Vui long yeu cau link moi.");
        } else {
            req.setAttribute("token", token);
        }
        req.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String token = req.getParameter("token");
        String newPassword = req.getParameter("newPassword");
        String confirmNewPassword = req.getParameter("confirmNewPassword");

        Integer userId = validate(token);
        if (userId == null) {
            req.setAttribute("error",
                    "Link dat lai mat khau khong hop le, da het han, hoac da duoc su dung roi. " +
                    "Vui long yeu cau link moi.");
            req.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(req, resp);
            return;
        }

        if (!ValidationUtil.isPasswordNonEmpty(newPassword)) {
            req.setAttribute("error", "Mat khau moi khong duoc de trong");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(req, resp);
            return;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            req.setAttribute("error", "Xac nhan mat khau khong khop");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(req, resp);
            return;
        }
        if (!ValidationUtil.isStrongPassword(newPassword)) {
            req.setAttribute("error", "Mat khau moi phai co it nhat 8 ky tu, gom ca chu va so");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(req, resp);
            return;
        }

        try {
            userDAO.updatePasswordHash(userId, PasswordUtil.hash(newPassword));
            // Doi mat khau xong -> hash trong DB da thay doi -> token nay (nhung claim pwdHash cu)
            // se KHONG con hop le neu ai co gang dung lai (xem JwtUtil.validatePasswordResetToken).
            resp.sendRedirect(req.getContextPath() + "/login?passwordChanged=1");
        } catch (SQLException e) {
            getServletContext().log("Loi dat lai mat khau", e);
            req.setAttribute("error", "He thong dang gap su co, vui long thu lai sau");
            req.setAttribute("token", token);
            req.getRequestDispatcher("/WEB-INF/views/reset-password.jsp").forward(req, resp);
        }
    }

    /** @return userId neu token hop le va chua bi dung, null neu khong */
    private Integer validate(String token) {
        if (token == null || token.isBlank()) return null;

        JwtUtil.ResetTokenPayload payload = JwtUtil.parsePasswordResetToken(token);
        if (payload == null) return null; // sai chu ky / het han / khong dung purpose

        try {
            Optional<User> userOpt = userDAO.findById(payload.userId);
            if (userOpt.isEmpty()) return null;

            // So sanh hash mat khau nhung trong token luc sinh voi hash HIEN TAI trong DB - neu
            // mat khau da doi (vd token nay da duoc dung 1 lan roi, hoac user da tu doi mat khau
            // cach khac), 2 hash se lech -> token nay khong con hop le nua.
            if (!payload.pwdHashClaim.equals(userOpt.get().getPasswordHash())) return null;

            return payload.userId;
        } catch (SQLException e) {
            getServletContext().log("Loi kiem tra token reset mat khau", e);
            return null;
        }
    }
}
