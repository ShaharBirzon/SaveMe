package com.example.saveme;

import com.example.saveme.main.Category;


import java.util.Map;

public class User {
    Map<String, Category> categories;

    public User() {
    }

    public User(Map<String, Category> categories) {
        this.categories = categories;
    }
    public Map<String, Category> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, Category> categories) {
        this.categories = categories;
    }
}
