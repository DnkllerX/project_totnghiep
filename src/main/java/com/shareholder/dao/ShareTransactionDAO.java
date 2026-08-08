package com.shareholder.dao;

import com.shareholder.model.ShareTransaction;
import com.shareholder.model.enums.TxStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ShareTransactionDAO {
    Optional<ShareTransaction> findById(int txId) throws SQLException;
    List<ShareTransaction> findByShareholderId(int shareholderId) throws SQLException;
    List<ShareTransaction> findByStatus(TxStatus status) throws SQLException;
    List<ShareTransaction> findAll() throws SQLException;

    /** Lich su chuyen nhuong da xu ly xong (COMPLETED/REJECTED), khong bao gom PENDING dang cho duyet. */
    List<ShareTransaction> findTransferHistory() throws SQLException;

    /** Insert trong 1 transaction co san (dung khi ket hop voi update SHARES). */
    int insert(Connection conn, ShareTransaction tx) throws SQLException;

    boolean updateStatus(int txId, TxStatus status) throws SQLException;
}
