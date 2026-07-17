package com.shareholder.dao;

import com.shareholder.model.ShareSnapshotDetail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ShareSnapshotDetailDAO {
    List<ShareSnapshotDetail> findBySnapshotId(int snapshotId) throws SQLException;
    List<ShareSnapshotDetail> findByShareholderId(int shareholderId) throws SQLException;

    /** Chup toan bo so du hien tai cua tat ca co dong vao snapshot (dung SELECT ... INTO trong 1 transaction). */
    int snapshotAllCurrentShares(Connection conn, int snapshotId) throws SQLException;
}
