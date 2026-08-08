package com.shareholder.controller;

import com.shareholder.dao.DocumentDAO;
import com.shareholder.dao.impl.DocumentDAOImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Trang CO DONG xem danh sach tai lieu (chi doc). Dung lai DocumentDAO da co san -
 * KHONG co doGet upload/xoa nao o day, tach biet hoan toan voi DocumentServlet (/app/admin/documents)
 * von danh cho ADMIN quan ly tai lieu. Duoc AuthFilter chan chi cho role SHAREHOLDER.
 */
@WebServlet("/app/shareholder/documents")
public class ShareholderDocumentServlet extends HttpServlet {

    private final DocumentDAO documentDAO = new DocumentDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("documents", documentDAO.findAll());
        } catch (SQLException e) {
            getServletContext().log("Loi tai danh sach tai lieu (co dong)", e);
            req.setAttribute("error", "Khong tai duoc danh sach tai lieu");
        }
        req.getRequestDispatcher("/WEB-INF/views/shareholder/documents.jsp").forward(req, resp);
    }
}
