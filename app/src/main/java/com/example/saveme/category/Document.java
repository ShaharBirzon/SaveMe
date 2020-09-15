package com.example.saveme.category;

import android.graphics.Bitmap;

import java.util.Date;

public class Document {
    private String title;
    private String comment;
    private String expirationDate;
    private Bitmap bitmap;
    private boolean hasPicture;
    private boolean hasAlarm;

    // todo add attr for images
    public Document() {
    }

    public Document(String title, String comment, String expirationDate, Bitmap bitmap, boolean hasPicture) {
        this.title = title;
        this.comment = comment;
        this.expirationDate = expirationDate;
        this.bitmap = bitmap;
        this.hasPicture = hasPicture;
    }

    public Document(String title, String comment, String expirationDate, boolean hasPicture, boolean hasAlarm) {
        this.title = title;
        this.comment = comment;
        this.expirationDate = expirationDate;
        this.hasPicture = hasPicture;
        this.hasAlarm = hasAlarm;
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

    public void setHasAlarm(boolean hasAlarm){ this.hasAlarm = hasAlarm; }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean getHasPicture() {
        return hasPicture;
    }

    public boolean getHasAlarm() {return hasAlarm;}

    public void setHasPicture(Boolean hasPicture) {
        this.hasPicture = hasPicture;
    }
}
