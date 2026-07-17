package com.shareholder.dao;

import com.shareholder.model.Share;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ShareDAO {
    Optional<Share> findByShareholderId(int shareholderId) throws SQLException;
    List<Share> findAll() throws SQLException;
    int insert(Share share) throws SQLException;
    int insert(Connection conn, Share share) throws SQLException;

    /** Cong them (hoac tru, neu delta am) vao so du hien tai - dung cho ADJUSTMENT/ISSUE/TRANSFER. */
    boolean addQuantity(Connection conn, int shareholderId, int delta) throws SQLException;

    /** Ghi de truc tiep so du - dung khi ADMIN dieu chinh thu cong. */
    boolean setQuantity(Connection conn, int shareholderId, int newQuantity) throws SQLException;
}
