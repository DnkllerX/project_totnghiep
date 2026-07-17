package com.shareholder.controller;

import com.shareholder.dao.ShareTransactionDAO;
import com.shareholder.dao.impl.ShareTransactionDAOImpl;
import com.shareholder.model.enums.TxStatus;
import com.shareholder.service.ShareTransferService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/app/admin/transfer-approval")
public class ShareTransferServlet extends HttpServlet {

    private final ShareTransferService transferService = new ShareTransferService();
    private final ShareTransactionDAO shareTransactionDAO = new ShareTransactionDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("pendingTransfers", shareTransactionDAO.findByStatus(TxStatus.PENDING));
        } catch (SQLException e) {
            getServletContext().log("Loi tai danh sach yeu cau chuyen nhuong", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/transfer-approval.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        int actorUserId = (Integer) session.getAttribute("userId");
        String userAgent = req.getHeader("User-Agent");
        String action = req.getParameter("action");

        try {
            int txId = Integer.parseInt(req.getParameter("txId"));
            if ("approve".equals(action)) {
                boolean approved = transferService.approveTransfer(txId, actorUserId, userAgent);
                if (!approved) {
                    req.setAttribute("error", "Co dong khong du so co phan de chuyen nhuong - yeu cau da bi tu choi");
                }
            } else if ("reject".equals(action)) {
                transferService.rejectTransfer(txId, actorUserId, userAgent);
            }
            resp.sendRedirect(req.getContextPath() + "/app/admin/transfer-approval");
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Du lieu khong hop le");
            doGet(req, resp);
        } catch (ShareTransferService.ValidationException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Loi xu ly chuyen nhuong", e);
            req.setAttribute("error", "He thong dang gap su co, vui long thu lai sau");
            doGet(req, resp);
        }
    }
}
