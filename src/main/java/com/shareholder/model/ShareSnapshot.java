package com.shareholder.model;

import java.time.LocalDateTime;

public class ShareSnapshot {
    private int snapshotId;
    private LocalDateTime snapshotDate;
    private String reason;
    private LocalDateTime createdAt;

    public ShareSnapshot() {}

    public ShareSnapshot(int snapshotId, LocalDateTime snapshotDate, String reason, LocalDateTime createdAt) {
        this.snapshotId = snapshotId;
        this.snapshotDate = snapshotDate;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public int getSnapshotId() { return snapshotId; }
    public void setSnapshotId(int snapshotId) { this.snapshotId = snapshotId; }

    public LocalDateTime getSnapshotDate() { return snapshotDate; }
    public void setSnapshotDate(LocalDateTime snapshotDate) { this.snapshotDate = snapshotDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
