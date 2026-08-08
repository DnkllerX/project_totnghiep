package com.shareholder.controller;

import com.shareholder.dao.ShareholderDAO;
import com.shareholder.dao.ShareTransactionDAO;
import com.shareholder.dao.impl.ShareholderDAOImpl;
import com.shareholder.dao.impl.ShareTransactionDAOImpl;
import com.shareholder.service.ShareTransferService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/app/shareholder/transfer-request")
public class TransferRequestServlet extends HttpServlet {

    private final ShareTransferService transferService = new ShareTransferService();
    private final ShareholderDAO shareholderDAO = new ShareholderDAOImpl();
    private final ShareTransactionDAO shareTransactionDAO = new ShareTransactionDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");
        try {
            Optional<com.shareholder.model.Shareholder> shOpt = shareholderDAO.findByUserId(userId);
            if (shOpt.isPresent()) {
                int shareholderId = shOpt.get().getShareholderId();
                req.setAttribute("myTransfers", shareTransactionDAO.findByShareholderId(shareholderId));
                req.setAttribute("shareholderId", shareholderId);
            }
        } catch (SQLException e) {
            getServletContext().log("Loi tai lich su chuyen nhuong", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/shareholder/transfer-request.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");
        String userAgent = req.getHeader("User-Agent");

        try {
            Optional<com.shareholder.model.Shareholder> shOpt = shareholderDAO.findByUserId(userId);
            if (shOpt.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Tai khoan chua gan voi co dong nao");
                return;
            }
            int fromShareholderId = shOpt.get().getShareholderId();
            int toShareholderId = Integer.parseInt(req.getParameter("toShareholderId"));
            int quantity = Integer.parseInt(req.getParameter("quantity"));

            transferService.createTransferRequest(fromShareholderId, toShareholderId, quantity,
                    userId, userAgent);
            resp.sendRedirect(req.getContextPath() + "/app/shareholder/transfer-request");
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Du lieu nhap khong hop le");
            doGet(req, resp);
        } catch (ShareTransferService.ValidationException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Loi tao yeu cau chuyen nhuong", e);
            req.setAttribute("error", "He thong dang gap su co, vui long thu lai sau");
            doGet(req, resp);
        }
    }
}
