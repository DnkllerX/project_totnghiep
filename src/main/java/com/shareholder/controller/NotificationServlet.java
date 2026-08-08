package com.shareholder.controller;

import com.shareholder.dao.ShareholderDAO;
import com.shareholder.dao.impl.ShareholderDAOImpl;
import com.shareholder.service.NotificationService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Optional;

/**
 * Trang "Thong bao" cua co dong - liet ke ket qua bieu quyet da tham gia va cac dot phat hanh
 * co tuc lien quan, ghep tu du lieu that (xem NotificationService), khong luu bang rieng.
 */
@WebServlet("/app/shareholder/notifications")
public class NotificationServlet extends HttpServlet {

    private final NotificationService notificationService = new NotificationService();
    private final ShareholderDAO shareholderDAO = new ShareholderDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        try {
            Optional<com.shareholder.model.Shareholder> shOpt = shareholderDAO.findByUserId(userId);
            if (shOpt.isPresent()) {
                req.setAttribute("notifications", notificationService.buildForShareholder(shOpt.get().getShareholderId()));
            } else {
                req.setAttribute("notifications", Collections.emptyList());
            }
        } catch (SQLException e) {
            getServletContext().log("Loi tai danh sach thong bao", e);
            req.setAttribute("error", "He thong dang gap su co, vui long thu lai sau");
        }
        req.getRequestDispatcher("/WEB-INF/views/shareholder/notifications.jsp").forward(req, resp);
    }
}
