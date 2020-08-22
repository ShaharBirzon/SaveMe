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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);
    }

    public void onClickSaveDocumentButton(View view) {
        Intent intent = new Intent( DocumentActivity.this, CategoryActivity.class);
        intent.putExtra("call_from", "new_document");
        intent.putExtra("document_title","neeeee");
        startActivity(intent);
        finish();
    }
}