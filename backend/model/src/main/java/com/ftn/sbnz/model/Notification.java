package com.ftn.sbnz.model;

import java.util.Date;

public class Notification {
    private String type;
    private String recipient;
    private String dumpsiteId;
    private String message;
    private Date createdAt;

    public Notification(String type, String recipient, String dumpsiteId, String message) {
        this.type = type;
        this.recipient = recipient;
        this.dumpsiteId = dumpsiteId;
        this.message = message;
        this.createdAt = new Date();
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getDumpsiteId() { return dumpsiteId; }
    public void setDumpsiteId(String dumpsiteId) { this.dumpsiteId = dumpsiteId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Date getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return "[" + type + "] -> " + recipient + ": " + message + " (deponija: " + dumpsiteId + ")";
    }
}