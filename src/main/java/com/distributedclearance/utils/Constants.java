package com.distributedclearance.utils;

public class Constants {

    // Server Configuration
    public static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 5000;

    // RMI Configuration
    public static final int RMI_PORT = 1099;
    public static final String RMI_SERVICE_NAME = "ClearanceService";

    // Database Configuration
    public static final String DB_URL = "jdbc:mysql://localhost:3306/clearance_system";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "";

    // Application Settings
    public static final int MAX_NOTIFICATION_LENGTH = 255;

    private Constants() {
    }
}