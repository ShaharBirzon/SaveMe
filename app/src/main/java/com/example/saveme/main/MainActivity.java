package com.example.saveme.main;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.saveme.category.CategoryActivity;
import com.example.saveme.R;
import com.example.saveme.User;
import com.example.saveme.category.Document;
import com.example.saveme.utils.FirebaseMediate;
import com.example.saveme.utils.MyPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AddCategoryDialog.OnInputListener {

    public static final int CATEGORY_REQUEST_CODE = 333;
    private RecyclerView recyclerView;
    private Map<String, Category> categories;
    private CategoryAdapter categoryAdapter;
    private String lastCategoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // checks if needs to sign up or log in
        if (MyPreferences.isFirstTime(getApplicationContext())) {
            categories = FirebaseMediate.getDefaultCategories();
            User user = new User(categories);
            FirebaseMediate.addUserToFirestoreDB(user);
        } else {
            categories = FirebaseMediate.getUserCategories();
        }

        // initializes the recycler view and the adapter
        initializeRecyclerView();

        // when a category is clicked
        initializeCategoryClickListener();

        // when a category is long clicked
        initializeCategoryLongClickListener();

        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String welcomeString;
        if (username == null || username.equals("")){
            welcomeString = "Welcome!";
        }
        else{
            welcomeString = "Welcome, " + username + "!";
        }
        TextView nameTxt = findViewById(R.id.tv_welcome_name);
        nameTxt.setText(welcomeString);
    }

    private void initializeRecyclerView() {
        //set recycler view and category adapter
        recyclerView = findViewById(R.id.category_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        categoryAdapter = new CategoryAdapter(new ArrayList<Category>(categories.values())); //todo check stay same order
        recyclerView.setAdapter(categoryAdapter);
    }

    /*
    when a category is clicked
    */
    private void initializeCategoryClickListener() {
        // click listener - to go inside a category
        categoryAdapter.setCategoryClickListener(new CategoryClickListener() {
            @Override
            public void onCategoryClicked(String categoryName) {
                Log.d("category clicked", "category was clicked");
                lastCategoryName = categoryName;
                Category category = categories.get(categoryName); //todo check how to get current category like this or from adapter?
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                intent.putExtra("category_name", category.title);
                Gson gson = new Gson();
                String json = gson.toJson(categories.get(categoryName).getDocsList());
                intent.putExtra("docList", json);

                // todo add more
                startActivityForResult(intent, CATEGORY_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CATEGORY_REQUEST_CODE) {
            //add new document
            if (resultCode == RESULT_OK) {
                Gson gson = new Gson();
                String json = data.getStringExtra("docList");
                Type type = new TypeToken<ArrayList<Document>>() {
                }.getType();
                ArrayList<Document> updatedDocList;
                updatedDocList = gson.fromJson(json, type);
                if (updatedDocList == null) {
                    updatedDocList = new ArrayList<>();
                }
                categories.get(lastCategoryName).docsList = updatedDocList;
            }
        }
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
                final Category category_to_delete = categories.get(position); //todo check how to get current category like this or from adapter?

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
        categories.put(category.getTitle(), category);
        FirebaseMediate.addCategory(category);
        categoryAdapter.notifyItemInserted(categories.size() - 1);
    }
}
