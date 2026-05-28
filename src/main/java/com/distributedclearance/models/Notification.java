package com.distributedclearance.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.distributedclearance.models.enums.NotificationType;

public class Notification implements Serializable {

    private int notificationId;

    private User recipient;

    private String message;

    private NotificationType type;

    private LocalDateTime createdAt;

    private boolean isRead;

    public Notification() {
    }

    public Notification(int notificationId,
                        User recipient,
                        String message,
                        NotificationType type,
                        LocalDateTime createdAt,
                        boolean isRead) {

        this.notificationId = notificationId;
        this.recipient = recipient;
        this.message = message;
        this.type = type;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", recipient=" + recipient +
                ", type=" + type +
                ", createdAt=" + createdAt +
                ", isRead=" + isRead +
                '}';
    }
}