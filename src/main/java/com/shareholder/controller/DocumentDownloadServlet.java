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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Endpoint dung chung cho ca ADMIN va SHAREHOLDER de tai file tai lieu (KHONG co doGet upload/xoa).
 * Khong dung /app/admin/* hay /app/shareholder/* rieng - AuthFilter khong co rule rieng cho path nay
 * nen chi can dang nhap (bat ky role nao) la tai duoc, giong tinh chat "tai lieu chung cua cong ty".
 * Neu sau nay muon gioi han (vd chi tai lieu noi bo cho ADMIN xem), them 1 cot visibility trong
 * bang DOCUMENTS va loc o day.
 */
@WebServlet("/app/documents/download")
public class DocumentDownloadServlet extends HttpServlet {

    private final DocumentDAO documentDAO = new DocumentDAOImpl();

    /**
     * QUAN TRONG: DocumentServlet luu file that su tai
     *   <document.storage.root>/financial/<uuid>.ext
     * nhung lai ghi fileUrl vao DB dang "documents/financial/<uuid>.ext" (co san tien to "documents/").
     * Vi document.storage.root mac dinh la ".../shareholder-system/documents" (da co san "documents"
     * o cuoi), neu resolve fileUrl truc tiep tren STORAGE_ROOT se ra duong dan bi lap doi
     * ".../documents/documents/financial/..." -> khong ton tai -> 404. Nen STORAGE_ROOT o day phai
     * la THU MUC CHA cua document.storage.root, de khop dung voi fileUrl da luu trong DB.
     */
    private static final Path STORAGE_ROOT = resolveStorageRoot();

    private static Path resolveStorageRoot() {
        Path documentStorageRoot = Paths.get(
                System.getProperty("document.storage.root", "/var/shareholder-system/documents"));
        Path parent = documentStorageRoot.getParent();
        return (parent != null ? parent : documentStorageRoot).normalize().toAbsolutePath();
    }

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

        Optional<Document> docOpt;
        try {
            docOpt = documentDAO.findById(documentId);
        } catch (SQLException e) {
            getServletContext().log("Loi truy van tai lieu de tai xuong", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        if (docOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Khong tim thay tai lieu");
            return;
        }
        Document doc = docOpt.get();

        // fileUrl luu trong DB dang "documents/financial/<uuid>.<ext>" (sinh boi DocumentServlet luc
        // upload, KHONG bao gio lay tu input nguoi dung). Van chuan hoa + kiem tra lai o day de chan
        // path traversal ngay ca neu du lieu bi sua truc tiep trong DB.
        Path resolved = STORAGE_ROOT.resolve(doc.getFileUrl()).normalize();
        if (!resolved.startsWith(STORAGE_ROOT) || !Files.isRegularFile(resolved)) {
            getServletContext().log("Duong dan file bat thuong hoac khong ton tai: " + doc.getFileUrl());
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File khong ton tai");
            return;
        }

        String downloadName = safeFileName(doc.getTitle(), resolved.getFileName().toString());
        String contentType = Files.probeContentType(resolved) != null
                ? Files.probeContentType(resolved) : "application/octet-stream";
        resp.setContentType(contentType);
        resp.setContentLengthLong(Files.size(resolved));

        // ?mode=view -> xem truoc inline ngay trong trinh duyet (giong cach cafef.vn nhung PDF).
        // Chi cho phep inline voi PDF - Word/Excel de "inline" co the khien trinh duyet tai ve mo
        // bang ung dung ngoai (Word/Excel desktop) thay vi hien thi an toan trong tab, nen ep ve
        // "attachment" cho cac loai file khac de tranh hanh vi bat ngo/khong nhat quan giua cac trinh duyet.
        boolean wantsPreview = "view".equals(req.getParameter("mode"));
        boolean isPdf = "application/pdf".equals(contentType);
        String disposition = (wantsPreview && isPdf) ? "inline" : "attachment";
        resp.setHeader("Content-Disposition", disposition + "; filename*=UTF-8''" +
                URLEncoder.encode(downloadName, StandardCharsets.UTF_8).replace("+", "%20"));
        // Chan trinh duyet tu suy doan/thuc thi noi dung sai kieu (vd HTML gia mao duoi .pdf)
        resp.setHeader("X-Content-Type-Options", "nosniff");
        // Ngan cache/lo tam thoi qua trinh duyet dung chung (may nguoi dung khac nhau tren cung 1 may)
        resp.setHeader("Cache-Control", "private, no-store");

        try (InputStream in = Files.newInputStream(resolved);
             OutputStream out = resp.getOutputStream()) {
            in.transferTo(out);
        }
    }

    /** Giu duoi file goc, doi ten hien thi sang title cho de doc, loai ky tu gay loi header. */
    private String safeFileName(String title, String storedFileName) {
        String ext = storedFileName.contains(".")
                ? storedFileName.substring(storedFileName.lastIndexOf('.')) : "";
        String base = (title == null || title.isBlank()) ? "document" : title.trim();
        base = base.replaceAll("[\\\\/:*?\"<>|]", "_");
        return base + ext;
    }
}
