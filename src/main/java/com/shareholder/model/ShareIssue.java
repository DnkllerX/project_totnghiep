package com.shareholder.model;

import com.shareholder.model.enums.IssueType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ShareIssue {
    private int issueId;
    private String title;
    private IssueType issueType;
    private LocalDate issueDate;
    private LocalDateTime snapshotDate;
    private int shareQuantity;
    private BigDecimal issueRatio;   // nullable
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private LocalDateTime createdAt;

    public ShareIssue() {}

    public int getIssueId() { return issueId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public IssueType getIssueType() { return issueType; }
    public void setIssueType(IssueType issueType) { this.issueType = issueType; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDateTime getSnapshotDate() { return snapshotDate; }
    public void setSnapshotDate(LocalDateTime snapshotDate) { this.snapshotDate = snapshotDate; }

    public int getShareQuantity() { return shareQuantity; }
    public void setShareQuantity(int shareQuantity) { this.shareQuantity = shareQuantity; }

    public BigDecimal getIssueRatio() { return issueRatio; }
    public void setIssueRatio(BigDecimal issueRatio) { this.issueRatio = issueRatio; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /** Khop CHK_Issues_DateOrder: issue_date <= snapshot_date <= start_date <= end_date */
    public boolean isDateOrderValid() {
        if (issueDate == null || snapshotDate == null || startDate == null || endDate == null) return false;
        java.time.LocalDateTime issueDateTime = issueDate.atStartOfDay();
        return !issueDateTime.isAfter(snapshotDate)
                && !snapshotDate.isAfter(startDate)
                && !startDate.isAfter(endDate);
    }
}
