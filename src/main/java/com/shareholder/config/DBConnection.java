package com.shareholder.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Quan ly connection pool toi SQL Server bang HikariCP.
 * Cau hinh doc tu src/main/resources/db.properties.
 */
public class DBConnection {

    private static volatile HikariDataSource dataSource;

    private DBConnection() {}

    private static void init() {
        if (dataSource != null) return;
        synchronized (DBConnection.class) {
            if (dataSource != null) return;

            Properties props = new Properties();
            try (InputStream in = DBConnection.class.getClassLoader()
                    .getResourceAsStream("db.properties")) {
                if (in == null) {
                    throw new RuntimeException("Khong tim thay db.properties trong classpath");
                }
                props.load(in);
            } catch (Exception e) {
                throw new RuntimeException("Loi doc db.properties: " + e.getMessage(), e);
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));
            config.setDriverClassName(props.getProperty("db.driver",
                    "com.microsoft.sqlserver.jdbc.SQLServerDriver"));

            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.maxSize", "10")));
            config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minIdle", "2")));
            config.setConnectionTimeout(Long.parseLong(props.getProperty("db.pool.connectionTimeout", "30000")));
            config.setIdleTimeout(Long.parseLong(props.getProperty("db.pool.idleTimeout", "600000")));
            config.setPoolName("ShareholderSystemPool");

            dataSource = new HikariDataSource(config);
        }
    }

    public static Connection getConnection() throws SQLException {
        init();
        return dataSource.getConnection();
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
