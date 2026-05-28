package com.distributedclearance.models;

import java.io.Serializable;

import com.distributedclearance.models.enums.UserRole;

public abstract class User implements Serializable {

    private int id;
    private String username;
    private String password;
    private String fullName;
    private UserRole role;

    public User() {
    }

    public User(int id,
                String username,
                String password,
                String fullName,
                UserRole role) {

        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

}