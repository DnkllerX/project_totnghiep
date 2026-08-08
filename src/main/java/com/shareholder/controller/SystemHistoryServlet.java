package com.shareholder.controller;

import com.shareholder.dao.AuditLogDAO;
import com.shareholder.dao.impl.AuditLogDAOImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Trang "Lich su he thong" (IT) - chi doc, hien thi lai AUDIT_LOGS da duoc ghi san
 * boi cac service khac (AuthService, ShareTransferService, ResolutionService, ...).
 * Khong co thao tac ghi/xoa o day - day la trang audit, du lieu phai bat bien.
 */
@WebServlet("/app/it/system-history")
public class SystemHistoryServlet extends HttpServlet {

    private final AuditLogDAO auditLogDAO = new AuditLogDAOImpl();

    /** Gioi han so dong tra ve, tranh load qua nhieu du lieu lam nang trang khi AUDIT_LOGS phinh to. */
    private static final int DEFAULT_LIMIT = 300;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("logs", auditLogDAO.findAll(DEFAULT_LIMIT));
        } catch (SQLException e) {
            getServletContext().log("Loi tai lich su he thong (audit log)", e);
            req.setAttribute("error", "Khong tai duoc lich su he thong");
        }
        req.getRequestDispatcher("/WEB-INF/views/it/system-history.jsp").forward(req, resp);
    }
}
