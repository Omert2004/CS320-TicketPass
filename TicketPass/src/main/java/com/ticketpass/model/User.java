package com.ticketpass.model;

public class User {
    private int userId;
    private String username;
    private String email;
    private String passwordHash;
    private Role role;
    private boolean isLocked;
    private int failedAttempts;
    private LocalDateTime createdAt;

    public User() {}

    public User(int userId, String username, String email, String passwordHash, Role role, boolean isLocked, int failedAttempts, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.isLocked = isLocked;
        this.failedAttempts = failedAttempts;
        this.createdAt = createdAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
