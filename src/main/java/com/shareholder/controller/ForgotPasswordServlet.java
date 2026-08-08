package com.shareholder.controller;

import com.shareholder.dao.ShareholderDAO;
import com.shareholder.dao.UserDAO;
import com.shareholder.dao.impl.ShareholderDAOImpl;
import com.shareholder.dao.impl.UserDAOImpl;
import com.shareholder.model.User;
import com.shareholder.service.EmailService;
import com.shareholder.util.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Trang "Quen mat khau" - cong khai, KHONG yeu cau dang nhap, nam ngoai /app/* nen KHONG bi
 * AuthFilter chan (giong het /login, /register). Sinh 1 JWT nhung claim "pwdHash" = hash mat khau
 * HIEN TAI (xem JwtUtil.generatePasswordResetToken) - tu vo hieu sau khi doi mat khau, KHONG can
 * luu token vao DB.
 */
@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAOImpl();
    private final ShareholderDAO shareholderDAO = new ShareholderDAOImpl();
    private final EmailService emailService = new EmailService();

    private static final String GENERIC_MESSAGE =
            "Neu email ton tai trong he thong, chung toi da gui link dat lai mat khau. " +
            "Vui long kiem tra hop thu (ke ca thu muc Spam).";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = req.getParameter("email");

        // Luon tra ve THONG BAO GIONG NHAU du email co ton tai hay khong - chong do email
        // (user enumeration): ke tan cong khong the biet email nao da dang ky trong he thong.
        req.setAttribute("success", GENERIC_MESSAGE);

        if (email != null && !email.isBlank()) {
            try {
                Optional<User> userOpt = userDAO.findByEmail(email.trim());
                if (userOpt.isPresent()) {
                    sendResetLink(req, userOpt.get());
                }
            } catch (SQLException e) {
                getServletContext().log("Loi xu ly quen mat khau", e);
                // Van hien thong bao chung, khong lo loi he thong ra ngoai
            }
        }

        req.getRequestDispatcher("/WEB-INF/views/forgot-password.jsp").forward(req, resp);
    }

    private void sendResetLink(HttpServletRequest req, User user) {
        try {
            String token = JwtUtil.generatePasswordResetToken(user.getUserId(), user.getPasswordHash());
            String resetLink = req.getScheme() + "://" + req.getServerName()
                    + (req.getServerPort() != 80 && req.getServerPort() != 443 ? ":" + req.getServerPort() : "")
                    + req.getContextPath() + "/reset-password?token=" + token;

            String displayName = shareholderDAO.findByUserId(user.getUserId())
                    .map(com.shareholder.model.Shareholder::getFullName)
                    .orElse(user.getUsername());

            emailService.sendPasswordResetLinkEmail(user.getEmail(), displayName, resetLink);
        } catch (SQLException | EmailService.EmailException e) {
            getServletContext().log("Gui email quen mat khau that bai cho userId=" + user.getUserId(), e);
            // Khong lam gi them - nguoi dung van thay thong bao chung, khong biet email co ton tai
            // hay khong, khong biet SMTP co loi hay khong.
        }
    }
}
