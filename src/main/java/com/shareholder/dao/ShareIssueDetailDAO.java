package com.shareholder.dao;

import com.shareholder.model.ShareIssueDetail;
import com.shareholder.model.enums.IssueDetailStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ShareIssueDetailDAO {
    Optional<ShareIssueDetail> findById(int id) throws SQLException;
    Optional<ShareIssueDetail> findByIssueAndShareholder(int issueId, int shareholderId) throws SQLException;
    List<ShareIssueDetail> findByIssueId(int issueId) throws SQLException;
    List<ShareIssueDetail> findByShareholderId(int shareholderId) throws SQLException;
    List<ShareIssueDetail> findByIssueIdAndStatus(int issueId, IssueDetailStatus status) throws SQLException;

    /** Sinh hang loat ban ghi PENDING tu SHARE_SNAPSHOT_DETAILS cua 1 snapshot, ap dung ty le phat hanh. */
    int generateFromSnapshot(Connection conn, int issueId, int snapshotId, java.math.BigDecimal ratio)
            throws SQLException;

    /** Luu chu ky tay (duong dan file anh da decode tu base64 canvas) va chuyen status -> ACCEPTED. */
    boolean signAccept(int id, int shareholderId, String signatureUrl) throws SQLException;

    /** Scheduler dung: cong received_quantity vao SHARES va chuyen status -> COMPLETED (trong 1 transaction). */
    boolean markCompleted(Connection conn, int id) throws SQLException;

    /** Scheduler dung: cac ban ghi con PENDING khi het end_date -> EXPIRED. */
    int expireStalePending(Connection conn, int issueId) throws SQLException;
}
