package com.example.saveme;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class Category {
    public String title;
    public String description;
    public Drawable image;
    public ArrayList<Document> docsList;

    public Category(String title, String desc){
        this.title = title;
        this.description = desc;
    }
}
