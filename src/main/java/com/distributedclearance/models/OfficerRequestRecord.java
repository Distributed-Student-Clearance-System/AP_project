package com.distributedclearance.models;

public class OfficerRequestRecord {

    private final int approvalId;
    private final int requestId;
    private final String studentName;
    private final String status;

    public OfficerRequestRecord(
            int approvalId,
            int requestId,
            String studentName,
            String status
    ) {
        this.approvalId = approvalId;
        this.requestId = requestId;
        this.studentName = studentName;
        this.status = status;
    }

    public int getApprovalId() {
        return approvalId;
    }

    public int getRequestId() {
        return requestId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStatus() {
        return status;
    }
}