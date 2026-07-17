package com.shareholder.controller;

import com.shareholder.dao.ShareIssueDAO;
import com.shareholder.dao.impl.ShareIssueDAOImpl;
import com.shareholder.model.ShareIssue;
import com.shareholder.model.enums.IssueType;
import com.shareholder.service.ShareIssueService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@WebServlet("/app/admin/share-issue")
public class ShareIssueServlet extends HttpServlet {

    private final ShareIssueService shareIssueService = new ShareIssueService();
    private final ShareIssueDAO shareIssueDAO = new ShareIssueDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("issues", shareIssueDAO.findAll());
        } catch (SQLException e) {
            getServletContext().log("Loi tai danh sach dot phat hanh", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/share-issue.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        int actorUserId = (Integer) session.getAttribute("userId");
        String userAgent = req.getHeader("User-Agent");

        try {
            ShareIssue issue = new ShareIssue();
            issue.setTitle(req.getParameter("title"));
            IssueType issueType = IssueType.valueOf(req.getParameter("issueType"));
            issue.setIssueType(issueType);
            issue.setIssueDate(LocalDate.parse(req.getParameter("issueDate")));
            issue.setSnapshotDate(LocalDateTime.parse(req.getParameter("snapshotDate")));
            issue.setShareQuantity(Integer.parseInt(req.getParameter("shareQuantity")));

            String ratioStr = req.getParameter("issueRatio");
            issue.setIssueRatio(ratioStr != null && !ratioStr.isBlank() ? new BigDecimal(ratioStr) : null);

            String priceStr = req.getParameter("issuePrice");
            issue.setIssuePrice(issueType == IssueType.ISSUE && priceStr != null && !priceStr.isBlank()
                    ? new BigDecimal(priceStr) : null);

            issue.setStartDate(LocalDateTime.parse(req.getParameter("startDate")));
            issue.setEndDate(LocalDateTime.parse(req.getParameter("endDate")));
            issue.setDescription(req.getParameter("description"));

            shareIssueService.createIssue(issue, actorUserId, userAgent);
            resp.sendRedirect(req.getContextPath() + "/app/admin/share-issue");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", "Du lieu nhap khong hop le: " + e.getMessage());
            doGet(req, resp);
        } catch (ShareIssueService.ValidationException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Loi tao dot phat hanh", e);
            req.setAttribute("error", "He thong dang gap su co, vui long thu lai sau");
            doGet(req, resp);
        }
    }
}
