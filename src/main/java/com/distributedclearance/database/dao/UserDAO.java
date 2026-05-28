package com.distributedclearance.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.distributedclearance.database.DatabaseManager;
import com.distributedclearance.models.Admin;
import com.distributedclearance.models.DeptOfficer;
import com.distributedclearance.models.Student;
import com.distributedclearance.models.User;
import com.distributedclearance.models.enums.Department;
import com.distributedclearance.models.enums.UserRole;

public class UserDAO {
    private final Connection connection;

    public UserDAO() {
        connection = DatabaseManager
                .getInstance()
                .getConnection();
    }

    public boolean saveUser(User user) {
        String sql =
                "INSERT INTO users " +
                "(username, password, full_name, role) " +
                "VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getRole().name());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public UserRole getUserRole(String username) {
        String sql = "SELECT role FROM users WHERE username = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return UserRole.valueOf(rs.getString("role"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean authenticateUser(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");

                return com.distributedclearance.utils
                        .PasswordUtil
                        .verifyPassword(password, storedHash);

                // return password.equals(storedHash);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User findUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {

                int id = rs.getInt("id");
                String dbUsername = rs.getString("username");
                String password = rs.getString("password");
                String fullName = rs.getString("full_name");

                UserRole role = UserRole.valueOf(rs.getString("role"));

                switch (role) {
                    case STUDENT:

                        String studentSql = "SELECT * FROM students WHERE user_id = ?";
                        PreparedStatement studentStmt = connection.prepareStatement(studentSql);
                        studentStmt.setInt(1, id);
                        ResultSet studentRs = studentStmt.executeQuery();

                        if (studentRs.next()) {
                            int studentTableId = studentRs.getInt("id");
                            String studentId = studentRs.getString("student_id");
                            String departmentName = studentRs.getString("department_name");
                            int batchYear = studentRs.getInt("batch_year");

                            return new Student(
                                    studentTableId,
                                    dbUsername,
                                    password,
                                    fullName,
                                    studentId,
                                    departmentName,
                                    batchYear
                            );
                        }

                        break;

                    case OFFICER:

                        String officerSql =
                                "SELECT * FROM department_officers WHERE user_id = ?";

                        PreparedStatement officerStmt =
                                connection.prepareStatement(officerSql);

                        officerStmt.setInt(1, id);
                        ResultSet officerRs =  officerStmt.executeQuery();

                        if (officerRs.next()) {
                            String department =
                                    officerRs.getString("department");

                            return new DeptOfficer(
                                    id,
                                    dbUsername,
                                    password,
                                    fullName,
                                    Department.valueOf(department),
                                    department
                            );
                        }

                        break;

                    case ADMIN:

                        return new Admin(
                                id,
                                dbUsername,
                                password,
                                fullName,
                                "SYSTEM_ADMIN"
                        );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}