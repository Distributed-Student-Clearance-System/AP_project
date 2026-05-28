package com.distributedclearance.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.distributedclearance.models.enums.Department;
import com.distributedclearance.models.enums.RequestStatus;

public class DeptApproval implements Serializable {

    private Department department;

    private DeptOfficer officer;

    private RequestStatus status;

    private String comment;

    private LocalDateTime reviewedAt;

    public DeptApproval() {
    }

    public DeptApproval(Department department,
                        DeptOfficer officer,
                        RequestStatus status,
                        String comment,
                        LocalDateTime reviewedAt) {

        this.department = department;
        this.officer = officer;
        this.status = status;
        this.comment = comment;
        this.reviewedAt = reviewedAt;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public DeptOfficer getOfficer() {
        return officer;
    }

    public void setOfficer(DeptOfficer officer) {
        this.officer = officer;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    @Override
    public String toString() {
        return "DeptApproval{" +
                "department=" + department +
                ", officer=" + officer +
                ", status=" + status +
                ", reviewedAt=" + reviewedAt +
                '}';
    }
}