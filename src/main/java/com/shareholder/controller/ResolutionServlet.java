package com.shareholder.controller;

import com.shareholder.dao.ResolutionDAO;
import com.shareholder.dao.impl.ResolutionDAOImpl;
import com.shareholder.model.Resolution;
import com.shareholder.service.ResolutionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

@WebServlet("/app/admin/resolution")
public class ResolutionServlet extends HttpServlet {

    private final ResolutionService resolutionService = new ResolutionService();
    private final ResolutionDAO resolutionDAO = new ResolutionDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("resolutions", resolutionDAO.findAll());
        } catch (SQLException e) {
            getServletContext().log("Loi tai danh sach nghi quyet", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/resolution.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        int actorUserId = (Integer) session.getAttribute("userId");
        String userAgent = req.getHeader("User-Agent");

        try {
            Resolution r = new Resolution();
            r.setTitle(req.getParameter("title"));
            r.setDescription(req.getParameter("description"));
            r.setStartTime(LocalDateTime.parse(req.getParameter("startTime")));
            r.setEndTime(LocalDateTime.parse(req.getParameter("endTime")));

            resolutionService.createResolutionWithSnapshot(r, actorUserId, userAgent);
            resp.sendRedirect(req.getContextPath() + "/app/admin/resolution");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", "Du lieu nhap khong hop le");
            doGet(req, resp);
        } catch (ResolutionService.ValidationException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Loi tao nghi quyet", e);
            req.setAttribute("error", "He thong dang gap su co, vui long thu lai sau");
            doGet(req, resp);
        }
    }
}
