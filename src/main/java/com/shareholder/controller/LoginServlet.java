package com.shareholder.controller;

import com.shareholder.dao.ShareholderDAO;
import com.shareholder.dao.impl.ShareholderDAOImpl;
import com.shareholder.model.User;
import com.shareholder.model.Shareholder;
import com.shareholder.model.enums.UserRole;
import com.shareholder.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();
    private final ShareholderDAO shareholderDAO = new ShareholderDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String usernameOrEmail = req.getParameter("username");
        String password = req.getParameter("password");
        String userAgent = req.getHeader("User-Agent");

        if (usernameOrEmail == null || usernameOrEmail.isBlank() || password == null || password.isBlank()) {
            req.setAttribute("error", "Vui long nhap day du username va mat khau");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        try {
            AuthService.LoginOutcome outcome = authService.login(usernameOrEmail.trim(), password, userAgent);

            switch (outcome.result) {
                case SUCCESS -> {
                    User user = outcome.user;
                    // Sinh lai session moi sau khi dang nhap thanh cong - chong session fixation
                    HttpSession oldSession = req.getSession(false);
                    if (oldSession != null) oldSession.invalidate();
                    HttpSession session = req.getSession(true);
                    session.setAttribute("userId", user.getUserId());
                    session.setAttribute("username", user.getUsername());
                    session.setAttribute("role", user.getRole().name());
                    session.setMaxInactiveInterval(30 * 60);

                    if (user.getRole() == UserRole.SHAREHOLDER) {
                        Optional<Shareholder> shOpt = shareholderDAO.findByUserId(user.getUserId());
                        shOpt.ifPresent(sh -> session.setAttribute("shareholderId", sh.getShareholderId()));
                    }

                    resp.sendRedirect(req.getContextPath() + "/app/dashboard");
                }
                case ACCOUNT_LOCKED -> {
                    req.setAttribute("error", "Tai khoan da bi khoa. Lien he IT de duoc ho tro.");
                    req.getRequestDispatcher("/login.jsp").forward(req, resp);
                }
                default -> {
                    req.setAttribute("error", "Sai username hoac mat khau");
                    req.getRequestDispatcher("/login.jsp").forward(req, resp);
                }
            }
        } catch (SQLException e) {
            // Khong lo chi tiet loi DB ra ngoai cho client
            getServletContext().log("Loi dang nhap", e);
            req.setAttribute("error", "He thong dang gap su co, vui long thu lai sau");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
