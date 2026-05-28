package com.distributedclearance.models;

import com.distributedclearance.models.enums.UserRole;

public class Student extends User {

    private String studentId;
    private String departmentName;
    private int batchYear;

    public Student() {
        super();
    }

    public Student(int id,
                   String username,
                   String password,
                   String fullName,
                   String studentId,
                   String departmentName,
                   int batchYear) {

        super(id, username, password, fullName, UserRole.STUDENT);

        this.studentId = studentId;
        this.departmentName = departmentName;
        this.batchYear = batchYear;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public int getBatchYear() {
        return batchYear;
    }

    public void setBatchYear(int batchYear) {
        this.batchYear = batchYear;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", batchYear=" + batchYear +
                '}';
    }
}