package com.example.saveme.category;

import android.graphics.Bitmap;

import java.util.Date;

public class Document {
    private String title;
    private String comment;
    private String expirationDate;
    private Bitmap bitmap;
    private Boolean hasPicture;

    // todo add attr for images
    public Document() {
    }

    public Document(String title, String comment, String expirationDate, Bitmap bitmap, Boolean hasPicture) {
        this.title = title;
        this.comment = comment;
        this.expirationDate = expirationDate;
        this.bitmap = bitmap;
        this.hasPicture = hasPicture;
    }

    public Document(String title, String comment, String expirationDate, Boolean hasPicture) {
        this.title = title;
        this.comment = comment;
        this.expirationDate = expirationDate;
    }


    //getters and setters
    public String getTitle() {
        return title;
    }

    public String getComment() {
        return comment;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Boolean getHasPicture() {
        return hasPicture;
    }

    public void setHasPicture(Boolean hasPicture) {
        this.hasPicture = hasPicture;
    }
}
