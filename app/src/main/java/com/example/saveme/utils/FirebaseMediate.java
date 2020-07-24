package com.example.saveme.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.saveme.Category;
import com.example.saveme.User;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;


public class FirebaseMediate {
    private static ArrayList<Category> categories = new ArrayList<Category>();
    private static final String TAG = "FirebaseMediate";
    private static FirebaseFirestore db;
    private static DocumentReference userDocumentRef;
    private static CollectionReference usersCollectionRef;
    private static Context appContext;
    private static CollectionReference categoriesRef;
    private static DocumentSnapshot userDocumentSnapshot;

    /**
     * This method initializes the firestore fields from data from the database.
     * @param context
     */
    public static void initializeDataFromDB(Context context) {
        appContext = context;
        db = FirebaseFirestore.getInstance();
        usersCollectionRef = db.collection("users");//todo if use only once it's ok here

        String userDocumentPath = MyPreferences.getUserDocumentPathFromPreferences(appContext);
        if (userDocumentPath != null) {
            userDocumentRef = db.document(userDocumentPath);
            initializeUserDocumentSnapshotFromDB();
            categoriesRef = userDocumentRef.collection("categories");
        }
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
     * @return user categories list.
     */
    public static ArrayList<Category> getUserCategories() {
        Log.d(TAG, "got to getUserCategories");
        User user = userDocumentSnapshot.toObject(User.class);
        categories = user.getCategories();
        return categories;
    }

    /**
     * This method adds a category
     * @param category
     */
    public static void addCategory(Category category){
        userDocumentRef.update("categories", FieldValue.arrayUnion(category));
    }

    /**
     * This method removes a category from user categories list.
     * @param category to be removed from user categories list.
     */
    public static void removeCategory(Category category){
        userDocumentRef.update("categories", FieldValue.arrayRemove(category));
    }

    /**
     * This method adds a user to firestore database. called only once, when sign up.
     * @param userToAdd the user to add.
     */
    public static void addUserToFirestoreDB(User userToAdd) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();//todo use

        // Add a new User document with a generated ID
        usersCollectionRef.add(userToAdd)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        userDocumentRef = documentReference;
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        MyPreferences.saveUserDocumentPathToPreferences(appContext, documentReference.getPath());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    /**
     * This method returns the default categories list.
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
