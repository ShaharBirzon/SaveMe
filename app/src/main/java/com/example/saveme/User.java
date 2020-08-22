package com.example.saveme;

import com.example.saveme.main.Category;

import java.util.ArrayList;

public class User {
    ArrayList<Category> categories;

    public User() {
    }

    public User(ArrayList<Category> categories) {
        this.categories = categories;
    }
    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }
}
