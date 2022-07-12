package com.xandria.tech.model;

import java.time.LocalDateTime;

public class DiscussionModel {
    private String sender;
    private String message;
    private String timeSent;
    private String senderName;

    public DiscussionModel(){}
    public DiscussionModel(String sender, String message, String timeSent, String senderName) {
        this.sender = sender;
        this.message = message;
        this.timeSent = timeSent;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(){
        this.timeSent = LocalDateTime.now().toString();
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }
}
