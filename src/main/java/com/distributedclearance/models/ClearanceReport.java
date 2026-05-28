package com.distributedclearance.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ClearanceReport implements Serializable {

    private int reportId;

    private ClearanceRequest request;

    private LocalDateTime generatedAt;

    private String finalStatus;

    private String summary;

    public ClearanceReport() {
    }

    public ClearanceReport(int reportId,
                           ClearanceRequest request,
                           LocalDateTime generatedAt,
                           String finalStatus,
                           String summary) {

        this.reportId = reportId;
        this.request = request;
        this.generatedAt = generatedAt;
        this.finalStatus = finalStatus;
        this.summary = summary;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public ClearanceRequest getRequest() {
        return request;
    }

    public void setRequest(ClearanceRequest request) {
        this.request = request;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getFinalStatus() {
        return finalStatus;
    }

    public void setFinalStatus(String finalStatus) {
        this.finalStatus = finalStatus;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "ClearanceReport{" +
                "reportId=" + reportId +
                ", generatedAt=" + generatedAt +
                ", finalStatus='" + finalStatus + '\'' +
                '}';
    }
}