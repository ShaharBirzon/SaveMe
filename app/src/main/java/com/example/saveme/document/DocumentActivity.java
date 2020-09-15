package com.example.saveme.document;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.saveme.main.AddCategoryDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;

import android.widget.TimePicker;

import android.widget.Toast;

import com.example.saveme.R;
import com.example.saveme.category.CategoryActivity;
import com.example.saveme.category.Document;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.Calendar;

public class DocumentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "DocumentActivity";
    private TextInputLayout documentTitleET;
    private TextInputLayout documentCommentET;
    private TextInputLayout documentExpirationDateET;
    private TextInputLayout reminderTimeET1;
    private TextInputLayout reminderTimeET2;
    private String callReason;
    private int position;
    private Spinner reminderSpinner1;
    private Spinner reminderSpinner2;
    SwitchMaterial reminderSwitch;
    private Document curDocument = new Document();
    private Button addPhotoBtn;
    private Uri selectedImage;
    ImageView documentImageView;
    private boolean changedPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);
        documentTitleET = findViewById(R.id.et_document_title);
        documentCommentET = findViewById(R.id.et_comment);
        reminderTimeET1 = findViewById(R.id.et_time1);
        reminderTimeET2 = findViewById(R.id.et_time2);
        reminderSwitch = findViewById(R.id.add_alarm);
        reminderSpinner1 = findViewById(R.id.spinner_times1);
        reminderSpinner2 = findViewById(R.id.spinner_times2);
        setReminderTime();
        documentExpirationDateET = findViewById(R.id.et_expiration_date);
        // todo add other

        Intent intentCreatedMe = getIntent();
        callReason = intentCreatedMe.getStringExtra("call_reason");
        if (callReason.equals("edit_document")) {
            curDocument.setTitle(intentCreatedMe.getStringExtra("document_title"));
            curDocument.setComment(intentCreatedMe.getStringExtra("document_comment"));
            curDocument.setExpirationDate(intentCreatedMe.getStringExtra("document_expiration_date"));
            position = intentCreatedMe.getIntExtra("position", -1);
            initializeActivityFieldsWithDocumentDataFromDB();
        }

        documentExpirationDateET.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        reminderSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                View v1 = findViewById(R.id.et_time1);
                View v2 = findViewById(R.id.btn_add_another_alarm);
                View v3 = findViewById(R.id.tv_reminder1);
                View v4 = findViewById(R.id.tv_add_to_calendar1);
                View v5 = findViewById(R.id.checkbox_calendar1);
                View v6 = findViewById(R.id.spinner_times1);
                View v7 = findViewById(R.id.et_time2);
                View v8 = findViewById(R.id.tv_reminder2);
                View v9 = findViewById(R.id.tv_add_to_calendar2);
                View v10 = findViewById(R.id.checkbox_calendar2);
                View v11 = findViewById(R.id.spinner_times2);


                if (b) {
                    v1.setVisibility(View.VISIBLE);
                    v2.setVisibility(View.VISIBLE);
                    v3.setVisibility(View.VISIBLE);
                    v4.setVisibility(View.VISIBLE);
                    v5.setVisibility(View.VISIBLE);
                    v6.setVisibility(View.VISIBLE);
                } else {
                    v1.setVisibility(View.GONE);
                    v2.setVisibility(View.GONE);
                    v3.setVisibility(View.GONE);
                    v4.setVisibility(View.GONE);
                    v5.setVisibility(View.GONE);
                    v6.setVisibility(View.GONE);
                    v7.setVisibility(View.GONE);
                    v8.setVisibility(View.GONE);
                    v9.setVisibility(View.GONE);
                    v10.setVisibility(View.GONE);
                    v11.setVisibility(View.GONE);
                }
            }
        });

        reminderTimeET1.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog1();
            }
        });

        reminderTimeET2.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog2();
            }
        });

        changedPhoto = false;
        addPhotoBtn = findViewById(R.id.btn_add_doc_photo);
        documentImageView = findViewById(R.id.iv_doc);
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhotoOnClickAS();
            }
        });

    }

    /**
     * This method initializes Activity Fields With Document Data From DB
     */
    private void initializeActivityFieldsWithDocumentDataFromDB() {
        Log.d(TAG, "got to initializeActivityFieldsWithDocumentDataFromDB");
        documentTitleET.getEditText().setText(curDocument.getTitle());
        documentCommentET.getEditText().setText(curDocument.getComment());
        documentExpirationDateET.getEditText().setText(curDocument.getExpirationDate());
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    private void showTimePickerDialog1() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                String time = hour + ":" + min;
                reminderTimeET1.getEditText().setText(time);

            }
        },
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE), true);
        timePickerDialog.show();

    }

    private void showTimePickerDialog2() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                String time = hour + ":" + min;
                reminderTimeET2.getEditText().setText(time);

            }
        },
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE), true);
        timePickerDialog.show();

    }

    /**
     * when the save button is pressed
     *
     * @param view - the view
     */
    public void onClickSaveDocumentButton(View view) {
        if (!isInputValid()) {
            Toast.makeText(getApplicationContext(), "invalid input data", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intentBack = new Intent(DocumentActivity.this, CategoryActivity.class);

        if (callReason.equals("edit_document")) {
            intentBack.putExtra("document_position", position);
        }

        intentBack.putExtra("has_photo", changedPhoto);

        intentBack.putExtra("document_title", documentTitleET.getEditText().getText().toString());
        intentBack.putExtra("document_comment", documentCommentET.getEditText().getText().toString());
        intentBack.putExtra("document_expiration_date", documentExpirationDateET.getEditText().getText().toString());
        //todo add others
        setResult(RESULT_OK, intentBack);
        finish();
    }

    /**
     * This method verifies user input is valid.
     *
     * @return true if user input is valid, false otherwise.
     */
    private boolean isInputValid() {
        if (documentTitleET.getEditText().getText().length() == 0) {
            return false;
        }
        return true;
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        month = month + 1;
        String date = day + "/" + month + "/" + year;
        documentExpirationDateET.getEditText().setText(date);
    }

    private void setReminderTime() {
        final ArrayAdapter<String> titlesAdapter = new ArrayAdapter<>(DocumentActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.times));
        titlesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reminderSpinner1.setAdapter(titlesAdapter);
        reminderSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                reminderSpinner1.setSelection(position);
                String title = titlesAdapter.getItem(position);
                //todo implement
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        reminderSpinner2.setAdapter(titlesAdapter);
        reminderSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                reminderSpinner1.setSelection(position);
                String title = titlesAdapter.getItem(position);
                //todo implement
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    public void onAddAlarmButtonClick(View view) {
        View v7 = findViewById(R.id.et_time2);
        View v8 = findViewById(R.id.tv_reminder2);
        View v9 = findViewById(R.id.tv_add_to_calendar2);
        View v10 = findViewById(R.id.checkbox_calendar2);
        View v11 = findViewById(R.id.spinner_times2);
        v7.setVisibility(View.VISIBLE);
        v8.setVisibility(View.VISIBLE);
        v9.setVisibility(View.VISIBLE);
        v10.setVisibility(View.VISIBLE);
        v11.setVisibility(View.VISIBLE);
        view.setVisibility(View.GONE);
    }

    //todo check!!
    private void addToCalendar(String reminderTime, String docTitle) {
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", reminderTime);
        intent.putExtra("allDay", false);
        intent.putExtra("rrule", "FREQ=YEARLY");
        //intent.putExtra("endTime", reminderTime+60*60*1000);
        intent.putExtra("title", "Reminder! your document: " + docTitle + "is expired");
        startActivity(intent);


    }

    /**
     * this method opens the gallery to choose image. Activates when user clicks on an upload photo
     * button.
     */
    public void uploadPhotoOnClickAS() {
        CropImage.activity()
                .setCropMenuCropButtonTitle("finish cropping")
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(this);
    }

    /**
     * updates the activity view after choosing an image from gallery
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                selectedImage = result.getUri();
                saveNewProfileImage();
            }
    }

    private void saveNewProfileImage() {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver()
                    , selectedImage);
            Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.1), (int) (bitmap.getHeight() * 0.1), true);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.1), (int) (bitmap.getHeight() * 0.1), true);
            documentImageView.setImageBitmap(previewBitmap);
            curDocument.setBitmap(resizedBitmap);
            curDocument.setHasPicture(true);
            changedPhoto = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}