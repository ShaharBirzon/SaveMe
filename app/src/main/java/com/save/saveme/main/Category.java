package com.save.saveme.main;

import com.save.saveme.category.Document;

import java.util.ArrayList;

/**
 * a class that represents a category
 */
public class Category {
    public String title;
    public int image;
    public ArrayList<Document> docsList;

    public Category() {
    }

    public Category(String title) {
        this.title = title;
    }

    public Category(String title, int image) {
        this.title = title;
        this.image = image;
    }

    //getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
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
