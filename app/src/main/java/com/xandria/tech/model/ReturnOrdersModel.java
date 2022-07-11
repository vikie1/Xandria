package com.xandria.tech.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.xandria.tech.dto.Location;

public class ReturnOrdersModel implements Parcelable {
    private String returnId;
    private String orderId;
    private String bookId;
    private String bookTitle;
    private String bookThumbNailUrl;
    private Location returnAddress; // Buy order host location(OrdersModel)
    private String hostId;
    private Location pickUpLocation; // the drop location in (OrdersModel)
    private String buyerId;

    // constructors
    public ReturnOrdersModel(){}
    public ReturnOrdersModel(String returnId, String orderId, Location returnAddress, String hostId, String bookTitle,
                              Location pickUpLocation, String buyerId, String bookId, String bookThumbNailUrl){
        setBuyerId(buyerId);
        setHostId(hostId);
        setReturnId(returnId);
        setReturnAddress(returnAddress);
        setOrderId(orderId);
        setPickUpLocation(pickUpLocation);
        setBookId(bookId);
        setBookTitle(bookTitle);
        setBookThumbNailUrl(bookThumbNailUrl);
    }

    protected ReturnOrdersModel(Parcel in) {
        returnId = in.readString();
        orderId = in.readString();
        bookId = in.readString();
        bookTitle = in.readString();
        bookThumbNailUrl = in.readString();
        returnAddress = in.readParcelable(Location.class.getClassLoader());
        hostId = in.readString();
        pickUpLocation = in.readParcelable(Location.class.getClassLoader());
        buyerId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(returnId);
        dest.writeString(orderId);
        dest.writeString(bookId);
        dest.writeString(bookTitle);
        dest.writeString(bookThumbNailUrl);
        dest.writeParcelable(returnAddress, flags);
        dest.writeString(hostId);
        dest.writeParcelable(pickUpLocation, flags);
        dest.writeString(buyerId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReturnOrdersModel> CREATOR = new Creator<ReturnOrdersModel>() {
        @Override
        public ReturnOrdersModel createFromParcel(Parcel in) {
            return new ReturnOrdersModel(in);
        }

        @Override
        public ReturnOrdersModel[] newArray(int size) {
            return new ReturnOrdersModel[size];
        }
    };

    // getters
    public String getOrderId() {
        return orderId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookId() {
        return bookId;
    }

    public String getBookThumbNailUrl() {
        return bookThumbNailUrl;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public String getHostId() {
        return hostId;
    }

    public Location getPickUpLocation() {
        return pickUpLocation;
    }

    public Location getReturnAddress() {
        return returnAddress;
    }

    public String getReturnId() {
        return returnId;
    }

    // setters
    public void setReturnId(String returnId) {
        this.returnId = returnId;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setReturnAddress(Location returnAddress) {
        this.returnAddress = returnAddress;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setBookThumbNailUrl(String bookThumbNailUrl) {
        this.bookThumbNailUrl = bookThumbNailUrl;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public void setPickUpLocation(Location pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

}
