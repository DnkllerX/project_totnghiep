package com.shareholder.model;

import java.time.LocalDateTime;

public class Share {
    private int shareId;
    private int shareholderId;
    private int quantity;
    private LocalDateTime updatedAt;

    public Share() {}

    public Share(int shareId, int shareholderId, int quantity, LocalDateTime updatedAt) {
        this.shareId = shareId;
        this.shareholderId = shareholderId;
        this.quantity = quantity;
        this.updatedAt = updatedAt;
    }

    public int getShareId() { return shareId; }
    public void setShareId(int shareId) { this.shareId = shareId; }

    public int getShareholderId() { return shareholderId; }
    public void setShareholderId(int shareholderId) { this.shareholderId = shareholderId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
