package com.shareholder.controller;

import com.shareholder.dao.ResolutionDAO;
import com.shareholder.dao.ShareholderDAO;
import com.shareholder.dao.ShareSnapshotDAO;
import com.shareholder.dao.impl.ResolutionDAOImpl;
import com.shareholder.dao.impl.ShareholderDAOImpl;
import com.shareholder.dao.impl.ShareSnapshotDAOImpl;
import com.shareholder.model.ShareSnapshot;
import com.shareholder.model.enums.VoteValue;
import com.shareholder.service.ResolutionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/app/shareholder/vote")
public class VoteServlet extends HttpServlet {

    private final ResolutionService resolutionService = new ResolutionService();
    private final ResolutionDAO resolutionDAO = new ResolutionDAOImpl();
    private final ShareholderDAO shareholderDAO = new ShareholderDAOImpl();
    private final ShareSnapshotDAO snapshotDAO = new ShareSnapshotDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("resolutions", resolutionDAO.findAll());
        } catch (SQLException e) {
            getServletContext().log("Loi tai danh sach nghi quyet", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/shareholder/vote.jsp").forward(req, resp);
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
            int resolutionId = Integer.parseInt(req.getParameter("resolutionId"));
            VoteValue voteValue = VoteValue.valueOf(req.getParameter("voteValue"));

            Optional<ShareSnapshot> snapshotOpt = snapshotDAO.findLatestByResolutionId(resolutionId);
            if (snapshotOpt.isEmpty()) {
                req.setAttribute("error", "Khong tim thay danh sach chot quyen bieu quyet cho nghi quyet nay");
                doGet(req, resp);
                return;
            }

            resolutionService.castVote(resolutionId, shOpt.get().getShareholderId(),
                    snapshotOpt.get().getSnapshotId(), voteValue, userAgent);
            resp.sendRedirect(req.getContextPath() + "/app/shareholder/vote");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", "Du lieu khong hop le");
            doGet(req, resp);
        } catch (ResolutionService.ValidationException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Loi bieu quyet", e);
            req.setAttribute("error", "He thong dang gap su co, vui long thu lai sau");
            doGet(req, resp);
        }
    }
}
