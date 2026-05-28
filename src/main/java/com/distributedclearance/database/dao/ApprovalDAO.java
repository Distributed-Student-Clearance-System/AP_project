package com.distributedclearance.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.distributedclearance.database.DatabaseManager;
import com.distributedclearance.models.Approval;
import com.distributedclearance.models.enums.ApprovalStatus;
import com.distributedclearance.models.enums.Department;

public class ApprovalDAO {
    private final Connection connection;

    public ApprovalDAO() {
        connection = DatabaseManager.getInstance().getConnection();
    }

    public void createInitialApprovals(int requestId
    ) {

        String sql =
                "INSERT INTO approvals " +
                "(request_id, department_name, status, comment) " +
                "VALUES (?, ?, ?, ?)";

        try {

            for (Department department : Department.values()) {
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, requestId);
                pstmt.setString(2, department.name());

                pstmt.setString(3, ApprovalStatus.PENDING.name());
                pstmt.setString(4,"");
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Approval> getPendingApprovals(Department department) {
        List<Approval> approvals = new ArrayList<>();
        String sql =
                "SELECT * FROM approvals " +
                "WHERE department_name = ? " +
                "AND status = 'PENDING'";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1,department.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Approval approval = new Approval(rs.getInt("id"), rs.getInt("request_id"),Department.valueOf(rs.getString("department_name")),
                                ApprovalStatus.valueOf(rs.getString("status")), rs.getString("comment"));

                approvals.add(approval);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return approvals;
    }

    public void updateApprovalStatus(int approvalId, ApprovalStatus status, String comment) {
        String sql =
                "UPDATE approvals " +
                "SET status = ?, comment = ? " +
                "WHERE id = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1,status.name());

            pstmt.setString(2,comment);
            pstmt.setInt(3,approvalId);

            pstmt.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    public List<Approval> getApprovalsByRequestId(int requestId) {

        List<Approval> approvals = new ArrayList<>();

        String sql =
                "SELECT * FROM approvals " +
                "WHERE request_id = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, requestId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Approval approval = new Approval(

                                rs.getInt("id"),

                                rs.getInt("request_id"),

                                Department.valueOf(
                                        rs.getString(
                                                "department_name"
                                        )
                                ),

                                ApprovalStatus.valueOf(
                                        rs.getString("status")
                                ),

                                rs.getString("comment")
                        );

                approvals.add(approval);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return approvals;
    }

    public List<Approval> getApprovalsByRequest(int requestId)    {
        List<Approval> approvals = new ArrayList<>();

        String sql =
                "SELECT * FROM approvals " +
                "WHERE request_id = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, requestId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Approval approval =
                        new Approval(
                                rs.getInt("id"),
                                rs.getInt("request_id"),

                                Department.valueOf(
                                        rs.getString(
                                                "department_name"
                                        )
                                ),

                                ApprovalStatus.valueOf(
                                        rs.getString("status")
                                ),

                                rs.getString("comment")
                        );

                approvals.add(approval);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return approvals;
    }

    public void processFinalRequestStatus(int requestId) {
        List<Approval> approvals = getApprovalsByRequest(requestId);
        boolean allApproved = true;

        for (Approval approval : approvals) {
            if (approval.getStatus() == ApprovalStatus.REJECTED) {

                RequestDAO requestDAO = new RequestDAO();
                requestDAO.updateRequestStatus(requestId, "REJECTED");

                return;
            }

            if (approval.getStatus() != ApprovalStatus.APPROVED) {
                allApproved = false;
            }
        }

        if (allApproved) {
            RequestDAO requestDAO = new RequestDAO();
            requestDAO.updateRequestStatus(requestId, "APPROVED");
        }
    }

    public Approval getApprovalByRequestAndDepartment(int requestId, Department department) {

        String sql =
                "SELECT * FROM approvals " +
                "WHERE request_id = ? " +
                "AND department_name = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);

            pstmt.setInt(1, requestId);
            pstmt.setString(2, department.name());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Approval(
                        rs.getInt("id"),
                        rs.getInt("request_id"),

                        Department.valueOf(rs.getString("department_name")),
                        ApprovalStatus.valueOf(
                                rs.getString("status")
                        ),

                        rs.getString("comment")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}