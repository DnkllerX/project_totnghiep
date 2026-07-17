package com.shareholder.model;

import com.shareholder.model.enums.AuditAction;
import com.shareholder.model.enums.EntityType;
import java.time.LocalDateTime;

public class AuditLog {
    private int logId;
    private Integer userId; // nullable - hanh dong he thong tu dong (scheduler)
    private AuditAction action;
    private EntityType entityType;
    private Integer entityId; // polymorphic, khong FK
    private String userAgent;
    private LocalDateTime createdAt;

    public AuditLog() {}

    public AuditLog(AuditAction action, EntityType entityType, Integer entityId,
                     Integer userId, String userAgent) {
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.userId = userId;
        this.userAgent = userAgent;
    }

    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public AuditAction getAction() { return action; }
    public void setAction(AuditAction action) { this.action = action; }

    public EntityType getEntityType() { return entityType; }
    public void setEntityType(EntityType entityType) { this.entityType = entityType; }

    public Integer getEntityId() { return entityId; }
    public void setEntityId(Integer entityId) { this.entityId = entityId; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
