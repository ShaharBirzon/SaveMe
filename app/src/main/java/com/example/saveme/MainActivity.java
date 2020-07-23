package com.example.saveme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Category> categoryList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        categoryList = new ArrayList<>();
        recyclerView = findViewById(R.id.category_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        categoryList.add(new Category("Car", "car category"));
        categoryList.add(new Category("Bank", "bank category"));
        categoryList.add(new Category("Personal", "personal category"));

        recyclerView.setAdapter(new CategoryAdapter(categoryList));
    }
}
