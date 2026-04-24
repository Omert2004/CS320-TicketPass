package com.ticketpass.model;

import java.time.LocalDateTime;

public class Transaction {
    private int transactionId;
    private int userId;
    private String lastFourDigits;
    private String token;
    private TransactionStatus status;
    private LocalDateTime timestamp;

    public Transaction() {}

    public Transaction(int transactionId, int userId, String lastFourDigits, String token, TransactionStatus status, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.lastFourDigits = lastFourDigits;
        this.token = token;
        this.status = status;
        this.timestamp = timestamp;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLastFourDigits() {
        return lastFourDigits;
    }

    public void setLastFourDigits(String lastFourDigits) {
        this.lastFourDigits = lastFourDigits;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}