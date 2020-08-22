package com.example.saveme.category;

import java.util.Date;

public class Document {
    private String title;
    private String comment;
    private String expirationDate;

    // todo add attr for images
    public Document() {
    }

    public Document(String title,String comment, String expirationDate) {
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
}
