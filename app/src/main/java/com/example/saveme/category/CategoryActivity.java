package com.example.saveme.category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.saveme.document.DocumentActivity;
import com.example.saveme.R;
import com.example.saveme.main.MainActivity;
import com.example.saveme.utils.FirebaseMediate;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    public static final int NEW_DOCUMENT = 111;
    public static final int EDIT_DOCUMENT = 222;
    public static final int DEFAULT_VALUE = -1;
    private RecyclerView recyclerView;
    private ArrayList<Document> documentList;
    private DocumentAdapter documentAdapter;
    private static final String TAG = "CategoryActivity";
    private String categoryTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Intent mainIntent = getIntent();
        Gson gson = new Gson();
        String json = mainIntent.getStringExtra("docList");
        Type type = new TypeToken<ArrayList<Document>>() {
        }.getType();
        documentList = gson.fromJson(json, type);
        if (documentList == null) {
            documentList = new ArrayList<>();
        }

        // initializes the recycler view and the adapter
        initializeRecyclerView();

        // when a document is clicked
        initializeDocumentClickListener();

        // when a document is long clicked
        initializeDocumentLongClickListener();

        categoryTitle = mainIntent.getStringExtra("category_name");
        TextView titleTxt = findViewById(R.id.tv_category_title);
        titleTxt.setText(categoryTitle);
    }


    /*
    the function initializes the recycler view and the adapter
     */
    private void initializeRecyclerView() {
        //set recycler view and document adapter
        recyclerView = findViewById(R.id.document_recycler);
        // todo check which layout
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        documentAdapter = new DocumentAdapter(documentList);
        recyclerView.setAdapter(documentAdapter);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                ExtendedFloatingActionButton categoryFab = findViewById(R.id.btn_add_doc);
                if (dy > 0) {
                    categoryFab.shrink();
                } else if (dy < 0) {
                    categoryFab.extend();
                }
            }
        });
    }

    /**
     * when the add document button is clicked
     *
     * @param view - view
     */
    public void onClickAddDocumentButton(View view) {
        Intent intent = new Intent(CategoryActivity.this, DocumentActivity.class);
        intent.putExtra("call_reason", "new_document");
        startActivityForResult(intent, NEW_DOCUMENT);
    }


    /**
     * @param requestCode - a new document or an edited documetn
     * @param resultCode
     * @param data        - the info from document activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_DOCUMENT) {
            //add new document
            if (resultCode == RESULT_OK) {
                addDocument(data);
            }
        }
        if (requestCode == EDIT_DOCUMENT) {
            //edit existing document
            if (resultCode == RESULT_OK) {
                updateDocument(data);
            }
        }
    }

    /*
    the method adds a document to a category
     */
    private void addDocument(Intent data) {
        String title = data.getStringExtra("document_title");
        String comment = data.getStringExtra("document_comment");
        String expirationDate = data.getStringExtra("document_expiration_date");
        boolean hasAlarm = data.getBooleanExtra("is_alarm", false);
        boolean addedPhoto = data.getBooleanExtra("has_photo", false);
        if (addedPhoto) {
            String imageString = data.getStringExtra("imageUri");
            Uri imageUri = Uri.parse(imageString);
            FirebaseMediate.uploadPhotoToStorage(imageUri, this, getApplicationContext(), categoryTitle, title, "image");
        }
        String fileStr = data.getStringExtra("file_uri");
        if (fileStr !=null){
            Uri fileUri = Uri.parse(fileStr);
            FirebaseMediate.uploadDocumentFileToDB( getApplicationContext(), categoryTitle, title, fileUri);
        }
        Log.e(TAG, "adding new document " + title);
        Document newDocument = new Document(title, comment, expirationDate, addedPhoto, hasAlarm); //todo change
        documentList.add(newDocument);
        FirebaseMediate.addNewDocument(categoryTitle, newDocument);
        documentAdapter.notifyItemInserted(documentList.size() - 1);
    }

    /*
    the method updates the document fields (also in the fireStore)
     */
    private void updateDocument(Intent data) {
        String title = data.getStringExtra("document_title");
        String comment = data.getStringExtra("document_comment");
        String expirationDate = data.getStringExtra("document_expiration_date");
        int position = data.getIntExtra("document_position", DEFAULT_VALUE);
        boolean addedPhoto = data.getBooleanExtra("has_photo", false);
        if (addedPhoto) {
            String imageString = data.getStringExtra("imageUri");
            Uri imageUri = Uri.parse(imageString);
            FirebaseMediate.uploadPhotoToStorage(imageUri, this, getApplicationContext(), categoryTitle, title, "image");
        }
        String fileStr = data.getStringExtra("file_uri");
        if (fileStr !=null){
            Uri fileUri = Uri.parse(fileStr);
            FirebaseMediate.uploadDocumentFileToDB( getApplicationContext(), categoryTitle, title, fileUri);
        }
        Log.e(TAG, "editing document " + title);
        Document document = documentList.get(position);
        FirebaseMediate.removeDocument(categoryTitle, document);
        if (!title.equals(document.getTitle())) {
            document.setTitle(title);
        }
        if (!comment.equals(document.getComment())) {
            document.setComment(comment);
        }
        if (!expirationDate.equals(document.getExpirationDate())) {
            document.setExpirationDate(expirationDate);
        }
        // todo add other fields
        FirebaseMediate.addNewDocument(categoryTitle, document);
        documentAdapter.notifyDataSetChanged();
    }


    /*
    when a document is clicked
    */
    private void initializeDocumentClickListener() {
        // click listener - to go inside a document
        documentAdapter.setDocumentClickListener(new DocumentClickListener() {
            @Override
            public void onDocumentClicked(int position) {
                Log.d("document clicked", "document was clicked");
                Document document = documentList.get(position); //todo check how to get current document like this or from adapter?
                Intent intent = new Intent(CategoryActivity.this, DocumentActivity.class);
                intent.putExtra("call_reason", "edit_document");
                intent.putExtra("position", position);
                intent.putExtra("document_title", document.getTitle());
                intent.putExtra("category_title", categoryTitle);
                intent.putExtra("document_comment", document.getComment());
                intent.putExtra("document_expiration_date", document.getExpirationDate());
                intent.putExtra("has_photo", document.getHasPicture());
                // todo add more into intent
                startActivityForResult(intent, EDIT_DOCUMENT);
            }
        });
    }

    /*
    when a document is long clicked - can delete it
    */
    private void initializeDocumentLongClickListener() {
        // builder for the delete dialog of long click
        final AlertDialog.Builder deleteAlertBuilder = new AlertDialog.Builder(this);

        // long click listener - if wants to delete a document
        documentAdapter.setDocumentLongClickListener(new DocumentLongClickListener() {
            @Override
            public void onDocumentLongClicked(int position) {
                Log.d("document long clicked", "document was long clicked");

                deleteAlertBuilder.setMessage("Are you sure you want do delete?");
                deleteAlertBuilder.setCancelable(true);
                final Document document_to_delete = documentList.get(position); //todo check how to get current document like this or from adapter?

                //if wants to delete for sure
                deleteAlertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        documentAdapter.deleteDocument(document_to_delete);
                        FirebaseMediate.removeDocument(categoryTitle, document_to_delete); //todo remove from firebase
                        documentAdapter.notifyDataSetChanged();
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

    @Override
    public void onBackPressed() {
        Gson gson = new Gson();
        String json = gson.toJson(documentList);
        Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
        intent.putExtra("docList", json);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onBackClick(View view) {
        Gson gson = new Gson();
        String json = gson.toJson(documentList);
        Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
        intent.putExtra("docList", json);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onClickChooseIcon(View view) {

    }
}
