package com.example.saveme.document;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.saveme.R;
import com.example.saveme.category.CategoryActivity;

public class DocumentActivity extends AppCompatActivity {

    private EditText documentTitleET;
    private EditText documentCommentET;
    private EditText documentExpirationDateET;
    private String callReason;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);
        documentTitleET = findViewById(R.id.et_document_title);
        documentCommentET = findViewById(R.id.et_comment);
        documentExpirationDateET = findViewById(R.id.et_expiration_date);
        // todo add other

        Intent intentCreatedMe = getIntent();
        callReason = intentCreatedMe.getStringExtra("call_reason");
        if (callReason.equals("edit_document")) {
            String title = intentCreatedMe.getStringExtra("document_title");
            documentTitleET.setText(title);
            String comment = intentCreatedMe.getStringExtra("document_comment");
            documentCommentET.setText(comment);
            String expirationDate = intentCreatedMe.getStringExtra("document_expiration_date");
            documentExpirationDateET.setText(expirationDate);
            // todo for others
            position = intentCreatedMe.getIntExtra("position", -1);

        }
    }

    public void onClickSaveDocumentButton(View view) {
        Intent intentBack = new Intent(DocumentActivity.this, CategoryActivity.class);

        // todo check if call_from is needed
        if (callReason.equals("edit_document")) {
            intentBack.putExtra("call_from", "edit_document");
            intentBack.putExtra("document_position", position);
        }
        if (callReason.equals("new_document")) {
            addDocumentToFirebase();
            intentBack.putExtra("call_from", "new_document");
        }

        intentBack.putExtra("document_title", documentTitleET.getText().toString());
        intentBack.putExtra("document_comment", documentCommentET.getText().toString());
        intentBack.putExtra("document_expiration_date", documentExpirationDateET.getText().toString());
        //todo add others
        setResult(RESULT_OK, intentBack);
        finish();
    }

    private void addDocumentToFirebase() {
        // todo maybe move to CategoryActivity
    }
}