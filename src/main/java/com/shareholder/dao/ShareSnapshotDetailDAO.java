package com.shareholder.dao;

import com.shareholder.model.ShareSnapshotDetail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ShareSnapshotDetailDAO {
    List<ShareSnapshotDetail> findBySnapshotId(int snapshotId) throws SQLException;
    List<ShareSnapshotDetail> findByShareholderId(int shareholderId) throws SQLException;

    /** Dung de kiem tra co dong co nam trong danh sach chot quyen (co co phan) tai 1 snapshot cu the khong. */
    Optional<ShareSnapshotDetail> findBySnapshotAndShareholder(int snapshotId, int shareholderId) throws SQLException;

    /** Chup toan bo so du hien tai cua tat ca co dong vao snapshot (dung SELECT ... INTO trong 1 transaction). */
    int snapshotAllCurrentShares(Connection conn, int snapshotId) throws SQLException;
}
