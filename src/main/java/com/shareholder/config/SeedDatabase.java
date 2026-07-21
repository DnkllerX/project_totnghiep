package com.shareholder.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import com.shareholder.util.PasswordUtil;

public class SeedDatabase {

    // ======= SQL SERVER =======
    private static final String URL =
            "jdbc:sqlserver://localhost:1433;"
          + "databaseName=SNAPSHOT01DB;"
          + "encrypt=true;"
          + "trustServerCertificate=true";

    private static final String USER = "sa";
    private static final String PASSWORD = "123456789";

    public static void main(String[] args) {

        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            String sql = """
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

            PreparedStatement ps = conn.prepareStatement(sql);

            // ADMIN
            ps.setString(1, "admin");
            ps.setString(2, "admin@snapshot01.local");
            ps.setString(3, PasswordUtil.hash("Admin@123"));
            ps.setString(4, "ADMIN");
            ps.setString(5, "ACTIVE");
            ps.executeUpdate();

            // IT
            ps.setString(1, "itadmin");
            ps.setString(2, "it@snapshot01.local");
            ps.setString(3, PasswordUtil.hash("IT@123"));
            ps.setString(4, "IT");
            ps.setString(5, "ACTIVE");
            ps.executeUpdate();

            conn.close();

            System.out.println("Seed thành công!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

