package com.shareholder.controller;

import com.shareholder.dao.ShareholderDAO;
import com.shareholder.dao.ShareIssueDetailDAO;
import com.shareholder.dao.impl.ShareholderDAOImpl;
import com.shareholder.dao.impl.ShareIssueDetailDAOImpl;
import com.shareholder.service.ShareIssueService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Trang ky nhan cua co dong. Frontend se dung <canvas> de ve chu ky, xuat ra base64
 * (canvas.toDataURL('image/png')) roi gui len qua truong "signatureData" cua form POST nay.
 */
@WebServlet("/app/shareholder/sign")
public class SignIssueServlet extends HttpServlet {

    private final ShareIssueService shareIssueService = new ShareIssueService();
    private final ShareIssueDetailDAO issueDetailDAO = new ShareIssueDetailDAOImpl();
    private final ShareholderDAO shareholderDAO = new ShareholderDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        int userId = (Integer) session.getAttribute("userId");
        try {
            Optional<com.shareholder.model.Shareholder> shOpt = shareholderDAO.findByUserId(userId);
            if (shOpt.isPresent()) {
                req.setAttribute("pendingSignatures",
                        issueDetailDAO.findByShareholderId(shOpt.get().getShareholderId()));
            }
        } catch (SQLException e) {
            getServletContext().log("Loi tai danh sach cho ky", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/shareholder/sign.jsp").forward(req, resp);
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
            int issueDetailId = Integer.parseInt(req.getParameter("issueDetailId"));
            String signatureData = req.getParameter("signatureData"); // base64 tu canvas

            boolean signed = shareIssueService.signIssueDetail(
                    issueDetailId, shOpt.get().getShareholderId(), signatureData, userAgent);

            if (!signed) {
                req.setAttribute("error", "Ky nhan khong thanh cong, vui long thu lai");
            }
            resp.sendRedirect(req.getContextPath() + "/app/shareholder/sign");
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Du lieu khong hop le");
            doGet(req, resp);
        } catch (ShareIssueService.ValidationException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Loi ky nhan co phan", e);
            req.setAttribute("error", "He thong dang gap su co, vui long thu lai sau");
            doGet(req, resp);
        }
    }
}
