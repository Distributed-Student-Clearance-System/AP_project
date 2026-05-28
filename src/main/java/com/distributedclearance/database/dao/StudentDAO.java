package com.distributedclearance.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.distributedclearance.database.DatabaseManager;
import com.distributedclearance.models.Student;

public class StudentDAO {
    private final Connection connection;
    public StudentDAO() {
        connection = DatabaseManager
                .getInstance()
                .getConnection();
    }

    public boolean saveStudent(Student student) {
        String insertUserSql =
                "INSERT INTO users " +
                "(username, password, full_name, role) " +
                "VALUES (?, ?, ?, ?)";

        String insertStudentSql =
                "INSERT INTO students " +
                "(user_id, student_id, department_name, batch_year) " +
                "VALUES (?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            PreparedStatement userStmt =
                    connection.prepareStatement(
                            insertUserSql,
                            PreparedStatement.RETURN_GENERATED_KEYS
                    );

            userStmt.setString(1, student.getUsername());
            userStmt.setString(2, student.getPassword());
            userStmt.setString(3, student.getFullName());
            userStmt.setString(4, student.getRole().name());

            int affectedRows = userStmt.executeUpdate();

            if (affectedRows == 0) {
                connection.rollback();
                return false;
            }

            ResultSet generatedKeys = userStmt.getGeneratedKeys();

            if (!generatedKeys.next()) {
                connection.rollback();
                return false;
            }

            int generatedUserId = generatedKeys.getInt(1);

            // student.setId(generatedUserId);

            PreparedStatement studentStmt = connection.prepareStatement( insertStudentSql, PreparedStatement.RETURN_GENERATED_KEYS);
            
            studentStmt.setInt(1, generatedUserId);
            studentStmt.setString(2, student.getStudentId());
            studentStmt.setString(3, student.getDepartmentName());
            studentStmt.setInt(4, student.getBatchYear());

            int studentRows = studentStmt.executeUpdate();
            ResultSet studentKeys = studentStmt.getGeneratedKeys();

            if (studentKeys.next()) {
                int generatedStudentId = studentKeys.getInt(1);
                student.setId(generatedStudentId);
            }

            if (studentRows == 0) {
                connection.rollback();
                return false;
            }
            connection.commit();
            return true;

        } catch (SQLException e) {

            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            e.printStackTrace();
        }

        return false;
    }
}