package com.shareholder.model;

import com.shareholder.model.enums.ResolutionStatus;
import java.time.LocalDateTime;

public class Resolution {
    private int resolutionId;
    private String title;
    private String description;
    private ResolutionStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;

    public Resolution() {}

    public int getResolutionId() { return resolutionId; }
    public void setResolutionId(int resolutionId) { this.resolutionId = resolutionId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ResolutionStatus getStatus() { return status; }
    public void setStatus(ResolutionStatus status) { this.status = status; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
