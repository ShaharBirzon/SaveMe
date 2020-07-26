package com.example.saveme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.saveme.utils.FirebaseMediate;
import com.example.saveme.utils.MyPreferences;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Category> categoryList;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (MyPreferences.isFirstTime(getApplicationContext())) {
            categoryList = FirebaseMediate.getDefaultCategories();
            User user = new User(categoryList);
            FirebaseMediate.addUserToFirestoreDB(user);
        } else {
            categoryList = FirebaseMediate.getUserCategories();
        }

        recyclerView = findViewById(R.id.category_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

//      recyclerView.setAdapter(new CategoryAdapter(categoryList)); todo changed to the lines below ok?
        //set category adapter
        categoryAdapter = new CategoryAdapter(categoryList);
        recyclerView.setAdapter(categoryAdapter);

        // builder for the delete dialog of long click
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(this);

        // click listener - to go inside a category
        categoryAdapter.setCategoryClickListener(new CategoryClickListener() {
            @Override
            public void onCategoryClicked(int position) {
                Log.d("category clicked", "category was clicked");
                Category category = categoryList.get(position); //todo check how to get current category like this or from adapter?
                //todo move to the category activity
            }
        });

        // long click listener - if wants to delete a category
        categoryAdapter.setCategoryLongClickListener(new CategoryLongClickListener() {
            @Override
            public void onCategoryLongClicked(int position) {
                Log.d("category long clicked", "category was long clicked");

                builder1.setMessage("Are you sure you want do delete?");
                builder1.setCancelable(true);
                final Category category_to_delete = categoryList.get(position); //todo check how to get current category like this or from adapter?

                //if wants to delete for sure
                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        categoryAdapter.deleteCategory(category_to_delete);
                        FirebaseMediate.removeCategory(category_to_delete);
                        categoryAdapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                });

                builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });


    }
}
