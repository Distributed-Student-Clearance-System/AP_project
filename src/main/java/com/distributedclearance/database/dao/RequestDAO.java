package com.distributedclearance.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import com.distributedclearance.database.DatabaseManager;
import com.distributedclearance.models.Student;

public class RequestDAO {
    private final Connection connection;

    public RequestDAO() {
        connection = DatabaseManager.getInstance().getConnection();
    }

    public boolean submitRequest(Student student) {
        String sql =
                "INSERT INTO clearance_requests " +
                "(student_id, submitted_at, status) " +
                "VALUES (?, ?, ?)";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, student.getId());
            pstmt.setObject(2, LocalDateTime.now());
            pstmt.setString(3, "PENDING");

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();

                if (generatedKeys.next()) {
                    int requestId = generatedKeys.getInt(1);
                    ApprovalDAO approvalDAO = new ApprovalDAO();

                    approvalDAO.createInitialApprovals(requestId);
                }
            }
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public int getLatestRequestId(int studentId) {
        String sql =
                "SELECT request_id " +
                "FROM clearance_requests " +
                "WHERE student_id = ? " +
                "ORDER BY request_id DESC " +
                "LIMIT 1";

        try {

            PreparedStatement pstmt =
                    connection.prepareStatement(sql);

            pstmt.setInt(1, studentId);

            ResultSet rs =
                    pstmt.executeQuery();

            if (rs.next()) {

                return rs.getInt("request_id");
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return -1;
    }

    public void updateRequestStatus(int requestId, String status) {
        String sql =
                "UPDATE clearance_requests " +
                "SET status = ? " +
                "WHERE request_id = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, requestId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}