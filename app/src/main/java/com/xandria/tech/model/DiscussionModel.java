package com.xandria.tech.model;

import java.util.Calendar;

public class DiscussionModel {
    private String sender;
    private String message;
    private String timeSent;

    public DiscussionModel(){}
    public DiscussionModel(String sender, String message, String timeSent) {
        this.sender = sender;
        this.message = message;
        this.timeSent = timeSent;
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
        this.timeSent = Calendar.getInstance().getTime().toString();
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }
}
