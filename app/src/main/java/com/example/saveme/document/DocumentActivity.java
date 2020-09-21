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
import android.content.pm.PackageManager;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.saveme.utils.MyPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.example.saveme.utils.AlarmReceiver;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;

import android.widget.TimePicker;

import android.widget.Toast;

import com.example.saveme.R;
import com.example.saveme.category.CategoryActivity;
import com.example.saveme.category.Document;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class DocumentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "DocumentActivity";
    private static final long ONE_MEGABYTE = 1024 * 1024;
    public static final int FILE_REQUEST_CODE = 10;
    private TextInputLayout documentTitleET;
    private TextInputLayout documentCommentET;
    private TextInputLayout documentExpirationDateET;
    private TextInputLayout reminderTimeET1;
    private TextInputLayout reminderTimeET2;
    private String callReason;
    private String categoryTitle;
    private int position;
    private Spinner reminderSpinner1;
    private Spinner reminderSpinner2;
    SwitchMaterial reminderSwitch;
    private Document curDocument = new Document();
    private Button addPhotoBtn, addFileBtn;
    private Uri selectedImage, fileUri, fileDownloadUri;
    ImageView documentImageView;
    LinearLayout filePreviewLayout;
    private boolean changedPhoto, changedReminderTime = false, addedFile = false;
    private boolean isAlarm = false;
    CheckBox check1;
    CheckBox check2;
    TimePicker alarmTimePicker1;
    TimePicker alarmTimePicker2;
    DatePicker datePicker;

    private View v1;
    private View v2;
    private View v3;
    private View v4;
    private View v5;
    private View v6;
    private View v7;
    private View v8;
    private View v9;
    private View v10;
    private View v11;

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
        filePreviewLayout.setVisibility(View.GONE);
        setReminderTime();
        // todo add other

        Intent intentCreatedMe = getIntent();
        categoryTitle = intentCreatedMe.getStringExtra("category_title");
        callReason = intentCreatedMe.getStringExtra("call_reason");
        if (callReason.equals("edit_document")) {
            handleEditDocument(intentCreatedMe);
        }
        boolean hasFile = intentCreatedMe.getBooleanExtra("has_file", false);
        curDocument.setHasFile(hasFile);
        if (hasFile) {
            filePreviewLayout.setVisibility(View.VISIBLE);
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

                if (b) {
                    isAlarm = true;
                    setFirstRemainderFieldsVisibility(View.VISIBLE);
                } else {
                    isAlarm = false;
                    setFirstRemainderFieldsVisibility(View.GONE);
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
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhotoOnClickAS();
            }
        });
        addFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFile();
                //todo change to this
