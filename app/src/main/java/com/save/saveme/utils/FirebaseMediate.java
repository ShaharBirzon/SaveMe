package com.save.saveme.utils;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.save.saveme.R;
import com.save.saveme.category.Document;
import com.save.saveme.main.Category;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.save.saveme.main.MainActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * a class the handles connection and integration with the firebase firestore
 */
public class FirebaseMediate extends Application {
    private static ArrayList<Category> categories = new ArrayList<>();
    private static final String TAG = "FirebaseMediate";
    private static FirebaseFirestore db;
    private static DocumentReference userDocumentRef;
    private static CollectionReference usersCollectionRef;
    private static Context appContext;
    private static CollectionReference categoriesRef;
    private static StorageReference storageReference;
    private FirebaseStorage storage;


    @Override
    public void onCreate() {
        super.onCreate();
        initializeDataFromDB(getApplicationContext());

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    /**
     * This method initializes the firestore fields from data from the database.
     *
     * @param context - the context
     */
    public void initializeDataFromDB(Context context) {
        Log.d(TAG, "started initializeDataFromDB");
        appContext = context;
        db = FirebaseFirestore.getInstance();
        usersCollectionRef = db.collection("users");

        String userDocumentPath = MyPreferences.getUserDocumentPathFromPreferences(appContext);
        if (userDocumentPath != null) {
            Log.d(TAG, "started initialize user data from DB");
            userDocumentRef = db.document(userDocumentPath);
            categoriesRef = userDocumentRef.collection("categories");
            initializeUserCategoriesSnapshotFromDB(new MainActivity.FireStoreCallBack() {
                @Override
                public void onCallBack(ArrayList<Category> categories) {
                    FirebaseMediate.categories = categories;
                }
            });
        }
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
     * @param fireStoreCallBack - callback
     */
    public static void addUserToFirestoreDB(final MainActivity.FireStoreCallBack fireStoreCallBack) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Add a new User document with a generated ID
        userDocumentRef = usersCollectionRef.document(firebaseUser.getUid());
        MyPreferences.saveUserDocumentPathToPreferences(appContext, userDocumentRef.getPath());
        categoriesRef = userDocumentRef.collection("categories");
        initializeUserCategoriesSnapshotFromDB(fireStoreCallBack);
    }

    /**
     * initalize the user's categories from DB
     *
     * @param fireStoreCallBack - callback
     */
    public static void initializeUserCategoriesSnapshotFromDB(final MainActivity.FireStoreCallBack fireStoreCallBack) {
        categoriesRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty()) {
                    Log.d(TAG, "onSuccess initializeUserCategoriesSnapshotFromDB: LIST EMPTY so add default categories list");
                    ArrayList<Category> defaultCategories = getDefaultCategories();
                    categories.addAll(defaultCategories);
                    addCategories(defaultCategories);
                } else {
                    List<Category> categoriesList = documentSnapshots.toObjects(Category.class);

                    // Add all to your list
                    categories.clear();
                    categories.addAll(categoriesList);
                    Log.d(TAG, "onSuccess initializeUserCategoriesSnapshotFromDB: " + categories);
                }
                fireStoreCallBack.onCallBack(categories);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error getting categories data from Firebase!!!");
            }
        });
    }

    /**
     * add categories to user's categories list.
     *
     * @param categories the categories to add to user's categories list.
     */
    private static void addCategories(ArrayList<Category> categories) {
        for (Category category : categories) {
            addCategory(category);
        }
    }

    /**
     * Add new document to firestore database.
     *
     * @param categoryName - The category name the document is been added to.
     * @param newDocument  - The new document.
     */
    public static void addNewDocument(String categoryName, Document newDocument) {
        db.document(categoriesRef.getPath() + "/" + categoryName).update("docsList", FieldValue.arrayUnion(newDocument));
    }

    /**
     * @param categoryName     - The category name the document is been removed from.
     * @param documentToDelete - The document to delete.
     */
    public static void removeDocument(final String categoryName, final Document documentToDelete) {

        db.document(categoriesRef.getPath() + "/" + categoryName).update("docsList", FieldValue.arrayRemove(documentToDelete)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "successfully deleted document: " + documentToDelete.getTitle() + " from category " + categoryName);
                } else {
                    Log.e(TAG, "Error while deleting document " + documentToDelete.getTitle() + " from category" + categoryName);
                }
            }
        });
    }

    /**
     * This method returns the default categories list.
     *
     * @return default categories list.
     */
    public static ArrayList<Category> getDefaultCategories() {
        ArrayList<Category> defaultCategories = new ArrayList<>();
        defaultCategories.add(new Category("Car", 10));
        defaultCategories.add(new Category("Bank", 0));
        defaultCategories.add(new Category("Personal", 3));
        return defaultCategories;
    }


    /**
     * This method uploads an image to firebase storage.
     *
     * @param selectedImage - the uri of the image to upload.
     * @param activity      - the activity calling this method.
     * @param context       - the activity context.
     * @param categoryTitle - category title
     * @param imageType     - the image type (profilePic/apartmentPic).
     */
    public static void uploadImageToStorage(Uri selectedImage, final Activity activity, Context context, String categoryTitle, String documentTitle, String imageType) {
        if (selectedImage != null) {
            final ProgressDialog progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle("Uploading the document to the cloud");
            progressDialog.show();
            progressDialog.setCancelable(false);
            StorageReference ref = storageReference.child("Files").
                    child(MyPreferences.getUserDocumentPathFromPreferences(context)).child(categoryTitle).child(documentTitle).child(imageType);
            ref.putFile(selectedImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(activity, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(activity, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    /**
     * get image from firebase
     *
     * @param context       - context
     * @param categoryTitle - category title
     * @param documentTitle - document title
     * @param imageType     - image type
     * @return the image
     */
    public Uri getImageFromFirebaseStorage(Context context, String categoryTitle, String documentTitle, String imageType) {
        final Uri[] imageUri = {null};
        StorageReference ref = storageReference.child("Files").
                child(MyPreferences.getUserDocumentPathFromPreferences(context)).child(categoryTitle).child(documentTitle).child(imageType);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                imageUri[0] = uri;
                Log.d(TAG, "got image from firebase storage successfully");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "Error getting image from firebase storage");
            }
        });
        return imageUri[0];
    }

    /**
     * upload file to DB
     *
     * @param context       - context
     * @param categoryTitle - category title
     * @param documentTitle - document title
     * @param fileUri       - the uri of the file
     */
    public static void uploadDocumentFileToDB(Context context, String categoryTitle, String documentTitle, final Uri fileUri) {
        StorageReference ref = storageReference.child("Files").
                child(MyPreferences.getUserDocumentPathFromPreferences(context)).child(categoryTitle).child(documentTitle).child("file");

        // Register observers to listen for when the download is done or if it fails
        ref.putFile(fileUri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e(TAG, "unsuccessful file upload");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "successful file upload");
            }
        });
    }

    /**
     * @param bitmap        - the bitmap of image
     * @param context       - context
     * @param categoryTitle - category title
     * @param documentTitle - document title
     * @param imageType     - image type
     */
    public static void uploadImageToFirebaseStorageDB(Bitmap bitmap, Context context, String categoryTitle, String documentTitle, String imageType) {
        // Get the data from an ImageView as bytes
        StorageReference ref = storageReference.child("Files").
                child(MyPreferences.getUserDocumentPathFromPreferences(context)).child(categoryTitle).child(documentTitle).child("image");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "filed to upload the document image to the storage DB");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "successfully uploaded the document image to the storage DB");
            }
        });
    }

}
