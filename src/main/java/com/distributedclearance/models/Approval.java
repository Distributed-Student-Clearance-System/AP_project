package com.distributedclearance.models;

import com.distributedclearance.models.enums.ApprovalStatus;
import com.distributedclearance.models.enums.Department;

public class Approval {

    private int id;
    private int requestId;
    private Department department;
    private ApprovalStatus status;
    private String comment;

    public Approval(
            int id,
            int requestId,
            Department department,
            ApprovalStatus status,
            String comment
    ) {

        this.id = id;
        this.requestId = requestId;
        this.department = department;
        this.status = status;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public int getRequestId() {
        return requestId;
    }

    public Department getDepartment() {
        return department;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public String getComment() {
        return comment;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}