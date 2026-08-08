package com.shareholder.model;

public class ShareSnapshotDetail {
    private int id;
    private int snapshotId;
    private int shareholderId;
    private int shareQuantity;

    public ShareSnapshotDetail() {}

    public ShareSnapshotDetail(int id, int snapshotId, int shareholderId, int shareQuantity) {
        this.id = id;
        this.snapshotId = snapshotId;
        this.shareholderId = shareholderId;
        this.shareQuantity = shareQuantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSnapshotId() { return snapshotId; }
    public void setSnapshotId(int snapshotId) { this.snapshotId = snapshotId; }

    public int getShareholderId() { return shareholderId; }
    public void setShareholderId(int shareholderId) { this.shareholderId = shareholderId; }

    public int getShareQuantity() { return shareQuantity; }
    public void setShareQuantity(int shareQuantity) { this.shareQuantity = shareQuantity; }
}
