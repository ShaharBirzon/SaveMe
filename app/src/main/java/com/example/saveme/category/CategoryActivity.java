package com.example.saveme.category;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.saveme.document.DocumentActivity;
import com.example.saveme.R;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    public static final int NEW_DOCUMENT = 111;
    public static final int EDIT_DOCUMENT = 222;
    public static final int DEFAULT_VALUE = -1;
    private RecyclerView recyclerView;
    private ArrayList<Document> documentList;
    private DocumentAdapter documentAdapter;
    private static final String Tag = "CategoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // initializes the recycler view and the adapter
        initializeRecyclerView();

        // when a document is clicked
        initializeDocumentClickListener();

        // when a document is long clicked
        initializeDocumentLongClickListener();

        Intent mainIntent = getIntent();
        String title = mainIntent.getStringExtra("category_name");
        TextView titleTxt = findViewById(R.id.tv_category_title);
        titleTxt.setText(title);
    }

    //todo maybe not needed
//    @Override
//    public void onResume() {
//        super.onResume();
//        Intent intent = getIntent();
//        if ("new_document".equals(intent.getStringExtra("call_from"))) {
//            addNewDocument(intent);
//        }
//    }


    /*
    the function initializes the recycler view and the adapter
     */
    private void initializeRecyclerView() {
        //set recycler view and document adapter
        recyclerView = findViewById(R.id.document_recycler);
        // todo check which layout
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        documentList = new ArrayList<>();
        documentAdapter = new DocumentAdapter(documentList);
        recyclerView.setAdapter(documentAdapter);
    }

    /**
     * when the add document button is clicked
     * @param view - view
     */
    public void onClickAddDocumentButton(View view) {
        Intent intent = new Intent(CategoryActivity.this, DocumentActivity.class);
        intent.putExtra("call_reason", "new_document");
        startActivityForResult(intent, NEW_DOCUMENT);
    }


    /**
     *
     * @param requestCode - a new document or an edited documetn
     * @param resultCode
     * @param data - the info from document activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_DOCUMENT) {
            //add new document
            if (resultCode == RESULT_OK) {
                String title = data.getStringExtra("document_title");
                String comment = data.getStringExtra("document_comment");
                String expirationDate = data.getStringExtra("document_expiration_date");

                Log.e(Tag, "adding new document " + title);
                Document newDocument = new Document(title,comment, expirationDate); //todo change
                documentList.add(newDocument);
                documentAdapter.notifyItemInserted(documentList.size()-1);
            }
        }
        if (requestCode == EDIT_DOCUMENT) {
            //edit existing document
            String title = data.getStringExtra("document_title");
            String comment = data.getStringExtra("document_comment");
            String expirationDate = data.getStringExtra("document_expiration_date");
            int position = data.getIntExtra("document_position", DEFAULT_VALUE);

            Log.e(Tag, "editing document " + title);
            Document document = documentList.get(position); //todo check how to get current document like this or from adapter?
            document.setTitle(title);
            document.setComment(comment);
            document.setExpirationDate(expirationDate);
            documentAdapter.notifyDataSetChanged();
        }
    }

    //todo maybe delete
//    private void addNewDocument(Intent intent) {
//        String title = intent.getStringExtra("document_title");
//        String date = intent.getStringExtra("document_date");
//        Log.e(Tag, "adding new document " + intent.getStringExtra("document_title"));
//        Document newDocument = new Document(title, date);
//        documentList.add(newDocument);
////        Toast.(CategoryActivity.this, , Toast.LENGTH_LONG);
//    }

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
                intent.putExtra("document_comment", document.getComment());
                intent.putExtra("document_expiration_date", document.getExpirationDate());
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
//                      FirebaseMediate.removeCategory(document_to_delete); //todo remove from firebase
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
}