//                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                    selectFile();
//                } else {
//                    Log.e(TAG, "missing permission");
//
//                    ActivityCompat.requestPermissions(DocumentActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 225);
//                }

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

    private void setFirstRemainderFieldsVisibility(int visibility) {
        v1.setVisibility(visibility);
        v2.setVisibility(visibility);
        v3.setVisibility(visibility);
        v4.setVisibility(visibility);
        v5.setVisibility(visibility);
        v6.setVisibility(visibility);
    }

    private void handleEditDocument(Intent intentCreatedMe) {
        curDocument.setTitle(intentCreatedMe.getStringExtra("document_title"));
        curDocument.setComment(intentCreatedMe.getStringExtra("document_comment"));
        curDocument.setExpirationDate(intentCreatedMe.getStringExtra("document_expiration_date"));
        position = intentCreatedMe.getIntExtra("position", -1);
        boolean hasPicture = intentCreatedMe.getBooleanExtra("has_photo", false);
        if (hasPicture) {
            //upload document's image from storage
            storageReference.child("Files").
                    child(MyPreferences.getUserDocumentPathFromPreferences(getApplicationContext())).child(categoryTitle).child(curDocument.getTitle()).child("image").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Bitmap previewBitmap = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() * 0.5), (int) (bmp.getHeight() * 0.5), true);
                    documentImageView.setImageBitmap(previewBitmap);
                    curDocument.setBitmap(previewBitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, "failed to fetch image from firebase storage");
                }
            });
        }
        curDocument.setHasFile(intentCreatedMe.getBooleanExtra("has_file", false));
        if (curDocument.isHasFile()) {
            filePreviewLayout.setVisibility(View.VISIBLE);
            curDocument.setFileDownloadUri(intentCreatedMe.getStringExtra("file_download_uri"));
            fileDownloadUri = Uri.parse(curDocument.getFileDownloadUri());
        }
        curDocument.setHasAlarm(intentCreatedMe.getBooleanExtra("has_alarm", false));
        if (curDocument.getHasAlarm()) {
            isAlarm = true;
            reminderSwitch.setChecked(true);
            setFirstRemainderFieldsVisibility(View.VISIBLE);
            reminderTimeET1.getEditText().setText(intentCreatedMe.getStringExtra("document_reminder_time"));
        }
        // todo add preview of image
        initializeActivityFieldsWithDocumentDataFromDB();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 225 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectFile();
        } else {
            Toast.makeText(DocumentActivity.this, "please provide permission", Toast.LENGTH_LONG).show();
        }
    }

    private void selectFile() {
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("*/*");
        fileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(fileIntent, "Select Your File"), FILE_REQUEST_CODE);
    }

    private void addFieldsValidation() {
        validateDocumentTitle();
    }

    /**
     * This method initializes Activity view Fields.
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
        filePreviewLayout = findViewById(R.id.ll_doc_file_prev);
        addPhotoBtn = findViewById(R.id.btn_add_doc_photo);
        addFileBtn = findViewById(R.id.btn_add_doc_file);

        v1 = findViewById(R.id.et_time1);
        v2 = findViewById(R.id.btn_add_another_alarm);
        v3 = findViewById(R.id.tv_reminder1);
        v4 = findViewById(R.id.tv_add_to_calendar1);
        v5 = findViewById(R.id.checkbox_calendar1);
        v6 = findViewById(R.id.spinner_times1);
        v7 = findViewById(R.id.et_time2);
        v8 = findViewById(R.id.tv_reminder2);
        v9 = findViewById(R.id.tv_add_to_calendar2);
        v10 = findViewById(R.id.checkbox_calendar2);
        v11 = findViewById(R.id.spinner_times2);
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
                changedReminderTime = true;
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
        if (changedReminderTime && !reminderTimeET1.getEditText().getText().toString().equals("") && !documentExpirationDateET.getEditText().getText().toString().equals("")) {
            Log.i("document activity", "valid for alarm");
            if (setAlarm(alarmTimePicker1) != 0) {
                Toast toast = Toast.makeText(getApplicationContext(), "This time has passed!", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }
        Log.i("document activity", "after if");


        Intent intentBack = new Intent(DocumentActivity.this, CategoryActivity.class);

        if (callReason.equals("edit_document")) {
            intentBack.putExtra("document_position", position);
        }
        if (changedPhoto) {
            intentBack.putExtra("has_photo", true);
            intentBack.putExtra("imageUri", selectedImage.toString());
            Log.d(TAG, "adding photo");
        }
        if (addedFile) {
            intentBack.putExtra("file_download_uri", fileDownloadUri.toString());
            intentBack.putExtra("has_file", true);
        }
        intentBack.putExtra("document_title", documentTitleET.getEditText().getText().toString());
        intentBack.putExtra("document_comment", documentCommentET.getEditText().getText().toString());
        intentBack.putExtra("document_expiration_date", documentExpirationDateET.getEditText().getText().toString());
        intentBack.putExtra("document_reminder_time", reminderTimeET1.getEditText().getText().toString());
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
        curDocument.setExpirationDate(date);
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
            } else if (requestCode == FILE_REQUEST_CODE && data != null) {
                Log.d(TAG, "got to FILE_REQUEST_CODE");
                fileUri = data.getData(); //uri of selected file

                addFileToDoc();
            } else {
                Log.e(TAG, "got to????");

            }
    }

    /*
    the method adds an image to document
     */
    private void addImageToDoc() {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver()
                    , selectedImage);
            Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.5), (int) (bitmap.getHeight() * 0.5), true);
            documentImageView.setImageBitmap(previewBitmap);
            curDocument.setBitmap(previewBitmap);
            curDocument.setHasPicture(true);
            changedPhoto = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    the method adds an image to document
     */
    private void addFileToDoc() {
        Log.d(TAG, "adding file to document activity");
        uploadDocumentFileToDB(getApplicationContext(), categoryTitle, curDocument.getTitle(), fileUri);
        curDocument.setHasFile(true);
        filePreviewLayout.setVisibility(View.VISIBLE);
        addedFile = true;
    }


    public int setAlarm(TimePicker time) {
        Log.i("document activity", "entered setAlarm");
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Date date = new Date();
        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTime(date);
        cal_alarm.set(getExpirationDateYear(), getExpirationDateMonth(), getExpirationDateDay(),
                time.getCurrentHour(), time.getCurrentMinute(), 0);

        if (cal_alarm.before(cal_now)) {
            return -1;
        }

        Intent myIntent = new Intent(getBaseContext(), AlarmReceiver.class);
        myIntent.putExtra("call_reason", "edit_document");
        myIntent.putExtra("position", position);
        myIntent.putExtra("document_title", curDocument.getTitle());
        myIntent.putExtra("category_title", categoryTitle);
        myIntent.putExtra("document_comment", curDocument.getComment());
        myIntent.putExtra("document_expiration_date", curDocument.getExpirationDate());
        myIntent.putExtra("document_reminder_time", curDocument.getReminderTime());
        myIntent.putExtra("has_photo", curDocument.getHasPicture());
        myIntent.putExtra("has_file", curDocument.isHasFile());
        myIntent.putExtra("file_download_uri", curDocument.getFileDownloadUri());
        myIntent.putExtra("has_alarm", curDocument.getHasAlarm());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, myIntent, 0);

        manager.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), pendingIntent);
        return 0;
    }

    private int getExpirationDateYear() {
        if (datePicker != null) {
            return datePicker.getYear();
        } else {
            return Integer.parseInt(documentExpirationDateET.getEditText().getText().toString().split("/")[2]);
        }
    }

    private int getExpirationDateMonth() {
        if (datePicker != null) {
            return datePicker.getMonth();
        } else {
            return Integer.parseInt(documentExpirationDateET.getEditText().getText().toString().split("/")[1]);
        }
    }

    private int getExpirationDateDay() {
        if (datePicker != null) {
            return datePicker.getDayOfMonth();
        } else {
            return Integer.parseInt(documentExpirationDateET.getEditText().getText().toString().split("/")[0]);
        }
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


    public void onClickOpenFile(View view) {
        if (fileDownloadUri != null) {
            Intent intent = new Intent(DocumentActivity.this, DisplayFileActivity.class);
            Log.d(TAG, "file_url " + fileDownloadUri.toString());
            intent.putExtra("file_url", fileDownloadUri.toString());
            startActivity(intent);
        }
    }

    private void uploadDocumentFileToDB(Context context, String categoryTitle, String documentTitle, final Uri fileUri) {
        final StorageReference ref = storageReference.child("Files").
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
                ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            fileDownloadUri = task.getResult();
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });
            }
        });
    }

    /**
     * the method moves to a full screen of the image when image is clicked
     *
     * @param view - view
     */
    public void onImageClick(View view) {
        final Intent fullScreenIntent = new Intent(this, DisplayImageActivity.class);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        curDocument.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        fullScreenIntent.putExtra("image", byteArray);
        startActivity(fullScreenIntent);
    }
}
