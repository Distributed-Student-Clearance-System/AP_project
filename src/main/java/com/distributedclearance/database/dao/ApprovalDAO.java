package com.distributedclearance.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.distributedclearance.database.DatabaseManager;
import com.distributedclearance.models.Approval;
import com.distributedclearance.models.OfficerRequestRecord;
import com.distributedclearance.models.enums.ApprovalStatus;
import com.distributedclearance.models.enums.Department;
import com.distributedclearance.models.enums.RequestStatus;

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

    public List<OfficerRequestRecord> getPendingRequestsForDepartment(
            Department department
    ) {
        List<OfficerRequestRecord> requests = new ArrayList<>();

        String sql =
                "SELECT a.id AS approval_id, a.request_id, u.full_name, a.status " +
                "FROM approvals a " +
                "JOIN clearance_requests r ON a.request_id = r.request_id " +
                "JOIN students s ON r.student_id = s.id " +
                "JOIN users u ON s.user_id = u.id " +
                "WHERE a.department_name = ? " +
                "AND a.status = 'PENDING' " +
                "ORDER BY r.submitted_at ASC, a.id ASC";

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, department.name());

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                requests.add(
                        new OfficerRequestRecord(
                                rs.getInt("approval_id"),
                                rs.getInt("request_id"),
                                rs.getString("full_name"),
                                rs.getString("status")
                        )
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return requests;
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
        RequestStatus finalStatus = determineFinalRequestStatus(approvals);

        RequestDAO requestDAO = new RequestDAO();
        requestDAO.updateRequestStatus(requestId, finalStatus.name());
    }

    public RequestStatus determineFinalRequestStatus(List<Approval> approvals) {
        if (approvals == null || approvals.isEmpty()) {
            return RequestStatus.PENDING;
        }

        boolean allApproved = true;

        for (Approval approval : approvals) {
            if (approval.getStatus() == ApprovalStatus.REJECTED) {
                return RequestStatus.REJECTED;
            }

            if (approval.getStatus() != ApprovalStatus.APPROVED) {
                allApproved = false;
            }
        }

        if (allApproved) {
            return RequestStatus.CLEARED;
        }

        return RequestStatus.PENDING;
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