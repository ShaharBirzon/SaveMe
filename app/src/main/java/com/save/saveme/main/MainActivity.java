package com.save.saveme.main;

import androidx.annotation.NonNull;
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

import com.save.saveme.category.CategoryActivity;
import com.save.saveme.R;
import com.save.saveme.User;
import com.save.saveme.category.Document;
import com.save.saveme.utils.FirebaseMediate;
import com.save.saveme.utils.MyPreferences;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AddCategoryDialog.OnInputListener {

    public static final int CATEGORY_REQUEST_CODE = 333;
    private RecyclerView recyclerView;
    private ArrayList<Category> categories;
    private CategoryAdapter categoryAdapter;
    private static final String TAG = "MainActivity";
    private int lastCategoryPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // checks if needs to sign up or log in
        if (MyPreferences.isFirstTime(getApplicationContext())) {
            categories = FirebaseMediate.getDefaultCategories();
            User user = new User(categories);
            FirebaseMediate.addUserToFirestoreDB(user, new FireStoreCallBack() {

                @Override
                public void onCallBack(ArrayList<Category> categories) {
                    Log.d(TAG, "got to update categories onCallBack, with categories: " + categories);
                    MainActivity.this.categories.clear();
                    MainActivity.this.categories.addAll(categories);// Adapter has to have same categories ArrayList
                    categoryAdapter.notifyDataSetChanged();
                }
            });
        } else {
            Log.d(TAG, "got to else - not user's first time");

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
        if (username == null || username.equals("")) {
            welcomeString = "Welcome!";
        } else {
            welcomeString = "Welcome, " + username + "!";
        }
        TextView nameTxt = findViewById(R.id.tv_welcome_name);
        nameTxt.setText(welcomeString);
    }

    public interface FireStoreCallBack {
        void onCallBack(ArrayList<Category> categories);
    }


    private void initializeRecyclerView() {
        //set recycler view and category adapter
        recyclerView = findViewById(R.id.category_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        categoryAdapter = new CategoryAdapter(categories); //todo check stay same order
        recyclerView.setAdapter(categoryAdapter);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                ExtendedFloatingActionButton mainFab = findViewById(R.id.btn_add_category);
                if (dy > 0) {
                    mainFab.shrink();
                } else if (dy < 0) {
                    mainFab.extend();
                }
            }
        });

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
                lastCategoryPosition = position;
                Category category = categories.get(position); //todo check how to get current category like this or from adapter?
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                intent.putExtra("category_name", category.title);
                Gson gson = new Gson();
                String json = gson.toJson(categories.get(position).getDocsList());
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
                categories.get(lastCategoryPosition).docsList = updatedDocList;
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
        String[] categoriesTitles = this.getResources().getStringArray(R.array.categories);
        List<String> categoriesTitlesList = new ArrayList<>(Arrays.asList(categoriesTitles));
        for (Category category : categories) {
            if (categoriesTitlesList.contains(category.getTitle())) {
                categoriesTitlesList.remove(category.getTitle());
            }
        }
        categoriesTitles = categoriesTitlesList.toArray(new String[0]);
        AddCategoryDialog addCategoryDialog = new AddCategoryDialog(categoriesTitles);
        addCategoryDialog.show(getSupportFragmentManager(), "AddCategoryDialogFragment");
    }

    @Override
    public void sendInput(String title, int image) {
        Category category = new Category(title, image);
        addNewCategory(category);
    }

    private void addNewCategory(Category category) {
        categories.add(category);
        FirebaseMediate.addCategory(category);
        categoryAdapter.notifyItemInserted(categories.size() - 1);
    }

}
