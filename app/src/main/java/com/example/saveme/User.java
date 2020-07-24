package com.example.saveme;

import java.util.List;

public class User {
    List<Category> categories;

    public User() {
    }

    public User(List<Category> categories) {
        this.categories = categories;
    }
    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
