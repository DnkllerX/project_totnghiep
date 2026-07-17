package com.shareholder.controller;

import com.shareholder.model.enums.UserRole;
import com.shareholder.model.enums.UserSortOption;
import com.shareholder.model.enums.UserStatus;
import com.shareholder.service.UserAccountService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/app/admin/user-management")
public class UserAccountServlet extends HttpServlet {

    private final UserAccountService userAccountService = new UserAccountService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String roleParam = req.getParameter("role");
        String statusParam = req.getParameter("status");
        String sortParam = req.getParameter("sort");

        UserRole role = parseEnumOrNull(UserRole.class, roleParam);
        UserStatus status = parseEnumOrNull(UserStatus.class, statusParam);
        UserSortOption sort = parseEnumOrNull(UserSortOption.class, sortParam);
        if (sort == null) sort = UserSortOption.NEWEST_FIRST;

        try {
            req.setAttribute("users", userAccountService.search(username, email, role, status, sort));
        } catch (SQLException e) {
            getServletContext().log("Loi tai danh sach tai khoan", e);
            req.setAttribute("error", "Khong tai duoc danh sach tai khoan");
        }

        // Giu lai gia tri filter tren form sau khi submit
        req.setAttribute("filterUsername", username);
        req.setAttribute("filterEmail", email);
        req.setAttribute("filterRole", roleParam);
        req.setAttribute("filterStatus", statusParam);
        req.setAttribute("filterSort", sort.name());

        req.getRequestDispatcher("/WEB-INF/views/it/user-management.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        int actorUserId = (Integer) session.getAttribute("userId");
        String userAgent = req.getHeader("User-Agent");
        String action = req.getParameter("action");

        try {
            switch (action) {
                case "create" -> {
                    String username = req.getParameter("username");
                    String email = req.getParameter("email");
                    String password = req.getParameter("password");
                    UserRole role = UserRole.valueOf(req.getParameter("role"));
                    userAccountService.createAccount(username, email, password, role, actorUserId, userAgent);
                }
                case "update" -> {
                    int targetId = Integer.parseInt(req.getParameter("userId"));
                    String username = req.getParameter("username");
                    String email = req.getParameter("email");
                    UserRole role = UserRole.valueOf(req.getParameter("role"));
                    userAccountService.updateAccount(targetId, username, email, role, actorUserId, userAgent);
                }
                case "lock" -> {
                    int targetId = Integer.parseInt(req.getParameter("userId"));
                    userAccountService.lockAccount(targetId, actorUserId, userAgent);
                }
                case "unlock" -> {
                    int targetId = Integer.parseInt(req.getParameter("userId"));
                    userAccountService.unlockAccount(targetId, actorUserId, userAgent);
                }
                case "reset-password" -> {
                    int targetId = Integer.parseInt(req.getParameter("userId"));
                    String tempPassword = userAccountService.resetPassword(targetId, actorUserId, userAgent);
                    // Hien thi 1 LAN DUY NHAT qua flash attribute (session), khong luu lai o dau khac
                    req.getSession().setAttribute("flashTempPassword", tempPassword);
                }
                default -> throw new IllegalArgumentException("Hanh dong khong hop le");
            }
            resp.sendRedirect(req.getContextPath() + "/app/admin/user-management");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", "Du lieu nhap khong hop le");
            doGet(req, resp);
        } catch (UserAccountService.ValidationException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Loi quan ly tai khoan", e);
            req.setAttribute("error", "He thong dang gap su co, vui long thu lai sau");
            doGet(req, resp);
        }
    }

    /** Parse enum tu query string, tra ve null neu rong hoac gia tri khong hop le (khong nem loi). */
    private <E extends Enum<E>> E parseEnumOrNull(Class<E> enumClass, String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
