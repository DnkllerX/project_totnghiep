package com.shareholder.service;

import com.shareholder.dao.AuditLogDAO;
import com.shareholder.dao.DocumentDAO;
import com.shareholder.dao.impl.AuditLogDAOImpl;
import com.shareholder.dao.impl.DocumentDAOImpl;
import com.shareholder.model.AuditLog;
import com.shareholder.model.Document;
import com.shareholder.model.enums.AuditAction;
import com.shareholder.model.enums.EntityType;

import java.sql.SQLException;

public class DocumentService {

    private final DocumentDAO documentDAO = new DocumentDAOImpl();
    private final AuditLogDAO auditLogDAO = new AuditLogDAOImpl();

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
        int documentId = documentDAO.insert(doc);

        AuditLog log = new AuditLog(AuditAction.UPLOAD_DOCUMENT, EntityType.DOCUMENT, documentId,
                actorUserId, userAgent);
        auditLogDAO.insert(log);
        return documentId;
    }

    public boolean deleteDocument(int documentId, int actorUserId, String userAgent) throws SQLException {
        boolean deleted = documentDAO.delete(documentId);
        if (deleted) {
            AuditLog log = new AuditLog(AuditAction.DELETE_DOCUMENT, EntityType.DOCUMENT, documentId,
                    actorUserId, userAgent);
            auditLogDAO.insert(log);
        }
        return deleted;
    }
}
