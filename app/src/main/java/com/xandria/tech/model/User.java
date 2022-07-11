package com.xandria.tech.model;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.xandria.tech.dto.Location;
import com.xandria.tech.util.GoogleServices;

public class User {
    private String name;
    private String userId;
    private String email;
    private String phoneNumber;
    private Location location;

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

    public Location getLocation() {
        return location;
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

    public void setLocation( Context context, String address) {
        LatLng latLng = GoogleServices.getLocationFromAddress(context, address);
        if (latLng != null)
            this.location = new Location(address, latLng.longitude, latLng.latitude);
        else {
            this.location = new Location(address, null, null);
            Toast.makeText(context, "The latitude and longitude not found from address", Toast.LENGTH_LONG).show();
        }
    }

    public void setLocation(Location location) {
        this.location = location;
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
