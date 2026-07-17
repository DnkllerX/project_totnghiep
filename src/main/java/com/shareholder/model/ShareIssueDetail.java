package com.shareholder.model;

import com.shareholder.model.enums.IssueDetailStatus;
import java.time.LocalDateTime;

public class ShareIssueDetail {
    private int id;
    private int issueId;
    private int snapshotId;
    private int shareholderId;
    private int eligibleQuantity;
    private int receivedQuantity; // NOT NULL DEFAULT 0
    private String signatureUrl;  // nullable cho den khi ky
    private LocalDateTime signedAt;
    private IssueDetailStatus status;

    public ShareIssueDetail() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIssueId() { return issueId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }

    public int getSnapshotId() { return snapshotId; }
    public void setSnapshotId(int snapshotId) { this.snapshotId = snapshotId; }

    public int getShareholderId() { return shareholderId; }
    public void setShareholderId(int shareholderId) { this.shareholderId = shareholderId; }

    public int getEligibleQuantity() { return eligibleQuantity; }
    public void setEligibleQuantity(int eligibleQuantity) { this.eligibleQuantity = eligibleQuantity; }

    public int getReceivedQuantity() { return receivedQuantity; }
    public void setReceivedQuantity(int receivedQuantity) { this.receivedQuantity = receivedQuantity; }

    public String getSignatureUrl() { return signatureUrl; }
    public void setSignatureUrl(String signatureUrl) { this.signatureUrl = signatureUrl; }

    public LocalDateTime getSignedAt() { return signedAt; }
    public void setSignedAt(LocalDateTime signedAt) { this.signedAt = signedAt; }

    public IssueDetailStatus getStatus() { return status; }
    public void setStatus(IssueDetailStatus status) { this.status = status; }

    /** Khop CHECK constraint: 0 <= received_quantity <= eligible_quantity */
    public boolean isReceivedQuantityValid() {
        return receivedQuantity >= 0 && receivedQuantity <= eligibleQuantity;
    }
}
