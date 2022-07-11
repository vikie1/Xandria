package com.xandria.tech.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.xandria.tech.dto.Location;

import java.util.Calendar;

public class OrdersModel implements Parcelable {
    private String orderId;
    private String bookId;
    private String bookTitle;
    private String bookImageUrl;
    private String userId;
    private String hostLocationUserId;
    private Location hostAddress; // Pick up location
    private Location dropLocation; // Buyer Location
    private String buyerUserId;
    private String dateOrdered;
    private String deliveryContact;
    private boolean isBorrowConfirmed;
    private boolean isReturned;

    // constructors
    public OrdersModel(){}
    public OrdersModel(String orderId, String bookId, String userId, String hostLocationUserId,
                       Location hostAddress, String buyerUserId,
                       String bookTitle, String bookImageUrl){
        setBookId(bookId);
        setOrderId(orderId);
        setUserId(userId);
        setHostAddress(hostAddress);
        setHostLocationUserId(hostLocationUserId);
        setBuyerUserId(buyerUserId);
        setBookImageUrl(bookImageUrl);
        setBookTitle(bookTitle);
    }

    protected OrdersModel(Parcel in) {
        orderId = in.readString();
        bookId = in.readString();
        bookTitle = in.readString();
        bookImageUrl = in.readString();
        userId = in.readString();
        hostLocationUserId = in.readString();
        hostAddress = in.readParcelable(Location.class.getClassLoader());
        dropLocation = in.readParcelable(Location.class.getClassLoader());
        buyerUserId = in.readString();
        dateOrdered = in.readString();
        deliveryContact = in.readString();
        isBorrowConfirmed = in.readByte() != 0;
        isReturned = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderId);
        dest.writeString(bookId);
        dest.writeString(bookTitle);
        dest.writeString(bookImageUrl);
        dest.writeString(userId);
        dest.writeString(hostLocationUserId);
        dest.writeParcelable(hostAddress, flags);
        dest.writeParcelable(dropLocation, flags);
        dest.writeString(buyerUserId);
        dest.writeString(dateOrdered);
        dest.writeString(deliveryContact);
        dest.writeByte((byte) (isBorrowConfirmed ? 1 : 0));
        dest.writeByte((byte) (isReturned ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrdersModel> CREATOR = new Creator<OrdersModel>() {
        @Override
        public OrdersModel createFromParcel(Parcel in) {
            return new OrdersModel(in);
        }

        @Override
        public OrdersModel[] newArray(int size) {
            return new OrdersModel[size];
        }
    };

    // getters
    public String getOrderId() {
        return orderId;
    }

    public String getDeliveryContact() {
        return deliveryContact;
    }

    public void setDeliveryContact(String deliveryContact) {
        this.deliveryContact = deliveryContact;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getDateOrdered() {
        if (dateOrdered==null) setDateOrdered();
        return dateOrdered;
    }

    public boolean isBorrowConfirmed() {
        return isBorrowConfirmed;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public String getBookId() {
        return bookId;
    }

    public String getBuyerUserId() {
        return buyerUserId;
    }

    public Location getDropLocation() {
        return dropLocation;
    }

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public Location getHostAddress() {
        return hostAddress;
    }

    public String getHostLocationUserId() {
        return hostLocationUserId;
    }

    public String getUserId() {
        return userId;
    }

    // setters
    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public void setBuyerUserId(String buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    public void setDropLocation(Location dropLocation) {
        this.dropLocation = dropLocation;
    }

    public void setHostAddress(Location hostAddress) {
        this.hostAddress = hostAddress;
    }

    public void setHostLocationUserId(String hostLocationUserId) {
        this.hostLocationUserId = hostLocationUserId;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public void setBorrowConfirmed(boolean borrowConfirmed) {
        isBorrowConfirmed = borrowConfirmed;
    }

    public void setReturned(boolean returnConfirmed) {
        isReturned = returnConfirmed;
    }

    public void setDateOrdered() {
        this.dateOrdered = Calendar.getInstance().getTime().toString();
    }

    public void setDateOrdered(String dateOrdered) {
        this.dateOrdered = dateOrdered;
    }

    public void setBookImageUrl(String bookImageUrl) {
        this.bookImageUrl = bookImageUrl;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @NonNull
    @Override
    public String toString() {
        return "OrdersModel{" +
                "orderId='" + orderId + '\'' +
                ", bookId='" + bookId + '\'' +
                ", bookImageUrl='" + bookImageUrl + '\'' +
                ", userId='" + userId + '\'' +
                ", hostLocationUserId='" + hostLocationUserId + '\'' +
                ", hostAddress='" + hostAddress + '\'' +
                ", dropLocation='" + dropLocation + '\'' +
                ", buyerUserId='" + buyerUserId + '\'' +
                ", dateOrdered='" + dateOrdered + '\'' +
                '}';
    }
}
