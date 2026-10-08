package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * DatabaseConnection.java
 * Configurable connection manager for Local Oracle SQL Database.
 * Centralized configuration settings for host, port, service name, username, and password.
 */
public class DatabaseConnection {

    // =========================================================================
    // CONFIGURABLE ORACLE CONNECTION PARAMETERS
    // Easily modify these values according to your local Oracle installation.
    // =========================================================================
    private static final Properties CONFIG = loadConfiguration();
    private static String DB_HOST = setting("db.host", "CAMPUSDROP_DB_HOST", "localhost");
    private static String DB_PORT = setting("db.port", "CAMPUSDROP_DB_PORT", "1522");
    private static String DB_SERVICE_NAME = setting("db.service", "CAMPUSDROP_DB_SERVICE", "XEPDB1");
    private static String DB_USERNAME = setting("db.username", "CAMPUSDROP_DB_USERNAME", "system");
    private static String DB_PASSWORD = secretSetting("db.password", "CAMPUSDROP_DB_PASSWORD");
    private static String DB_URL = setting("db.url", "CAMPUSDROP_DB_URL", null);

    private static Properties loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(Paths.get("campusdrop.properties"))) {
            properties.load(input);
        } catch (IOException e) {
            // A local properties file is optional; environment variables can be used instead.
        }
        return properties;
    }

    private static String setting(String property, String environmentVariable, String defaultValue) {
        String value = System.getenv(environmentVariable);
        if (value == null || value.trim().isEmpty()) {
            value = CONFIG.getProperty(property);
        }
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    private static String secretSetting(String property, String environmentVariable) {
        String value = System.getenv(environmentVariable);
        return value != null ? value : CONFIG.getProperty(property);
    }

    // Oracle Thin JDBC Driver URL format
    private static String getJdbcUrl() {
        return DB_URL != null ? DB_URL : "jdbc:oracle:thin:@//" + DB_HOST + ":" + DB_PORT + "/" + DB_SERVICE_NAME;
    }

    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Warning: Oracle JDBC Driver (ojdbc8.jar) not found in classpath.");
        }
    }

    /**
     * Establishes and returns an active Connection to the local Oracle Database.
     */
    public static Connection getConnection() throws SQLException {
        if (DB_PASSWORD == null || DB_PASSWORD.trim().isEmpty()) {
            throw new SQLException("Oracle password is not configured. Set db.password in campusdrop.properties or CAMPUSDROP_DB_PASSWORD.");
        }
        return DriverManager.getConnection(getJdbcUrl(), DB_USERNAME, DB_PASSWORD);
    }

    /**
     * Diagnostic helper method to verify connection status.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("==================================================");
                System.out.println("   ORACLE DATABASE CONNECTED SUCCESSFULLY!");
                System.out.println("   URL: " + getJdbcUrl());
                System.out.println("==================================================");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("==================================================");
            System.out.println("   ORACLE DATABASE CONNECTION WARNING");
            System.out.println("   Unable to connect to Oracle DB: " + e.getMessage());
            System.out.println("   Note: Operating in Java In-Memory Mode.");
            System.out.println("==================================================");
        }
        return false;
    }

    public static void setCredentials(String host, String port, String serviceName, String username, String password) {
        DB_HOST = host;
        DB_PORT = port;
        DB_SERVICE_NAME = serviceName;
        DB_USERNAME = username;
        DB_PASSWORD = password;
        DB_URL = null;
    }
}
