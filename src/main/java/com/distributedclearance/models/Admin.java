package com.distributedclearance.models;

import com.distributedclearance.models.enums.UserRole;

public class Admin extends User {

    private String adminLevel;

    public Admin() {
        super();
    }

    public Admin(int id,
                 String username,
                 String password,
                 String fullName,
                 String adminLevel) {

        super(id, username, password, fullName, UserRole.ADMIN);

        this.adminLevel = adminLevel;
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminLevel='" + adminLevel + '\'' +
                '}';
    }
}