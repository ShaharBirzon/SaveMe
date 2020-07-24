package com.example.saveme;

import java.util.ArrayList;
import java.util.List;

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
