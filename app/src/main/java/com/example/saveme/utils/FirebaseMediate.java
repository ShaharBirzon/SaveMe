package com.example.saveme.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.saveme.Category;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseMediate {
    private static final String TAG = "FirebaseMediate";
    private static FirebaseFirestore db;
    private static DocumentSnapshot userDocumentSnapshot;
    private static CollectionReference usersCollectionRef;
    private static Context appContext;


    public static void initializeDataFromDB(Context context){
        appContext = context;
        db = FirebaseFirestore.getInstance();
        initializeUserCollectionRefFromDB(MyPreferences.getUserDocumentPathFromPreferences(appContext));
        usersCollectionRef = db.collection("users");//todo maybe change place to once

    }

    public static void initializeUserCollectionRefFromDB(String userDocumentReference){
        db.document(userDocumentReference).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                userDocumentSnapshot = value;
            }
        });
        Log.d(TAG, "successful setUserCollectionRef from db");
    }

    public static ArrayList<Category> getUserCategories(){
        ArrayList<Category> categoryArrayList = new ArrayList<Category>();
        categoryArrayList = (ArrayList<Category>) userDocumentSnapshot.get("categories");
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (DocumentSnapshot document : task.getResult()) {
//                                categoryArrayList.add(document.toObject(Category.class));
//                            }
//                            Log.d(TAG, "successful getUserCategories from db");
//
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });
        return categoryArrayList;
    }

    public static ArrayList<Category> addUserToFirestoreDB() {
        // Create a new user with a first and last name
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ArrayList<Category> categories = new ArrayList<>();
        categories = getDefaultCategories();
        Map<String, Object> user = new HashMap<>();
        user.put("categories", categories);

        // Add a new document with a generated ID
        usersCollectionRef.add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        FirebaseMediate.initializeUserCollectionRefFromDB(documentReference.getPath());
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
        return categories;
    }

    private static ArrayList<Category> getDefaultCategories() {
        ArrayList<Category> defaultCategories = new ArrayList<>();
        defaultCategories.add(new Category("Car", "car category"));
        defaultCategories.add(new Category("Bank", "bank category"));
        defaultCategories.add(new Category("Personal", "personal category"));
        return defaultCategories;
    }

}
