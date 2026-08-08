package com.shareholder.controller;

import com.shareholder.dao.DocumentDAO;
import com.shareholder.dao.impl.DocumentDAOImpl;
import com.shareholder.model.Document;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Trang xem truoc tai lieu dang HTML rieng (giong cafef.vn: co header/tieu de/mo ta ben ngoai,
 * nhung PDF nhung vao giua qua iframe) - thay vi mo thang file PDF chiem het tab bang plugin
 * mac dinh cua trinh duyet nhu truoc day. Dung chung cho ca ADMIN va SHAREHOLDER (khong gioi han
 * rieng trong AuthFilter.ROLE_RULES, giong thiet ke cua /app/documents/download).
 */
@WebServlet("/app/documents/view")
public class DocumentViewerServlet extends HttpServlet {

    private final DocumentDAO documentDAO = new DocumentDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int documentId;
        try {
            documentId = Integer.parseInt(req.getParameter("id"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "documentId khong hop le");
            return;
        }

        try {
            Optional<Document> docOpt = documentDAO.findById(documentId);
            if (docOpt.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Khong tim thay tai lieu");
                return;
            }
            req.setAttribute("document", docOpt.get());
            // Sidebar chi liet ke tai lieu khac; khong hien thi bat ky widget thi truong/search nao.
            req.setAttribute("relatedDocuments", documentDAO.findAll().stream()
                    .filter(document -> document.getDocumentId() != documentId)
                    .limit(8)
                    .toList());
        } catch (SQLException e) {
            getServletContext().log("Loi tai thong tin tai lieu de xem truoc", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        req.getRequestDispatcher("/WEB-INF/views/common/document-viewer.jsp").forward(req, resp);
    }
}
