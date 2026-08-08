package com.shareholder.model;

import com.shareholder.model.enums.EventStatus;
import com.shareholder.model.enums.EventType;
import java.time.LocalDateTime;

public class ScheduledEvent {
    private int eventId;
    private String eventKey;
    private EventType eventType;
    private Integer issueId;      // NOT NULL khi PROCESS_SHARE_ISSUE
    private Integer resolutionId; // NOT NULL khi CLOSE_RESOLUTION
    private LocalDateTime eventDate;
    private EventStatus status;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;

    public ScheduledEvent() {}

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public String getEventKey() { return eventKey; }
    public void setEventKey(String eventKey) { this.eventKey = eventKey; }

    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }

    public Integer getIssueId() { return issueId; }
    public void setIssueId(Integer issueId) { this.issueId = issueId; }

    public Integer getResolutionId() { return resolutionId; }
    public void setResolutionId(Integer resolutionId) { this.resolutionId = resolutionId; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /** Khop CHECK constraint CHK_Sched_TargetByType */
    public boolean isTargetValid() {
        if (eventType == EventType.PROCESS_SHARE_ISSUE) return issueId != null && resolutionId == null;
        if (eventType == EventType.CLOSE_RESOLUTION) return resolutionId != null && issueId == null;
        return false;
    }
}
