package com.shareholder.dao;

import com.shareholder.model.User;
import com.shareholder.model.enums.UserRole;
import com.shareholder.model.enums.UserSortOption;
import com.shareholder.model.enums.UserStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDAO {
    Optional<User> findById(int userId) throws SQLException;
    Optional<User> findByUsername(String username) throws SQLException;
    Optional<User> findByEmail(String email) throws SQLException;
    List<User> findAll() throws SQLException;
    List<User> findByRole(UserRole role) throws SQLException;

    /**
     * Danh sach tai khoan role=SHAREHOLDER va status=LOCKED - gom ca tai khoan moi dang ky (cho
     * ADMIN duyet lan dau) lan tai khoan da bi khoa sau nay, vi he thong dung chung 1 status cho ca 2.
     */
    List<User> findLockedShareholders() throws SQLException;

    /**
     * Tim kiem/loc/sort linh hoat cho trang quan ly tai khoan. Tat ca dieu kien la optional (null/rong
     * = bo qua). Cau truc SQL (WHERE/ORDER BY) duoc dung san trong code, KHONG ghep tu input nguoi dung;
     * chi gia tri thuc te di qua PreparedStatement "?".
     */
    List<User> search(String usernameContains, String emailContains, UserRole role, UserStatus status,
                       UserSortOption sort) throws SQLException;

    int insert(User user) throws SQLException;

    /** Dung khi can gop chung 1 transaction voi cac insert khac (vd: tao Shareholder). */
    int insert(Connection conn, User user) throws SQLException;
    boolean update(User user) throws SQLException;
    boolean updateStatus(int userId, UserStatus status) throws SQLException;
    boolean updatePasswordHash(int userId, String passwordHash) throws SQLException;

    /**
     * Chuyen status, dieu kien "status = fromStatus" de tranh duyet 2 lan / race condition.
     * Dung trong 1 transaction co san (vd: duyet tai khoan + cap co phan khoi tao).
     */
    boolean updateStatusIfCurrentStatus(Connection conn, int userId, UserStatus fromStatus, UserStatus toStatus)
            throws SQLException;

    boolean existsByUsername(String username) throws SQLException;
    boolean existsByEmail(String email) throws SQLException;
    boolean delete(int userId) throws SQLException;
}
