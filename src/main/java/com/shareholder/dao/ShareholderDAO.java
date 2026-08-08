package com.shareholder.dao;

import com.shareholder.model.Shareholder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ShareholderDAO {
    Optional<Shareholder> findById(int shareholderId) throws SQLException;
    Optional<Shareholder> findByUserId(int userId) throws SQLException;
    Optional<Shareholder> findByCitizenId(String citizenId) throws SQLException;
    List<Shareholder> findAll() throws SQLException;

    /** Chi tra ve co dong co tai khoan USERS.status = ACTIVE (da duoc ADMIN duyet). */
    List<Shareholder> findAllActive() throws SQLException;

    /**
     * Nhu findAllActive() nhung loc them theo cac tieu chi tim kiem (tham so null/rong = bo qua
     * dieu kien do). id la khop chinh xac, cac truong con lai la khop tuong doi (LIKE %...%).
     */
    List<Shareholder> searchActive(Integer id, String fullName, String citizenId, String phone)
            throws SQLException;
    int insert(Shareholder shareholder) throws SQLException;
    int insert(Connection conn, Shareholder shareholder) throws SQLException;
    boolean update(Shareholder shareholder) throws SQLException;
    boolean existsByCitizenId(String citizenId) throws SQLException;
    boolean delete(int shareholderId) throws SQLException;
}
