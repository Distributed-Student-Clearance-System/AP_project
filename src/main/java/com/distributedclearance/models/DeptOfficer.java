package com.distributedclearance.models;

import com.distributedclearance.models.enums.Department;
import com.distributedclearance.models.enums.UserRole;

public class DeptOfficer extends User {

    private Department department;
    private String officeName;

    public DeptOfficer() {
        super();
    }

    public DeptOfficer(int id,
                       String username,
                       String password,
                       String fullName,
                       Department department,
                       String officeName) {

        super(id, username, password, fullName, UserRole.OFFICER);

        this.department = department;
        this.officeName = officeName;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    @Override
    public String toString() {
        return "DeptOfficer{" +
                "department=" + department +
                ", officeName='" + officeName + '\'' +
                '}';
    }
}