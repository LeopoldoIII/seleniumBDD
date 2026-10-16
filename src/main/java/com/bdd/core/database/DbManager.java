package com.bdd.core.database;

import com.bdd.config.ConfigReader;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic JDBC Database Manager for SQL validations and assertions during tests.
 * Supports PostgreSQL, MySQL, H2, SQLite, or any JDBC compliant database.
 */
public class DbManager {

    private static Connection connection;

    private static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = ConfigReader.getDatabaseUrl();
            String user = ConfigReader.getDatabaseUsername();
            String password = ConfigReader.getDatabasePassword();
            String driverClass = ConfigReader.getDatabaseDriver();

            try {
                if (driverClass != null && !driverClass.isEmpty()) {
                    Class.forName(driverClass);
                }
            } catch (ClassNotFoundException e) {
                System.err.println("Database driver class not found: " + driverClass);
            }

            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }

    /**
     * Executes a SELECT query and returns the results as a List of Maps (columnName -> columnValue).
     */
    public static List<Map<String, Object>> executeQuery(String sql) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL Query execution failed: " + sql + " | Error: " + e.getMessage(), e);
        }
        return results;
    }

    /**
     * Executes an INSERT, UPDATE, or DELETE query.
     * @return Number of affected rows.
     */
    public static int executeUpdate(String sql) {
        try (Statement stmt = getConnection().createStatement()) {
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("SQL Update execution failed: " + sql + " | Error: " + e.getMessage(), e);
        }
    }

    /**
     * Executes a query expecting a single scalar value (e.g. COUNT(*), single column).
     */
    public static Object executeSingleValue(String sql) {
        List<Map<String, Object>> rows = executeQuery(sql);
        if (!rows.isEmpty()) {
            Map<String, Object> firstRow = rows.get(0);
            if (!firstRow.isEmpty()) {
                return firstRow.values().iterator().next();
            }
        }
        return null;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
