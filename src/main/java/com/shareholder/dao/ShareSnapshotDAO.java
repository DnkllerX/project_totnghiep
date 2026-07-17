package com.shareholder.dao;

import com.shareholder.model.ShareSnapshot;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ShareSnapshotDAO {
    Optional<ShareSnapshot> findById(int snapshotId) throws SQLException;
    List<ShareSnapshot> findAll() throws SQLException;
    int insert(Connection conn, ShareSnapshot snapshot) throws SQLException;

    /**
     * RESOLUTIONS khong co cot snapshot_id truc tiep (chi VOTES moi co FK toi SHARE_SNAPSHOTS).
     * Ham nay tim lai snapshot da tao rieng cho 1 nghi quyet cu the, dua vao tien to co dinh
     * cua truong "reason" duoc ResolutionService gan luc tao ("Snapshot cho nghi quyet #<id>: ...").
     */
    Optional<ShareSnapshot> findLatestByResolutionId(int resolutionId) throws SQLException;
}
