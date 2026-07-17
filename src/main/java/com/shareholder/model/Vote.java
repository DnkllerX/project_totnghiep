package com.shareholder.model;

import com.shareholder.model.enums.VoteValue;
import java.time.LocalDateTime;

public class Vote {
    private int voteId;
    private int resolutionId;
    private int shareholderId;
    private int snapshotId;
    private VoteValue voteValue;
    private LocalDateTime votedAt;

    public Vote() {}

    public int getVoteId() { return voteId; }
    public void setVoteId(int voteId) { this.voteId = voteId; }

    public int getResolutionId() { return resolutionId; }
    public void setResolutionId(int resolutionId) { this.resolutionId = resolutionId; }

    public int getShareholderId() { return shareholderId; }
    public void setShareholderId(int shareholderId) { this.shareholderId = shareholderId; }

    public int getSnapshotId() { return snapshotId; }
    public void setSnapshotId(int snapshotId) { this.snapshotId = snapshotId; }

    public VoteValue getVoteValue() { return voteValue; }
    public void setVoteValue(VoteValue voteValue) { this.voteValue = voteValue; }

    public LocalDateTime getVotedAt() { return votedAt; }
    public void setVotedAt(LocalDateTime votedAt) { this.votedAt = votedAt; }
}
