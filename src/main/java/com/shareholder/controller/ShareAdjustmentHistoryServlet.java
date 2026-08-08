package com.shareholder.controller;

import com.shareholder.dao.ShareAdjustmentLogDAO;
import com.shareholder.dao.ShareholderDAO;
import com.shareholder.dao.UserDAO;
import com.shareholder.dao.impl.ShareAdjustmentLogDAOImpl;
import com.shareholder.dao.impl.ShareholderDAOImpl;
import com.shareholder.dao.impl.UserDAOImpl;
import com.shareholder.model.Shareholder;
import com.shareholder.model.ShareAdjustmentLog;
import com.shareholder.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Trang "Lich su dieu chinh co phan" (ADMIN) - chi doc, hien thi lai SHARE_ADJUSTMENT_LOGS da
 * duoc ghi san boi ShareAdjustmentService.adjustShareQuantity() nhung truoc gio khong co UI nao
 * xem lai duoc. Ten co dong/nguoi thuc hien duoc resolve o day cho de doc, khong sua DAO.
 */
@WebServlet("/app/admin/share-adjustment-history")
public class ShareAdjustmentHistoryServlet extends HttpServlet {

    private final ShareAdjustmentLogDAO adjustmentLogDAO = new ShareAdjustmentLogDAOImpl();
    private final ShareholderDAO shareholderDAO = new ShareholderDAOImpl();
    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<ShareAdjustmentLog> logs = adjustmentLogDAO.findAll();
            // Moi nhat len dau, de admin xem lich su gan day truoc.
            logs.sort((a, b) -> {
                if (a.getAdjustedAt() == null || b.getAdjustedAt() == null) return 0;
                return b.getAdjustedAt().compareTo(a.getAdjustedAt());
            });

            Map<Integer, String> shareholderNameById = new HashMap<>();
            for (Shareholder sh : shareholderDAO.findAll()) {
                shareholderNameById.put(sh.getShareholderId(), sh.getFullName());
            }
            Map<Integer, String> userNameById = new HashMap<>();
            for (User u : userDAO.findAll()) {
                userNameById.put(u.getUserId(), u.getUsername());
            }

            req.setAttribute("logs", logs);
            req.setAttribute("shareholderNameById", shareholderNameById);
            req.setAttribute("userNameById", userNameById);
        } catch (SQLException e) {
            getServletContext().log("Loi tai lich su dieu chinh co phan", e);
            req.setAttribute("error", "Khong tai duoc lich su dieu chinh co phan");
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/share-adjustment-history.jsp").forward(req, resp);
    }
}
