package com.shareholder.controller;

import com.shareholder.dao.DocumentDAO;
import com.shareholder.dao.impl.DocumentDAOImpl;
import com.shareholder.service.DocumentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet("/app/admin/documents")
@MultipartConfig(maxFileSize = 20 * 1024 * 1024) // 20MB, khop voi cac bao cao tai chinh PDF
public class DocumentServlet extends HttpServlet {

    private final DocumentService documentService = new DocumentService();
    private final DocumentDAO documentDAO = new DocumentDAOImpl();

    /** Thu muc luu tai lieu that su, nam ngoai webapp de tranh truy cap truc tiep khong qua kiem soat. */
    private static final String DOCUMENT_STORAGE_ROOT = System.getProperty(
            "document.storage.root", "/var/shareholder-system/documents");

    private static final java.util.Set<String> ALLOWED_EXTENSIONS =
            java.util.Set.of("pdf", "doc", "docx", "xls", "xlsx");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setAttribute("documents", documentDAO.findAll());
        } catch (SQLException e) {
            getServletContext().log("Loi tai danh sach tai lieu", e);
        }
        req.getRequestDispatcher("/WEB-INF/views/admin/documents.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        int actorUserId = (Integer) session.getAttribute("userId");
        String userAgent = req.getHeader("User-Agent");

        try {
            String title = req.getParameter("title");
            String description = req.getParameter("description");
            Part filePart = req.getPart("file");

            if (filePart == null || filePart.getSize() == 0) {
                req.setAttribute("error", "Vui long chon file de upload");
                doGet(req, resp);
                return;
            }

            String originalName = filePart.getSubmittedFileName();
            String extension = extractExtension(originalName);
            if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
                req.setAttribute("error", "Chi chap nhan file PDF/Word/Excel");
                doGet(req, resp);
                return;
            }

            // Ten file luu tren disk la UUID, KHONG dung ten goc client gui -> tranh path traversal
            String storedFileName = UUID.randomUUID() + "." + extension.toLowerCase();
            Path targetDir = Paths.get(DOCUMENT_STORAGE_ROOT, "financial");
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(storedFileName);

            try (InputStream in = filePart.getInputStream();
                 OutputStream out = Files.newOutputStream(targetFile, StandardOpenOption.CREATE_NEW)) {
                in.transferTo(out);
            }

            String fileUrl = "documents/financial/" + storedFileName;
            documentService.uploadDocument(title, description, fileUrl, actorUserId, userAgent);
            resp.sendRedirect(req.getContextPath() + "/app/admin/documents");
        } catch (DocumentService.ValidationException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        } catch (SQLException e) {
            getServletContext().log("Loi upload tai lieu", e);
            req.setAttribute("error", "He thong dang gap su co, vui long thu lai sau");
            doGet(req, resp);
        }
    }

    private String extractExtension(String fileName) {
        if (fileName == null) return null;
        int dotIdx = fileName.lastIndexOf('.');
        return dotIdx == -1 || dotIdx == fileName.length() - 1 ? null : fileName.substring(dotIdx + 1);
    }
}
