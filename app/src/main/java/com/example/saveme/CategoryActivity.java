package com.example.saveme;

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
import android.widget.Toast;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

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

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if ("new_document".equals(intent.getStringExtra("call_from"))){
            addNewDocument(intent);
        }
    }

    private void addNewDocument(Intent intent) {
        Log.e(Tag, "adding new document " + intent.getStringExtra("document_title"));
//        Toast.(CategoryActivity.this, , Toast.LENGTH_LONG);
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
        documentList = new ArrayList<>();
        documentAdapter = new DocumentAdapter(documentList);
        recyclerView.setAdapter(documentAdapter);
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
                //todo go inside the document
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

    public void onClickAddDocumentButton(View view) {
        Intent intent = new Intent(CategoryActivity.this, DocumentActivity.class);
        startActivity(intent);
    }
}
