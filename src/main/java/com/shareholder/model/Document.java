package com.shareholder.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Document {
    private int documentId;
    private String title;
    private String description;
    private String fileUrl;
    private LocalDateTime uploadedAt;
    /** user_id cua ADMIN da upload tai lieu nay. Co the NULL voi du lieu cu tao truoc khi co cot nay. */
    private Integer createdBy;

    public Document() {}

    public int getDocumentId() { return documentId; }
    public void setDocumentId(int documentId) { this.documentId = documentId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    /** Chuoi hien thi ngan gon cho giao dien danh sach tai lieu. */
    public String getUploadedAtDisplay() {
        return uploadedAt == null ? "--" : uploadedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
