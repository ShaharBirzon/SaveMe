package com.example.saveme.document;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.saveme.utils.MyPreferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.example.saveme.utils.AlarmReceiver;
import com.google.android.material.switchmaterial.SwitchMaterial;

import android.widget.TimePicker;

import android.widget.Toast;

import com.example.saveme.R;
import com.example.saveme.category.CategoryActivity;
import com.example.saveme.category.Document;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class DocumentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "DocumentActivity";
    private static final long ONE_MEGABYTE = 1024 * 1024;
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
    private boolean isAlarm = false;
    CheckBox check1;
    CheckBox check2;
    TimePicker alarmTimePicker1;
    TimePicker alarmTimePicker2;
    DatePicker datePicker;

    private boolean isDocumentTitleValid = false;

    private static StorageReference storageReference;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        initializeActivityFields();
        setReminderTime();
        // todo add other

        Intent intentCreatedMe = getIntent();
        callReason = intentCreatedMe.getStringExtra("call_reason");
        if (callReason.equals("edit_document")) {
            curDocument.setTitle(intentCreatedMe.getStringExtra("document_title"));
            curDocument.setComment(intentCreatedMe.getStringExtra("document_comment"));
            curDocument.setExpirationDate(intentCreatedMe.getStringExtra("document_expiration_date"));
            position = intentCreatedMe.getIntExtra("position", -1);
            String categoryTitle = intentCreatedMe.getStringExtra("category_title");
            boolean hasPhoto = intentCreatedMe.getBooleanExtra("has_photo", false);
            if (hasPhoto) {
                //upload document's image from storage
                storageReference.child("Files").
                        child(MyPreferences.getUserDocumentPathFromPreferences(getApplicationContext())).child(categoryTitle).child(curDocument.getTitle()).child("image").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Bitmap previewBitmap = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() * 0.1), (int) (bmp.getHeight() * 0.1), true);
                        documentImageView.setImageBitmap(previewBitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d(TAG, "failed to fetch image from firebase storage");
                    }
                });
            }
            // todo add preview of image
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
                    isAlarm = true;
                    v1.setVisibility(View.VISIBLE);
                    v2.setVisibility(View.VISIBLE);
                    v3.setVisibility(View.VISIBLE);
                    v4.setVisibility(View.VISIBLE);
                    v5.setVisibility(View.VISIBLE);
                    v6.setVisibility(View.VISIBLE);
                } else {
                    isAlarm = false;
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
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhotoOnClickAS();
            }
        });

        check1.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    addToCalendar(documentTitleET.getEditText().getText().toString(), alarmTimePicker1);
                }
            }
        });

        check2.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    addToCalendar(documentTitleET.getEditText().getText().toString(), alarmTimePicker2);
                }
            }
        });

        addFieldsValidation();

    }

    private void addFieldsValidation() {
        validateDocumentTitle();
    }

    /**
    This method initializes Activity view Fields.
     */
    private void initializeActivityFields() {
        documentTitleET = findViewById(R.id.et_document_title);
        documentCommentET = findViewById(R.id.et_comment);
        reminderTimeET1 = findViewById(R.id.et_time1);
        reminderTimeET2 = findViewById(R.id.et_time2);
        reminderSwitch = findViewById(R.id.add_alarm);
        reminderSpinner1 = findViewById(R.id.spinner_times1);
        reminderSpinner2 = findViewById(R.id.spinner_times2);
        check1 = findViewById(R.id.checkbox_calendar1);
        check2 = findViewById(R.id.checkbox_calendar2);
        documentExpirationDateET = findViewById(R.id.et_expiration_date);
        documentImageView = findViewById(R.id.iv_doc);
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
        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                alarmTimePicker1 = timePicker;
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
                alarmTimePicker2 = timePicker;
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
        if (changedPhoto) {
            intentBack.putExtra("has_photo", true);
            intentBack.putExtra("imageUri", selectedImage.toString());
            Log.d(TAG, "adding photo");
        }
        intentBack.putExtra("document_title", documentTitleET.getEditText().getText().toString());
        intentBack.putExtra("document_comment", documentCommentET.getEditText().getText().toString());
        intentBack.putExtra("document_expiration_date", documentExpirationDateET.getEditText().getText().toString());
        intentBack.putExtra("is_alarm", isAlarm);
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
        return isDocumentTitleValid;
    }


    @Override
    public void onDateSet(DatePicker datePicker1, int year, int month, int day) {
        datePicker = datePicker1;
        month = month + 1;
        String date = day + "/" + month + "/" + year;
        documentExpirationDateET.getEditText().setText(date);

        // todo check the correct dates
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
    private void addToCalendar(String docTitle, TimePicker timePicker) {
        Calendar cal = Calendar.getInstance();
        cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
        long startTime = cal.getTimeInMillis();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", startTime);
        intent.putExtra("allDay", false);
        intent.putExtra("rrule", "FREQ=YEARLY");
//        intent.putExtra("endTime", reminderTime1+60*60*1000);
        intent.putExtra("title", "Reminder! your document: " + docTitle + " is expired");

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
     * updates the activity view after adding an image
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                selectedImage = result.getUri();
                addImageToDoc();
            }
    }

    /*
    the method adds an image to document
     */
    private void addImageToDoc() {
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

    public int setAlarm(int time) {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Date date = new Date();
        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTime(date);
        cal_alarm.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                alarmTimePicker1.getCurrentHour(), alarmTimePicker1.getCurrentMinute(), 0);
        if (cal_alarm.before(cal_now)) {
            return -1;
        }

        Intent myIntent = new Intent(getBaseContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, myIntent, 0);

        manager.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
        return 0;
    }

    /**
     * validate the entered name.
     */
    private void validateDocumentTitle() {
        setIsDocumentTitleValidToTrueIfValid();
        documentTitleET.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isDocumentTitleValid = false;
                int inputLength = documentTitleET.getEditText().getText().toString().length();
                if (inputLength >= 16) {//todo check number
                    documentTitleET.setError("Maximum Limit Reached!");
                } else if (inputLength == 0) {
                    documentTitleET.setError("Document title is required!");
                } else {
                    documentTitleET.setError(null);
                    curDocument.setTitle(documentTitleET.getEditText().getText().toString());
                    isDocumentTitleValid = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * set isDocumentTitleValid to true if document title is valid
     */
    private void setIsDocumentTitleValidToTrueIfValid() {
        int inputLength = documentTitleET.getEditText().getText().toString().length();
        if (inputLength < 16 && inputLength > 0) {
            isDocumentTitleValid = true;
        }
    }


}