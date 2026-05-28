package com.distributedclearance.utils;

public class ValidationUtil {

    private ValidationUtil() {
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isValidStudentId(String studentId) {
        return studentId != null && studentId.matches("^[A-Z]{2,4}/\\d{2}/\\d{4}$");
    }
}