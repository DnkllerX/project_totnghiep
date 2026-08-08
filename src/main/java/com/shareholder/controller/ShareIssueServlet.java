package com.shareholder.controller;

import com.shareholder.dao.ShareDAO;
import com.shareholder.dao.ShareIssueDAO;
import com.shareholder.dao.impl.ShareDAOImpl;
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
import java.time.LocalTime;

/**
 * Ghi chu ve cac dieu chinh nghiep vu (thang 07/2026):
 *  - He thong hien tai CHI ho tro phat hanh co tuc bang co phieu (DIVIDEND). Loai ISSUE
 *    ("quyen mua co gia phat hanh") da bi xoa hoan toan - ca UI, code (model/DAO/service/servlet)
 *    lan schema DB (cot issue_price + constraint CHK_Issues_Price da drop, CHK_Issues_Type
 *    gio chi cho phep 'DIVIDEND'). Xem migration tuong ung trong snapshot01db.sql.
 *  - "Tong so co phan phat hanh" KHONG con do admin nhap tay - duoc tinh tu dong tu ty le
 *    (issueRatio) ap len tong SHARES hien co (xem ShareDAO.estimateIssueQuantity), lam tron
 *    xuong tung co dong roi cong lai, giong het cong thuc that su dung luc chot snapshot
 *    (ShareIssueDetailDAOImpl.generateFromSnapshot). Day la con so DU KIEN - con so chinh xac
 *    chi co tai thoi diem snapshot (co the lech neu co giao dich mua/ban giua chung).
 *  - "Ngay phat hanh" duoc doi ten hien thi thanh "Ngay dang ky cuoi cung" (dung
 *    thuat ngu nghiep vu chung khoan) - ten tham so/cot DB van la issueDate/issue_date de
 *    khong phai doi schema.
 *  - "Snapshot Date" khong con do admin chon - LUON tu dong = "Ngay dang ky cuoi cung" (1)
 *    + 1 ngay, luc 00:00.
 *  - Ky nhan: gio bat dau luon la 00:00:00 cua ngay bat dau, gio ket thuc luon la 23:59:59
 *    cua ngay ket thuc (admin chi chon NGAY, khong chon gio) - tranh truong hop co dong bi
 *    cat mat quyen ky vi lech gio trong ngay.
 */
@WebServlet("/app/admin/share-issue")
public class ShareIssueServlet extends HttpServlet {

    private final ShareIssueService shareIssueService = new ShareIssueService();
    private final ShareIssueDAO shareIssueDAO = new ShareIssueDAOImpl();
    private final ShareDAO shareDAO = new ShareDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Endpoint xem truoc: GET .../share-issue?action=preview&ratio=0.1 -> tra JSON
        // { "totalShares": <long> } de JS tren form hien "So co phan du kien phat hanh"
        // ngay khi admin go ty le, khong can submit form.
        if ("preview".equals(req.getParameter("action"))) {
            writePreviewJson(req, resp);
            return;
        }

        try {
            req.setAttribute("issues", shareIssueDAO.findAll());
            req.setAttribute("now", java.time.LocalDateTime.now());
        } catch (SQLException e) {
            getServletContext().log("Loi tai danh sach dot phat hanh", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/share-issue.jsp").forward(req, resp);
    }

    private void writePreviewJson(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String ratioStr = req.getParameter("ratio");
        try {
            BigDecimal ratio = new BigDecimal(ratioStr);
            if (ratio.signum() <= 0) throw new NumberFormatException("ratio <= 0");
            long total = shareDAO.estimateIssueQuantity(ratio);
            resp.getWriter().write("{\"ok\":true,\"totalShares\":" + total + "}");
        } catch (NumberFormatException e) {
            resp.getWriter().write("{\"ok\":false,\"error\":\"Ty le khong hop le\"}");
        } catch (SQLException e) {
            getServletContext().log("Loi uoc tinh so co phan phat hanh", e);
            resp.getWriter().write("{\"ok\":false,\"error\":\"Loi he thong, thu lai sau\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        int actorUserId = (Integer) session.getAttribute("userId");
        String userAgent = req.getHeader("User-Agent");

        try {
            if ("delete".equals(req.getParameter("action"))) {
                shareIssueService.deleteUnopenedIssue(
                        Integer.parseInt(req.getParameter("issueId")), actorUserId, userAgent);
                resp.sendRedirect(req.getContextPath() + "/app/admin/share-issue");
                return;
            }
            ShareIssue issue = new ShareIssue();
            issue.setTitle(req.getParameter("title"));

            // (1) Ngay dang ky cuoi cung - luu vao cung cot issueDate/issue_date cu.
            LocalDate lastRegistrationDate = LocalDate.parse(req.getParameter("issueDate"));
            issue.setIssueDate(lastRegistrationDate);

            // (2) Snapshot Date = ngay dang ky cuoi cung + 1 ngay, 00:00 - KHONG doc tu request nua,
            // admin khong duoc tu chon de tranh nhap sai thu tu nghiep vu.
            issue.setSnapshotDate(lastRegistrationDate.plusDays(1).atStartOfDay());

            // He thong chi con DIVIDEND - ep cung, bo qua bat ky gia tri "issueType" nao client
            // co the gui len (phong thu neu co request thu cong bo qua UI). Truong issuePrice
            // da bi xoa hoan toan khoi model/DB, khong con ton tai de gan.
            issue.setIssueType(IssueType.DIVIDEND);

            String ratioStr = req.getParameter("issueRatio");
            if (ratioStr == null || ratioStr.isBlank()) {
                throw new IllegalArgumentException("Ty le phat hanh la bat buoc");
            }
            BigDecimal ratio = new BigDecimal(ratioStr);
            issue.setIssueRatio(ratio);

            // Tong so co phan phat hanh: tinh lai o server, KHONG tin gia tri client gui (neu co) -
            // dung dung cong thuc se ap dung that su luc chot snapshot.
            long estimatedTotal = shareDAO.estimateIssueQuantity(ratio);
            if (estimatedTotal <= 0) {
                throw new IllegalArgumentException(
                        "Ty le nay cho ra tong so co phan du kien = 0 (kiem tra lai ty le hoac "
                                + "danh sach co dong dang co co phan)");
            }
            issue.setShareQuantity((int) estimatedTotal);

            // (3)/(4) Ky nhan: admin chi chon NGAY, gio duoc ep cung 00:00:00 -> 23:59:59.
            LocalDate startDay = LocalDate.parse(req.getParameter("startDate"));
            LocalDate endDay = LocalDate.parse(req.getParameter("endDate"));
            issue.setStartDate(startDay.atStartOfDay());
            issue.setEndDate(endDay.atTime(LocalTime.of(23, 59, 59)));

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
