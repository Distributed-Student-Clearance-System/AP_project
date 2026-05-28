package com.distributedclearance.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.distributedclearance.utils.Constants;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private DatabaseManager() {
        connect();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }

        return instance;
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection(
                    Constants.DB_URL,
                    Constants.DB_USERNAME,
                    Constants.DB_PASSWORD
            );

            System.out.println("Database connected successfully.");

        } catch (SQLException e) {
            System.out.println("Database connection failed.");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}