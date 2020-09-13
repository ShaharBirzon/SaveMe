package com.example.saveme.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.saveme.category.Document;
import com.example.saveme.main.Category;
import com.example.saveme.User;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class FirebaseMediate extends Application {
    private static ArrayList<Category> categories = new ArrayList<>();
    private static final String TAG = "FirebaseMediate";
    private static FirebaseFirestore db;
    private static DocumentReference userDocumentRef;
    private static CollectionReference usersCollectionRef;
    private static Context appContext;
    private static CollectionReference categoriesRef;
    private static DocumentSnapshot userDocumentSnapshot; //todo needed?

    @Override
    public void onCreate() {
        super.onCreate();
        initializeDataFromDB(getApplicationContext());
    }

    /**
     * This method initializes the firestore fields from data from the database.
     *
     * @param context
     */
    public static void initializeDataFromDB(Context context) {
        appContext = context;
        db = FirebaseFirestore.getInstance();
        usersCollectionRef = db.collection("users");//todo if use only once it's ok here

        String userDocumentPath = MyPreferences.getUserDocumentPathFromPreferences(appContext);
        if (userDocumentPath != null) {
            userDocumentRef = db.document(userDocumentPath);
            categoriesRef = userDocumentRef.collection("categories");
            initializeUserDocumentSnapshotFromDB();
            initializeUserCategoriesSnapshotFromDB();
        }
    }

    private static void initializeUserCategoriesSnapshotFromDB() {
        categoriesRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty()) {
                    Log.d(TAG, "onSuccess: LIST EMPTY");
                    return;
                } else {
                    // Convert the whole Query Snapshot to a list
                    // of objects directly! No need to fetch each
                    // document.
                    List<Category> categoriesList = documentSnapshots.toObjects(Category.class);

                    // Add all to your list
                    categories.clear();
                    categories.addAll(categoriesList);
                    Log.d(TAG, "onSuccess: " + categories);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error getting data!!!");
            }
        });

        Log.d(TAG, "successful userCategoriesSnapshot from db");

    }

    /**
     * This method initializes the userDocumentSnapshot.
     */
    public static void initializeUserDocumentSnapshotFromDB() {
        userDocumentRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                userDocumentSnapshot = value;
            }
        });
        Log.d(TAG, "successful setUserCollectionRef from db");
    }

    /**
     * This method is a getter  for user categories list.
     *
     * @return user categories list.
     */
    public static ArrayList<Category> getUserCategories() {
        Log.d(TAG, "got to getUserCategories");
        return categories;
    }

    /**
     * This method adds a category to user categories list.
     *
     * @param category category to add.
     */
    public static void addCategory(final Category category) {
        categoriesRef.document(category.getTitle()).set(category).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "category added successfully: " + category.getTitle());
                } else {
                    Log.w(TAG, "Error adding document");
                }
            }
        });
    }

    /**
     * This method removes a category from user categories list.
     *
     * @param category to be removed from user categories list.
     */
    public static void removeCategory(final Category category) {
        db.document(categoriesRef.getPath() + "/" + category.getTitle()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "remove category %s successfully" + category.title);
                } else {
                    Log.e(TAG, "error occurred while trying to remove category %s " + category.title);
                }
            }
        });
    }

    /**
     * This method adds a user to firestore database. called only once, when sign up.
     *
     * @param userToAdd the user to add.
     */
    public static void addUserToFirestoreDB(User userToAdd) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();//todo use

        // Add a new User document with a generated ID
        userDocumentRef = usersCollectionRef.document(firebaseUser.getUid());
        MyPreferences.saveUserDocumentPathToPreferences(appContext, userDocumentRef.getPath());
        categoriesRef = userDocumentRef.collection("categories");
        addCategories(userToAdd.getCategories());
    }

    private static void addCategories(ArrayList<Category> categories) {
        for (Category category : categories) {
            addCategory(category);
        }
    }

    /**
     * Add new document to firestore database.
     *
     * @param categoryName The category name the document is been added to.
     * @param newDocument  The new document.
     */
    public static void addNewDocument(String categoryName, Document newDocument) {
        db.document(categoriesRef.getPath() + "/" + categoryName).update("docsList", FieldValue.arrayUnion(newDocument));
    }

    /**
     * @param categoryName     The category name the document is been removed from.
     * @param documentToDelete The document to delete.
     */
    public static void removeDocument(String categoryName, Document documentToDelete) {
        db.document(categoriesRef.getPath() + "/" + categoryName).update("docsList", FieldValue.arrayRemove(documentToDelete));
    }


    /**
     * This method returns the default categories list.
     *
     * @return default categories list.
     */
    public static ArrayList<Category> getDefaultCategories() {
        ArrayList<Category> defaultCategories = new ArrayList<>();
        defaultCategories.add(new Category("Car", "car category"));
        defaultCategories.add(new Category("Bank", "bank category"));
        defaultCategories.add(new Category("Personal", "personal category"));
        return defaultCategories;
    }

}
