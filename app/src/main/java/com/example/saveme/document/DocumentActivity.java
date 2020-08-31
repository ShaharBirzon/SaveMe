package com.example.saveme.document;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.saveme.R;
import com.example.saveme.category.CategoryActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class DocumentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private TextInputLayout documentTitleET;
    private TextInputLayout documentCommentET;
    private TextInputLayout documentExpirationDateET;
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
            position = intentCreatedMe.getIntExtra("position", -1);

        }

        documentExpirationDateET.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        documentExpirationDateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        documentExpirationDateET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    showDatePickerDialog();
                }
            }
        });
        documentExpirationDateET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

    }

    private void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    /**
     * when the save button is pressed
     *
     * @param view - the view
     */
    public void onClickSaveDocumentButton(View view) {

        Intent intentBack = new Intent(DocumentActivity.this, CategoryActivity.class);

        if (callReason.equals("edit_document")) {
            intentBack.putExtra("document_position", position);
            updateDocumentInFirebase();
        }
        if (callReason.equals("new_document")) {
            addDocumentToFirebase();
        }

        intentBack.putExtra("document_title", documentTitleET.getEditText().getText().toString());
        intentBack.putExtra("document_comment", documentCommentET.getEditText().getText().toString());
        intentBack.putExtra("document_expiration_date", documentExpirationDateET.getEditText().getText().toString());
        //todo add others
        setResult(RESULT_OK, intentBack);
        finish();
    }


    private void addDocumentToFirebase() {
        // todo maybe move to CategoryActivity
    }

    private void updateDocumentInFirebase() {
        // todo maybe move to CategoryActivity
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String date = day +"/" + month + "/" + year;
        documentExpirationDateET.getEditText().setText(date);
    }
}