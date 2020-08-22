package com.example.saveme.main;

import android.graphics.drawable.Drawable;

import com.example.saveme.category.Document;

import java.util.ArrayList;

public class Category {
    public String title;
    public String description;
    public Drawable image;
    public ArrayList<Document> docsList;

    public Category(){
    }

    public Category(String title, String desc){
        this.title = title;
        this.description = desc;
    }

    //getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public ArrayList<Document> getDocsList() {
        return docsList;
    }
    // todo maybe only to add or remove from list
    public void setDocsList(ArrayList<Document> docsList) {
        this.docsList = docsList;
    }
}
