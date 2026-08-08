package com.shareholder.model;

import java.time.LocalDateTime;

public class ShareAdjustmentLog {
    private int id;
    private int shareholderId;
    private int oldValue;
    private int newValue;
    private String reason;
    private int adjustedBy; // FK USERS
    private LocalDateTime adjustedAt;

    public ShareAdjustmentLog() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getShareholderId() { return shareholderId; }
    public void setShareholderId(int shareholderId) { this.shareholderId = shareholderId; }

    public int getOldValue() { return oldValue; }
    public void setOldValue(int oldValue) { this.oldValue = oldValue; }

    public int getNewValue() { return newValue; }
    public void setNewValue(int newValue) { this.newValue = newValue; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public int getAdjustedBy() { return adjustedBy; }
    public void setAdjustedBy(int adjustedBy) { this.adjustedBy = adjustedBy; }

    public LocalDateTime getAdjustedAt() { return adjustedAt; }
    public void setAdjustedAt(LocalDateTime adjustedAt) { this.adjustedAt = adjustedAt; }

    /** Dung cho hien thi UI: yyyy-MM-dd HH:mm:ss, bo phan nano-second thua cua LocalDateTime.toString(). */
    public String getFormattedAdjustedAt() {
        if (adjustedAt == null) return "";
        return adjustedAt.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
