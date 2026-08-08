package com.shareholder.service;

import com.shareholder.dao.AuditLogDAO;
import com.shareholder.dao.DocumentDAO;
import com.shareholder.dao.impl.AuditLogDAOImpl;
import com.shareholder.dao.impl.DocumentDAOImpl;
import com.shareholder.model.AuditLog;
import com.shareholder.model.Document;
import com.shareholder.model.enums.AuditAction;
import com.shareholder.model.enums.EntityType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DocumentService {

    private static final Logger LOGGER = Logger.getLogger(DocumentService.class.getName());

    private final DocumentDAO documentDAO = new DocumentDAOImpl();
    private final AuditLogDAO auditLogDAO = new AuditLogDAOImpl();

    /**
     * PHAI khop voi DOCUMENT_STORAGE_ROOT trong DocumentServlet va STORAGE_ROOT trong
     * DocumentDownloadServlet (do la thu muc CHA cua document.storage.root, vi fileUrl luu trong DB
     * da co san tien to "documents/" - xem chu thich trong DocumentDownloadServlet de biet ly do).
     */
    private static final Path STORAGE_ROOT = resolveStorageRoot();

    private static Path resolveStorageRoot() {
        Path documentStorageRoot = Paths.get(
                System.getProperty("document.storage.root", "/var/shareholder-system/documents"));
        Path parent = documentStorageRoot.getParent();
        return (parent != null ? parent : documentStorageRoot).normalize().toAbsolutePath();
    }

    public static class ValidationException extends Exception {
        public ValidationException(String message) { super(message); }
    }

    /**
     * fileUrl phai la duong dan tuong doi da duoc controller sinh ra (vd UUID-based), KHONG dung
     * ten file goc nguoi dung upload de tranh path traversal / trung ten.
     */
    public int uploadDocument(String title, String description, String fileUrl,
                               int actorUserId, String userAgent) throws SQLException, ValidationException {
        if (title == null || title.isBlank()) throw new ValidationException("Tieu de khong duoc de trong");
        if (fileUrl == null || fileUrl.isBlank()) throw new ValidationException("Duong dan file khong hop le");
        if (fileUrl.contains("..") || fileUrl.contains("\\")) {
            throw new ValidationException("Duong dan file chua ky tu khong hop le");
        }

        Document doc = new Document();
        doc.setTitle(title);
        doc.setDescription(description);
        doc.setFileUrl(fileUrl);
        // actorUserId luon la ADMIN o day (DocumentServlet da chan "!ADMIN".equals(role) truoc khi
        // goi vao ham nay) nen gan thang lam nguoi tao, khong can kiem tra lai role.
        doc.setCreatedBy(actorUserId);
        int documentId = documentDAO.insert(doc);

        AuditLog log = new AuditLog(AuditAction.UPLOAD_DOCUMENT, EntityType.DOCUMENT, documentId,
                actorUserId, userAgent);
        auditLogDAO.insert(log);
        return documentId;
    }

    public boolean deleteDocument(int documentId, int actorUserId, String userAgent) throws SQLException {
        Optional<Document> docOpt = documentDAO.findById(documentId);
        boolean deleted = documentDAO.delete(documentId);
        if (deleted) {
            AuditLog log = new AuditLog(AuditAction.DELETE_DOCUMENT, EntityType.DOCUMENT, documentId,
                    actorUserId, userAgent);
            auditLogDAO.insert(log);

            // Xoa luon file vat ly tren dia - khong de rac lai neu khong se chiem dung dung luong
            // vo thoi han. Loi xoa file (vd file da bi xoa tay truoc do) chi log lai, KHONG rollback
            // viec xoa ban ghi DB (ban ghi DB da xoa la nguon "su that" chinh, file rac khong quan
            // trong bang viec du lieu hien thi dung).
            docOpt.ifPresent(doc -> {
                try {
                    Path resolved = STORAGE_ROOT.resolve(doc.getFileUrl()).normalize();
                    if (resolved.startsWith(STORAGE_ROOT)) {
                        Files.deleteIfExists(resolved);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Khong xoa duoc file vat ly cho documentId=" + documentId, e);
                }
            });
        }
        return deleted;
    }
}
