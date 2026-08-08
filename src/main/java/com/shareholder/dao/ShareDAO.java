package com.shareholder.dao;

import com.shareholder.model.Share;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ShareDAO {
    Optional<Share> findByShareholderId(int shareholderId) throws SQLException;

    /**
     * Giong findByShareholderId nhung (1) dung CHUNG connection/transaction dang chay ben ngoai
     * (khong tu mo connection rieng) va (2) khoa dong bang WITH (UPDLOCK, ROWLOCK) - chan moi
     * giao dich khac (adjust/transfer/issue) doc-ghi dong nay cho den khi transaction hien tai
     * commit/rollback. Dung o buoc "doc so du cu truoc khi ghi so du moi" de tranh race condition
     * (2 admin/thao tac cung sua 1 dong SHARES gan nhu dong thoi).
     */
    Optional<Share> findByShareholderIdForUpdate(Connection conn, int shareholderId) throws SQLException;

    List<Share> findAll() throws SQLException;
    int insert(Share share) throws SQLException;
    int insert(Connection conn, Share share) throws SQLException;

    /** Cong them (hoac tru, neu delta am) vao so du hien tai - dung cho ADJUSTMENT/ISSUE/TRANSFER. */
    boolean addQuantity(Connection conn, int shareholderId, int delta) throws SQLException;

    /** Ghi de truc tiep so du - dung khi ADMIN dieu chinh thu cong. */
    boolean setQuantity(Connection conn, int shareholderId, int newQuantity) throws SQLException;

    /**
     * Uoc tinh tong so co phan se duoc phat hanh neu ap dung ty le "ratio" cho toan bo
     * SHARES hien tai (quantity > 0), lam tron xuong tung shareholder roi cong lai
     * (khop cong thuc that su dung trong ShareIssueDetailDAOImpl.generateFromSnapshot:
     * FLOOR(share_quantity * ratio) cho tung nguoi). Chi la con so DU KIEN de admin xem truoc -
     * so lieu chinh xac se duoc tinh lai tai thoi diem snapshot (co the lech neu co giao dich
     * mua/ban giua luc tao dot va luc chot snapshot).
     */
    long estimateIssueQuantity(java.math.BigDecimal ratio) throws SQLException;
}
