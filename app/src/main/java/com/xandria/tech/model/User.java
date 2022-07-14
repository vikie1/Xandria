package com.xandria.tech.model;

public class User {
    private String name;
    private String userId;
    private String email;
    private String phoneNumber;
    private double points;

    // constructors
    public User(){}
    public User(String name, String userId, String email, String phoneNumber){
        setEmail(email);
        setName(name);
        setPhoneNumber(phoneNumber);
        setUserId(userId);
    }

    // getters
    public String getUserId() {
        return userId;
    }

    public double getPoints() {
        return points;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    // setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
