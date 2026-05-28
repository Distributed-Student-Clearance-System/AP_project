package com.distributedclearance.models;

import com.distributedclearance.models.enums.RequestStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClearanceRequest implements Serializable {

    private int requestId;

    private Student student;

    private LocalDateTime submittedAt;

    private RequestStatus status;

    private List<DeptApproval> approvals;

    public ClearanceRequest() {

        approvals = new ArrayList<>();
    }

    public ClearanceRequest(int requestId,
                            Student student,
                            LocalDateTime submittedAt,
                            RequestStatus status) {

        this.requestId = requestId;
        this.student = student;
        this.submittedAt = submittedAt;
        this.status = status;

        this.approvals = new ArrayList<>();
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public List<DeptApproval> getApprovals() {
        return approvals;
    }

    public void setApprovals(List<DeptApproval> approvals) {
        this.approvals = approvals;
    }

    public void addApproval(DeptApproval approval) {
        approvals.add(approval);
    }

    @Override
    public String toString() {
        return "ClearanceRequest{" +
                "requestId=" + requestId +
                ", student=" + student +
                ", submittedAt=" + submittedAt +
                ", status=" + status +
                '}';
    }
}