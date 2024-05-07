package com.homework.code.entity;

public class User {
    private Long userId;
    private String accountName;
    private String role;

    public User(Long userId, String accountName, String role) {
        this.userId = userId;
        this.accountName = accountName;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
