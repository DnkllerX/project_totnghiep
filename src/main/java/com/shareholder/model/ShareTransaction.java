package com.shareholder.model;

import com.shareholder.model.enums.TxStatus;
import com.shareholder.model.enums.TxType;
import java.time.LocalDateTime;

public class ShareTransaction {
    private int txId;
    private Integer fromShareholderId; // nullable: ISSUE/ADJUSTMENT khong co nguon
    private Integer toShareholderId;
    private int quantity;
    private TxType txType;
    private TxStatus status;
    private LocalDateTime createdAt;

    public ShareTransaction() {}

    public ShareTransaction(int txId, Integer fromShareholderId, Integer toShareholderId,
                             int quantity, TxType txType, TxStatus status, LocalDateTime createdAt) {
        this.txId = txId;
        this.fromShareholderId = fromShareholderId;
        this.toShareholderId = toShareholderId;
        this.quantity = quantity;
        this.txType = txType;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getTxId() { return txId; }
    public void setTxId(int txId) { this.txId = txId; }

    public Integer getFromShareholderId() { return fromShareholderId; }
    public void setFromShareholderId(Integer fromShareholderId) { this.fromShareholderId = fromShareholderId; }

    public Integer getToShareholderId() { return toShareholderId; }
    public void setToShareholderId(Integer toShareholderId) { this.toShareholderId = toShareholderId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public TxType getTxType() { return txType; }
    public void setTxType(TxType txType) { this.txType = txType; }

    public TxStatus getStatus() { return status; }
    public void setStatus(TxStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
