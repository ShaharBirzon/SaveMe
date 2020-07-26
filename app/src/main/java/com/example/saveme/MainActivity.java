package com.example.saveme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.saveme.utils.FirebaseMediate;
import com.example.saveme.utils.MyPreferences;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AddCategoryDialog.OnInputListener {

    private RecyclerView recyclerView;
    private ArrayList<Category> categoryList;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // checks if needs to sign up or log in
        if (MyPreferences.isFirstTime(getApplicationContext())) {
            categoryList = FirebaseMediate.getDefaultCategories();
            User user = new User(categoryList);
            FirebaseMediate.addUserToFirestoreDB(user);
        } else {
            categoryList = FirebaseMediate.getUserCategories();
        }

        // initializes the recycler view and the adapter
        initializeRecyclerView();

        // when a category is clicked
        initializeCategoryClickListener();

        // when a category is long clicked
        initializeCategoryLongClickListener();
    }

    private void initializeRecyclerView() {
        //set recycler view and category adapter
        recyclerView = findViewById(R.id.category_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        categoryAdapter = new CategoryAdapter(categoryList);
        recyclerView.setAdapter(categoryAdapter);
    }

    /*
    when a category is clicked
    */
    private void initializeCategoryClickListener() {
        // click listener - to go inside a category
        categoryAdapter.setCategoryClickListener(new CategoryClickListener() {
            @Override
            public void onCategoryClicked(int position) {
                Log.d("category clicked", "category was clicked");
                Category category = categoryList.get(position); //todo check how to get current category like this or from adapter?
                //todo move to the category activity
            }
        });
    }

    /*
    when a category is long clicked
     */
    private void initializeCategoryLongClickListener() {
        // builder for the delete dialog of long click
        final AlertDialog.Builder deleteAlertBuilder = new AlertDialog.Builder(this);

        // long click listener - if wants to delete a category
        categoryAdapter.setCategoryLongClickListener(new CategoryLongClickListener() {
            @Override
            public void onCategoryLongClicked(int position) {
                Log.d("category long clicked", "category was long clicked");

                deleteAlertBuilder.setMessage("Are you sure you want do delete?");
                deleteAlertBuilder.setCancelable(true);
                final Category category_to_delete = categoryList.get(position); //todo check how to get current category like this or from adapter?

                //if wants to delete for sure
                deleteAlertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        categoryAdapter.deleteCategory(category_to_delete);
                        FirebaseMediate.removeCategory(category_to_delete);
                        categoryAdapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                });

                deleteAlertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog DeleteAlertDialog = deleteAlertBuilder.create();
                DeleteAlertDialog.show();
            }
        });
    }

    public void onClickAddCategoryButton(View view) {
        AddCategoryDialog addCategoryDialog = new AddCategoryDialog();
        addCategoryDialog.show(getSupportFragmentManager(), "AddCategoryDialogFragment");
    }

    @Override
    public void sendInput(String title, String description) {
        Category category = new Category(title, description);
        addNewCategory(category);
    }

    private void addNewCategory(Category category) {
        categoryList.add(category);
        FirebaseMediate.addCategory(category);
        categoryAdapter.notifyItemInserted(categoryList.size()-1);
    }

}
