package com.xandria.tech.dto;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.xandria.tech.util.GoogleServices;

public class Location implements Parcelable {
    private String streetAddress;
    private String address;
    private String locality;
    private String city;
    private String pinCode;
    private Double longitude;
    private Double latitude;

    public Location(){}
    public Location(String address, Double longitude, Double latitude) {
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public Location(Context context, String streetAddress, String locality, String city, String pinCode){
        setCity(city);
        setLocality(locality);
        setPinCode(pinCode);
        setStreetAddress(streetAddress);
        setAddress(streetAddress + " " + city + " " + locality + " " + pinCode);
        setCoordinates(context);
    }

    protected Location(Parcel in) {
        streetAddress = in.readString();
        address = in.readString();
        locality = in.readString();
        city = in.readString();
        pinCode = in.readString();
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getLocality() {
        return locality;
    }

    public String getPinCode() {
        return pinCode;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setCoordinates(Context context){
        LatLng latLng = GoogleServices.getLocationFromAddress(context, address);
        if (latLng != null) {
            setLatitude(latLng.latitude);
            setLongitude(latLng.longitude);
        }
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @NonNull
    @Override
    public String toString() {
        return "Location{" +
                "streetAddress='" + streetAddress + '\'' +
                ", address='" + address + '\'' +
                ", locality='" + locality + '\'' +
                ", city='" + city + '\'' +
                ", pinCode='" + pinCode + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(streetAddress);
        dest.writeString(address);
        dest.writeString(locality);
        dest.writeString(city);
        dest.writeString(pinCode);
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
    }
}
