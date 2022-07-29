package com.xandria.tech.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.xandria.tech.dto.Location;

import java.util.Objects;

public class BookRecyclerModel implements Parcelable {
    private String bookID;
    private String title;
    private String subtitle;
    private String authors;
    private String publisher = null;
    private String publishedDate = null;
    private String description;
    private String pageCount;
    private String userId; // this is the current owner of the book in the app
    private String thumbnail;
    private String previewLink = null;
    private String ISBN = null;
    private String category;
    private Location location;
    private double value = 0.00; // the value of a book is expressed in points not rupees

    protected BookRecyclerModel(Parcel in) {
        bookID = in.readString();
        title = in.readString();
        subtitle = in.readString();
        authors = in.readString();
        publisher = in.readString();
        publishedDate = in.readString();
        description = in.readString();
        pageCount = in.readString();
        userId = in.readString();
        thumbnail = in.readString();
        previewLink = in.readString();
        ISBN = in.readString();
        category = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
        value = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookID);
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeString(authors);
        dest.writeString(publisher);
        dest.writeString(publishedDate);
        dest.writeString(description);
        dest.writeString(pageCount);
        dest.writeString(userId);
        dest.writeString(thumbnail);
        dest.writeString(previewLink);
        dest.writeString(ISBN);
        dest.writeString(category);
        dest.writeParcelable(location, flags);
        dest.writeDouble(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BookRecyclerModel> CREATOR = new Creator<BookRecyclerModel>() {
        @Override
        public BookRecyclerModel createFromParcel(Parcel in) {
            return new BookRecyclerModel(in);
        }

        @Override
        public BookRecyclerModel[] newArray(int size) {
            return new BookRecyclerModel[size];
        }
    };

    public Location getLocation() {
        return location;
    }

    public double getValue() {
        return value;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getUserId() {
        return userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPageCount() {
        return pageCount;
    }

    public void setPageCount(String pageCount) {
        this.pageCount = pageCount;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPreviewLink() {
        return previewLink;
    }

    public void setPreviewLink(String previewLink) {
        this.previewLink = previewLink;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public BookRecyclerModel(String title, String subtitle, String authors,
                             String publisher, String publishedDate, String description,
                             String  pageCount, String thumbnail, String previewLink, String ISBN, String bookID) {
        this.title = title;
        this.subtitle = subtitle;
        this.authors = authors;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.pageCount = pageCount;
        this.thumbnail = thumbnail;
        this.previewLink = previewLink;
        this.ISBN = ISBN;
        this.bookID =bookID;
    }
    public BookRecyclerModel(String title, String subtitle, String authors,
                             String description, String pageCount, String thumbnail, String bookID) {
        this.title = title;
        this.subtitle = subtitle;
        this.authors = authors;
        this.publisher = null;
        this.publishedDate = null;
        this.description = description;
        this.pageCount = pageCount;
        this.thumbnail = thumbnail;
        this.previewLink = null;
        this.ISBN = null;
        this.bookID = bookID;
    }

    public BookRecyclerModel(){} //no args for firebase object fields creation

    // for logging purposed
    @NonNull
    @Override
    public String toString() {
        return "\n\nBookRecyclerModel{\n" +
                "bookID='" + bookID + "'\n" +
                ", title='" + title + "'\n" +
                ", subtitle='" + subtitle + "'\n" +
                ", authors='" + authors + "'\n" +
                ", publisher='" + publisher + "'\n" +
                ", publishedDate='" + publishedDate + "'\n" +
                ", description='" + description + "'\n" +
                ", pageCount='" + pageCount + "'\n" +
                ", userId='" + userId + "'\n" +
                ", thumbnail='" + thumbnail + "'\n" +
                ", previewLink='" + previewLink + "'\n" +
                ", ISBN='" + ISBN + "'\n" +
                ", Category='" + category + "'\n" +
                "}\n\n";
    }

    // for comparisons
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookRecyclerModel that = (BookRecyclerModel) o;
        return Double.compare(that.getValue(), getValue()) == 0 &&
                Objects.equals(getBookID(), that.getBookID()) &&
                getTitle().equals(that.getTitle()) &&
                Objects.equals(getSubtitle(), that.getSubtitle()) &&
                Objects.equals(getAuthors(), that.getAuthors()) &&
                Objects.equals(getPublisher(), that.getPublisher()) &&
                Objects.equals(getPublishedDate(), that.getPublishedDate()) &&
                Objects.equals(getDescription(), that.getDescription()) &&
                Objects.equals(getPageCount(), that.getPageCount()) &&
                Objects.equals(getUserId(), that.getUserId()) &&
                Objects.equals(getThumbnail(), that.getThumbnail()) &&
                Objects.equals(getPreviewLink(), that.getPreviewLink()) &&
                Objects.equals(getISBN(), that.getISBN()) &&
                Objects.equals(getCategory(), that.getCategory()) &&
                Objects.equals(getLocation(), that.getLocation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBookID(), getTitle(), getSubtitle(), getAuthors(), getPublisher(), getPublishedDate(), getDescription(), getPageCount(), getUserId(), getThumbnail(), getPreviewLink(), getISBN(), getCategory(), getLocation(), getValue());
    }
}
