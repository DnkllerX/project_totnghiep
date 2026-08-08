package com.shareholder.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.shareholder.util.PasswordUtil;

/**
 * Script seed du lieu, dung khi can khoi tao san tai khoan ADMIN/IT/SHAREHOLDER tren mot DB dev moi
 * (khong phai code chay trong app luc runtime - chi bam Run tay trong IDE khi can).
 *
 * An toan de chay lai nhieu lan: truoc khi insert se kiem tra username da ton tai chua,
 * neu roi thi bo qua (khong insert lai) thay vi de SQL Server nem loi UNIQUE constraint
 * (USERS.username / USERS.email la UNIQUE - xem snapshot01db.sql) roi bi nuot am tham qua
 * catch(Exception). Vi vay KHONG can xoa hay comment file nay sau khi seed lan dau - de danh
 * moi khi ban tao lai DB dev tu dau.
 *
 * KHONG tu mo ket noi rieng voi credential hardcode - dung lai DBConnection (doc tu
 * src/main/resources/db.properties, file nay da bi gitignore) de tranh lo user/password SQL Server
 * len source control.
 *
 * LUU Y QUAN TRONG ve tai khoan SHAREHOLDER: bang USERS chi luu thong tin dang nhap.
 * He thong con dung 2 bang lien quan: SHAREHOLDERS (ho so: ho ten, CCCD...) va SHARES
 * (so co phan dang nam giu) - xem FK trong snapshot01db.sql (SHAREHOLDERS.user_id ->
 * USERS.user_id, 1-1; SHARES.shareholder_id -> SHAREHOLDERS.shareholder_id, 1-1).
 * Neu chi insert vao USERS ma bo qua 2 bang nay:
 *   - Dashboard ADMIN se KHONG dem tai khoan do vao "Tong so co dong"
 *     (DashboardServlet dung shareholderDAO.findAllActive().size(), doc tu bang SHAREHOLDERS).
 *   - Cac trang Ky nhan / Bieu quyet / Chuyen nhuong cua chinh tai khoan do se loi vi khong
 *     tim thay shareholder_id tuong ung.
 * Vi vay seedShareholder() ben duoi luon insert ca 3 bang trong 1 transaction, dung y het
 * luong that trong ShareholderService.registerShareholderAccount() (chi khac cho status =
 * ACTIVE ngay tu dau thay vi LOCKED cho ADMIN duyet, vi day la du lieu test can dung ngay).
 */
public class SeedDatabase {

    private static final String INSERT_USER_SQL = """
            INSERT INTO USERS
            (
                username,
                email,
                password_hash,
                role,
                status
            )
            VALUES
            (?,?,?,?,?)
            """;

    private static final String INSERT_SHAREHOLDER_SQL = """
            INSERT INTO SHAREHOLDERS
            (
                user_id,
                full_name,
                citizen_id,
                phone,
                address,
                nationality
            )
            VALUES
            (?,?,?,?,?,?)
            """;

    private static final String INSERT_SHARE_SQL =
            "INSERT INTO SHARES (shareholder_id, quantity) VALUES (?, ?)";

    private static final String EXISTS_SQL = "SELECT 1 FROM USERS WHERE username = ?";

    public static void main(String[] args) {

        try (Connection conn = DBConnection.getConnection()) {
//passwd hash nen phải có cái này trước
            seedUser(conn, "admin", "admin@vinscape.lol", "Admin@123", "ADMIN");
            seedUser(conn, "itadmin", "no-reply@vinscape.lol", "IT@123", "IT");

            // 2 tai khoan SHAREHOLDER day du (USERS + SHAREHOLDERS + SHARES) de test duoc
            // toan bo tinh nang cua co dong, va de hien dung so lieu trong Dashboard ADMIN.
            seedShareholder(conn, "user1", "dungptts02476@gmail.com", "user1",
                    "Nguyen Van Test 1", "079099000001", "0900000001", 1000);
            //test user smtp ( mail thật của D)

            seedShareholder(conn, "user2", "user2@vinscape.local", "user2",
                    "Tran Thi Test 2", "079099000002", "0900000002", 500);

            System.out.println("Seed hoan tat.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean userExists(Connection conn, String username) throws Exception {
        try (PreparedStatement check = conn.prepareStatement(EXISTS_SQL)) {
            check.setString(1, username);
            try (ResultSet rs = check.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Seed tai khoan KHONG co ho so co dong (ADMIN / IT). */
    private static void seedUser(Connection conn, String username, String email,
                                 String plainPassword, String role) throws Exception {
        if (userExists(conn, username)) {
            System.out.println("Bo qua \"" + username + "\" - da ton tai.");
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement(INSERT_USER_SQL)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, PasswordUtil.hash(plainPassword));
            ps.setString(4, role);
            ps.setString(5, "ACTIVE"); //de thanh active
            ps.executeUpdate();
            System.out.println("Da tao \"" + username + "\".");
        }
    }

    /**
     * Seed tai khoan SHAREHOLDER day du: USERS (status=ACTIVE) + SHAREHOLDERS (ho so) +
     * SHARES (so co phan khoi tao), ca 3 trong 1 transaction.
     */
    private static void seedShareholder(Connection conn, String username, String email,
                                        String plainPassword, String fullName, String citizenId,
                                        String phone, int initialShareQuantity) throws Exception {
        if (userExists(conn, username)) {
            System.out.println("Bo qua \"" + username + "\" - da ton tai.");
            return;
        }

        boolean originalAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            int userId;
            try (PreparedStatement ps = conn.prepareStatement(INSERT_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, username);
                ps.setString(2, email);
                ps.setString(3, PasswordUtil.hash(plainPassword));
                ps.setString(4, "SHAREHOLDER");
                ps.setString(5, "ACTIVE");
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    userId = keys.getInt(1);
                }
            }

            int shareholderId;
            try (PreparedStatement ps = conn.prepareStatement(INSERT_SHAREHOLDER_SQL, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setString(2, fullName);
                ps.setString(3, citizenId);
                ps.setString(4, phone);
                ps.setString(5, null); // address - khong bat buoc cho du lieu test
                ps.setString(6, "Vietnam");
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    shareholderId = keys.getInt(1);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(INSERT_SHARE_SQL)) {
                ps.setInt(1, shareholderId);
                ps.setInt(2, initialShareQuantity);
                ps.executeUpdate();
            }

            conn.commit();
            System.out.println("Da tao co dong \"" + username + "\" (shareholder_id=" + shareholderId
                    + ", " + initialShareQuantity + " co phan).");
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }
}