package com.example.saveme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.saveme.utils.FirebaseMediate;
import com.example.saveme.utils.MyPreferences;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Category> categoryList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(MyPreferences.isFirstTime(getApplicationContext())){
            categoryList = FirebaseMediate.addUserToFirestoreDB();
        }
        else {
            categoryList = FirebaseMediate.getUserCategories();
        }

        recyclerView = findViewById(R.id.category_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        recyclerView.setAdapter(new CategoryAdapter(categoryList));
    }
}
