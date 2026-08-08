package com.shareholder.controller;

import com.shareholder.model.enums.UserRole;
import com.shareholder.model.enums.UserSortOption;
import com.shareholder.model.enums.UserStatus;
import com.shareholder.service.EmailService;
import com.shareholder.service.ShareholderService;
import com.shareholder.service.UserAccountService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

@WebServlet("/app/it/user-management")
public class UserAccountServlet extends HttpServlet {

    private final UserAccountService userAccountService = new UserAccountService();
    private final EmailService emailService = new EmailService();
    private final ShareholderService shareholderService = new ShareholderService();

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
                    // Trang nay CHI danh cho IT (xem AuthFilter: /app/it/user-management -> IT).
                    // IT CHI duoc tao tai khoan SHAREHOLDER (khong duoc chon ADMIN/IT tu day nua -
                    // muon tao ADMIN/IT phai qua SeedDatabase.java/DBA truc tiep, ngoai pham vi web UI).
                    // Dung lai dung logic voi tu dang ky cong khai (/register): tao du ho so
                    // SHAREHOLDERS+SHARES(0), status=LOCKED, CHO ADMIN DUYET o /app/admin/shareholders -
                    // khong active ngay nhu truoc day (truoc day tao xong la dang nhap duoc luon).
                    String fullName = req.getParameter("fullName");
                    String citizenId = req.getParameter("citizenId");
                    String phone = req.getParameter("phone");
                    String address = req.getParameter("address");
                    String nationality = req.getParameter("nationality");
                    LocalDate birthDate = null;
                    String birthDateStr = req.getParameter("birthDate");
                    if (birthDateStr != null && !birthDateStr.isBlank()) {
                        try {
                            birthDate = LocalDate.parse(birthDateStr);
                        } catch (Exception ignored) {
                            throw new ShareholderService.ValidationException("Ngay sinh khong hop le");
                        }
                    }
                    shareholderService.registerShareholderAccount(username, email, password, fullName,
                            citizenId, phone, address, birthDate, nationality, actorUserId, userAgent);
                    req.getSession().setAttribute("flashMessage",
                            "Da tao tai khoan co dong moi (trang thai: cho ADMIN duyet). Tai khoan chua the " +
                            "dang nhap cho den khi duoc phe duyet o trang Quan ly Co dong cua ADMIN.");
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
                    Optional<UserAccountService.StepUpEmailInfo> stepUp =
                            userAccountService.checkStepUp(UserAccountService.PendingAction.LOCK, targetId, actorUserId);
                    if (stepUp.isPresent()) {
                        sendStepUpEmail(req, stepUp.get());
                        req.getSession().setAttribute("flashMessage",
                                "Tai khoan nay la ADMIN/IT nen KHONG the khoa ngay - da gui email " +
                                "xac nhan toi chinh chu tai khoan, hanh dong chi co hieu luc khi ho tu xac nhan.");
                    } else {
                        userAccountService.lockAccount(targetId, actorUserId, userAgent);
                    }
                }
                case "unlock" -> {
                    int targetId = Integer.parseInt(req.getParameter("userId"));
                    userAccountService.unlockAccount(targetId, actorUserId, userAgent);
                }
                case "reset-password" -> {
                    int targetId = Integer.parseInt(req.getParameter("userId"));
                    Optional<UserAccountService.StepUpEmailInfo> stepUp = userAccountService.checkStepUp(
                            UserAccountService.PendingAction.RESET_PASSWORD, targetId, actorUserId);
                    if (stepUp.isPresent()) {
                        sendStepUpEmail(req, stepUp.get());
                        req.getSession().setAttribute("flashMessage",
                                "Tai khoan nay la ADMIN/IT nen KHONG the dat lai mat khau ngay - da gui email " +
                                "xac nhan toi chinh chu tai khoan, mat khau CHUA doi cho den khi ho tu xac nhan.");
                    } else {
                        UserAccountService.ResetPasswordResult result =
                                userAccountService.resetPassword(targetId, actorUserId, userAgent);
                        if (result.isEmailSent()) {
                            req.getSession().setAttribute("flashMessage",
                                    "Da gui mat khau moi qua email cho nguoi dung.");
                        } else {
                            // SMTP loi - fallback hien thi 1 LAN DUY NHAT qua flash attribute (session)
                            req.getSession().setAttribute("flashMessage",
                                    "Khong gui duoc email (kiem tra lai cau hinh SMTP). Mat khau tam thoi ben duoi, " +
                                    "vui long cung cap thu cong cho nguoi dung:");
                            req.getSession().setAttribute("flashTempPassword", result.getTempPasswordFallback());
                        }
                    }
                }
                default -> throw new IllegalArgumentException("Hanh dong khong hop le");
            }
            resp.sendRedirect(req.getContextPath() + "/app/it/user-management");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", "Du lieu nhap khong hop le");
            doGet(req, resp);
        } catch (UserAccountService.ValidationException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        } catch (ShareholderService.ValidationException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Loi quan ly tai khoan", e);
            req.setAttribute("error", "He thong dang gap su co, vui long thu lai sau");
            doGet(req, resp);
        }
    }

    /** Xay link xac nhan (can HttpServletRequest de lay scheme/host/port - vi vay khong lam o Service). */
    private void sendStepUpEmail(HttpServletRequest req, UserAccountService.StepUpEmailInfo info) {
        try {
            String confirmLink = req.getScheme() + "://" + req.getServerName()
                    + (req.getServerPort() != 80 && req.getServerPort() != 443 ? ":" + req.getServerPort() : "")
                    + req.getContextPath() + "/confirm-account-action?token=" + info.token;
            emailService.sendActionConfirmationEmail(
                    info.targetEmail, info.targetDisplayName, info.actorUsername, info.actionLabelVi, confirmLink);
        } catch (EmailService.EmailException e) {
            getServletContext().log("Gui email xac nhan step-up that bai", e);
            // Khong lam gi them - flash message chung da bao IT la da "gui email", neu that bai
            // that thi target se khong nhan duoc gi va tu lien he lai, chap nhan duoc cho pham vi nay.
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
