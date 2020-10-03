package com.save.saveme.document;

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
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;

import com.save.saveme.utils.FirebaseMediate;
import com.save.saveme.utils.MyPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.save.saveme.utils.AlarmReceiver;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.switchmaterial.SwitchMaterial;

import android.widget.TimePicker;

import android.widget.Toast;

import com.save.saveme.R;
import com.save.saveme.category.CategoryActivity;
import com.save.saveme.category.Document;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * a class for the document activity
 */
public class DocumentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, ExpirationDateDialog.OnExpirationDateInputListener {

    private static final String TAG = "DocumentActivity";
    private static final long ONE_MEGABYTE = 1024 * 1024;
    public static final int FILE_REQUEST_CODE = 10;
    private static int notificationId = 0;
    private TextInputLayout documentTitleET;
    private TextInputLayout documentCommentET;
    private TextInputLayout documentExpirationDateET;
    private TextInputLayout reminderTimeET1;
    private String callReason;
    private String categoryTitle;
    private int position;
    private Spinner reminderSpinner1;
    private SwitchMaterial reminderSwitch;
    private Document curDocument = new Document();
    private Button addImageBtn, addFileBtn;
    private Uri selectedImage, fileUri, fileDownloadUri;
    private ImageView documentImageView;
    private LinearLayout filePreviewLayout;
    private ProgressBar progressBar;
    private boolean changedImage;
    private boolean changedReminderTime = false;
    private boolean isAlarm = false;
    private CheckBox check1;
    private TimePicker alarmTimePicker1;
    private DatePicker datePicker;
    private int alarmBeforeDay = 0;
    private long alarmBeforeWeek = 0;
    private int alarmBeforeMonth = 0;

    private View v1;
    private View v4;
    private View v5;
    private View v6;

    private boolean isDocumentTitleValid = false;
    private static StorageReference storageReference;
    private FirebaseStorage storage;
    private boolean isUploadingFile = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        initializeActivityFields();
        filePreviewLayout.setVisibility(View.GONE);
        setReminderTime();

        final Intent intentCreatedMe = getIntent();
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

        documentExpirationDateET.getEditText().setOnClickListener(new View.OnClickListener() {
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
                }
            }
        });

        reminderTimeET1.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog1();
            }
        });

        reminderTimeET1.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog1();
            }
        });

        changedImage = false;
        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageOnClick();
            }
        });
        addFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDocumentTitleValid) {
                    selectFile();
                } else {
                    Toast.makeText(DocumentActivity.this, "please choose document title first", Toast.LENGTH_LONG).show();
                }
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
        addFieldsValidation();
    }

    /*
     * make remainder visible
     */
    private void setFirstRemainderFieldsVisibility(int visibility) {
        v1.setVisibility(visibility);
        v4.setVisibility(visibility);
        v5.setVisibility(visibility);
        v6.setVisibility(visibility);
    }

    /*
     * when a document is edited
     */
    private void handleEditDocument(Intent intentCreatedMe) {
        curDocument.setTitle(intentCreatedMe.getStringExtra("document_title"));
        curDocument.setComment(intentCreatedMe.getStringExtra("document_comment"));
        curDocument.setExpirationDate(intentCreatedMe.getStringExtra("document_expiration_date"));
        position = intentCreatedMe.getIntExtra("position", -1);
        boolean hasImage = intentCreatedMe.getBooleanExtra("has_image", false);
        if (hasImage) {
            curDocument.setHasImage(true);
            Button btnAddImage = findViewById(R.id.btn_add_doc_image);
            btnAddImage.setText(R.string.change_image);
            //upload document's image from storage
            storageReference.child("Files").
                    child(MyPreferences.getUserDocumentPathFromPreferences(getApplicationContext())).child(categoryTitle).child(curDocument.getTitle()).child("image").getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Bitmap previewBitmap = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() * 0.5), (int) (bmp.getHeight() * 0.5), true);
                    documentImageView.setImageBitmap(previewBitmap);
                    curDocument.setBitmap(bmp);
                    Log.d(TAG, "successfully fetch document image from firebase storage");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.e(TAG, "failed to fetch image from firebase storage");
                }
            });
        }
        curDocument.setHasFile(intentCreatedMe.getBooleanExtra("has_file", false));
        if (curDocument.isHasFile()) {
            Button btnAddImage = findViewById(R.id.btn_add_doc_file);
            btnAddImage.setText(R.string.change_file);
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
            check1.setChecked(intentCreatedMe.getBooleanExtra("is_add_event_to_phone_calender", false));
        }
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

    /*
     * validate doc fields
     */
    private void addFieldsValidation() {
        validateDocumentTitle();
        validateDocumentComment();
    }

    /*
     * This method initializes Activity view Fields.
     */
    private void initializeActivityFields() {
        documentTitleET = findViewById(R.id.et_document_title);
        documentCommentET = findViewById(R.id.et_comment);
        reminderTimeET1 = findViewById(R.id.et_time1);
        reminderSwitch = findViewById(R.id.add_alarm);
        reminderSpinner1 = findViewById(R.id.spinner_times1);
        check1 = findViewById(R.id.checkbox_calendar1);
        documentExpirationDateET = findViewById(R.id.et_expiration_date);
        documentImageView = findViewById(R.id.iv_doc);
        filePreviewLayout = findViewById(R.id.ll_doc_file_prev);
        progressBar = findViewById(R.id.fileProgressBar);
        progressBar.setVisibility(View.INVISIBLE);
        addImageBtn = findViewById(R.id.btn_add_doc_image);
        addFileBtn = findViewById(R.id.btn_add_doc_file);

        v1 = findViewById(R.id.et_time1);
        v4 = findViewById(R.id.tv_add_to_calendar1);
        v5 = findViewById(R.id.checkbox_calendar1);
        v6 = findViewById(R.id.spinner_times1);
    }

    /*
     * This method initializes Activity Fields With Document Data From DB
     */
    private void initializeActivityFieldsWithDocumentDataFromDB() {
        Log.d(TAG, "got to initializeActivityFieldsWithDocumentDataFromDB");
        documentTitleET.getEditText().setText(curDocument.getTitle());
        documentCommentET.getEditText().setText(curDocument.getComment());
        documentExpirationDateET.getEditText().setText(curDocument.getExpirationDate());
    }

    /*
     * show date dialog
     */
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    /*
     * show time picker
     */
    private void showTimePickerDialog1() {
        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                alarmTimePicker1 = timePicker;
                String minStr = "" + min;
                if (min < 10) {
                    minStr = "0" + min;
                }
                String time = hour + ":" + minStr;
                reminderTimeET1.getEditText().setText(time);
                changedReminderTime = true;
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
            return;
        }

        if (curDocument.getHasImage() && changedImage) {
            FirebaseMediate.uploadImageToFirebaseStorageDB(curDocument.getBitmap(), getApplicationContext(), categoryTitle, documentTitleET.getEditText().getText().toString(), "image");
        }

        Intent intentBack = new Intent(DocumentActivity.this, CategoryActivity.class);

        if (callReason.equals("edit_document")) {
            intentBack.putExtra("document_position", position);
        }

        intentBack.putExtra("has_image", curDocument.getHasImage());
        intentBack.putExtra("file_download_uri", curDocument.getFileDownloadUri());
        intentBack.putExtra("has_file", curDocument.isHasFile());
        intentBack.putExtra("document_title", documentTitleET.getEditText().getText().toString());
        intentBack.putExtra("document_comment", documentCommentET.getEditText().getText().toString());
        intentBack.putExtra("document_expiration_date", documentExpirationDateET.getEditText().getText().toString());
        intentBack.putExtra("document_reminder_time", reminderTimeET1.getEditText().getText().toString());
        intentBack.putExtra("is_alarm", isAlarm);
        intentBack.putExtra("is_add_event_to_phone_calender", check1.isChecked());

        setResult(RESULT_OK, intentBack);
        finish();
    }

    /**
     * This method verifies user input is valid.
     *
     * @return true if user input is valid, false otherwise.
     */
    private boolean isInputValid() {
        if (!isDocumentTitleValid) {
            Toast.makeText(getApplicationContext(), "invalid Title", Toast.LENGTH_LONG).show();
            return false;
        } else if (isAlarm && documentExpirationDateET.getEditText().getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Can't set alarm without expiration date", Toast.LENGTH_LONG).show();
            return false;
        } else if (isUploadingFile) {
            Toast.makeText(getApplicationContext(), "Can't save document while uploading file", Toast.LENGTH_LONG).show();
            return false;
        }
        if (changedReminderTime && !reminderTimeET1.getEditText().getText().toString().equals("") && !documentExpirationDateET.getEditText().getText().toString().equals("")) {
            Log.i("document activity", "valid for alarm");
            if (setAlarm(alarmTimePicker1) != 0) {
                Toast.makeText(getApplicationContext(), "This time has passed!", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
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

                switch (title) {
                    case "day before":
                        alarmBeforeDay = 1;
                        break;
                    case "week before":
                        alarmBeforeWeek = 604800000;
                        break;
                    case "month before":
                        alarmBeforeMonth = 1;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }


    /*
     * add expiration date to calendar
     */
    private void addToCalendar(String docTitle, TimePicker timePicker) {
        Calendar cal = Calendar.getInstance();
        cal.set(getExpirationDateYear(), getExpirationDateMonth(), getExpirationDateDay(),
                getReminderHourTime(timePicker), getReminderMinutesTime(timePicker), 0);
        long startTime = cal.getTimeInMillis();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", startTime);
        intent.putExtra("allDay", false);
        intent.putExtra("rrule", "FREQ=YEARLY");
        intent.putExtra("title", "Reminder! your document: " + docTitle + " is expired");

        startActivity(intent);
    }

    private Integer getReminderHourTime(TimePicker timePicker) {
        if (timePicker != null) {
            return timePicker.getCurrentHour();
        } else {
            return Integer.parseInt(reminderTimeET1.getEditText().getText().toString().split(":")[0]);
        }
    }

    /*
     * get remainder time
     */
    private Integer getReminderMinutesTime(TimePicker timePicker) {
        if (timePicker != null) {
            return timePicker.getCurrentMinute();
        } else {
            return Integer.parseInt(reminderTimeET1.getEditText().getText().toString().split(":")[1]);
        }
    }

    /**
     * this method opens the gallery to choose image. Activates when user clicks on an upload image
     * button.
     */
    public void uploadImageOnClick() {
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
            documentImageView.setDrawingCacheEnabled(true);
            documentImageView.buildDrawingCache();
            curDocument.setBitmap(bitmap);
            curDocument.setHasImage(true);
            changedImage = true;
            Button btnAddImage = findViewById(R.id.btn_add_doc_image);
            btnAddImage.setText(R.string.change_image);
            doOCROnImage(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    check if there is a date in image with OCR
     */
    private void doOCROnImage(Bitmap bitmap) {
        // do OCR for image to check if an expiration date exists
        TextRecognizer textRecognizer = new TextRecognizer.Builder(this).build();
        Frame imageFrame = new Frame.Builder().setBitmap(bitmap).build();
        String imageText = "";
        SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
        DateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yy");
        DateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date d;
        ArrayList<String> possibleDates = new ArrayList<>();
        for (int i = 0; i < textBlocks.size(); i++) {
            TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
            imageText = textBlock.getValue();
            Log.d(TAG, imageText);
            try {
                d = inputDateFormat.parse(imageText);
                possibleDates.add(outputDateFormat.format(d));
                Log.d(TAG, "the date: " + d.toString());
                // string contains valid date
            } catch (ParseException ex) {
                // string contains invalid date
                Log.d(TAG, "the date is error");
            }
        }
        if (!possibleDates.isEmpty()) {
            startExpirationDateDialog(possibleDates);
        }
    }

    /*
    the method checks if to set the date received from OCR to expiration date
     */
    private void startExpirationDateDialog(ArrayList<String> possibleDates) {
        ExpirationDateDialog expirationDateDialog = new ExpirationDateDialog(possibleDates.toArray(new String[0]));
        expirationDateDialog.show(getSupportFragmentManager(), "ExpirationDateDialog");
    }

    /*
    the method adds an image to document
     */
    private void addFileToDoc() {
        Log.d(TAG, "adding file to document activity");
        uploadDocumentFileToDB(getApplicationContext(), categoryTitle, curDocument.getTitle(), fileUri);
        curDocument.setHasFile(true);
        filePreviewLayout.setVisibility(View.VISIBLE);
        Button btnAddImage = findViewById(R.id.btn_add_doc_file);
        btnAddImage.setText(R.string.change_file);
    }


    /**
     * set an alarm
     *
     * @param time - time of alarm
     * @return
     */
    public int setAlarm(TimePicker time) {
        Log.i("document activity", "entered setAlarm");
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Date date = new Date();
        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTime(date);
        cal_alarm.set(getExpirationDateYear(), getExpirationDateMonth() - alarmBeforeMonth, getExpirationDateDay() - alarmBeforeDay,
                getReminderHourTime(time), time.getCurrentMinute(), 0);

        if (cal_alarm.before(cal_now)) {
            return -1;
        }

        Intent myIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        myIntent.putExtra("call_reason", "edit_document");
        myIntent.putExtra("position", position);
        myIntent.putExtra("notificationId", notificationId);
        myIntent.putExtra("document_title", curDocument.getTitle());
        myIntent.putExtra("category_title", categoryTitle);
        myIntent.putExtra("document_comment", curDocument.getComment());
        myIntent.putExtra("document_expiration_date", curDocument.getExpirationDate());
        myIntent.putExtra("document_reminder_time", curDocument.getReminderTime());
        myIntent.putExtra("has_image", curDocument.getHasImage());
        myIntent.putExtra("has_file", curDocument.isHasFile());
        myIntent.putExtra("file_download_uri", curDocument.getFileDownloadUri());
        myIntent.putExtra("has_alarm", curDocument.getHasAlarm());
        myIntent.putExtra("is_add_event_to_phone_calender", curDocument.getIsAddEventToPhoneCalender());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notificationId, myIntent, PendingIntent.FLAG_ONE_SHOT);
        notificationId++;
        manager.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis() - alarmBeforeWeek, pendingIntent);
        return 0;
    }

    /*
     * get expiration date year
     */
    private int getExpirationDateYear() {
        if (datePicker != null) {
            return datePicker.getYear();
        } else {
            return Integer.parseInt(documentExpirationDateET.getEditText().getText().toString().split("/")[2]);
        }
    }

    /*
     * get expiration date month
     */
    private int getExpirationDateMonth() {
        if (datePicker != null) {
            return datePicker.getMonth();
        } else {
            return Integer.parseInt(documentExpirationDateET.getEditText().getText().toString().split("/")[1]);
        }
    }

    /*
     * get expiration date day
     */
    private int getExpirationDateDay() {
        if (datePicker != null) {
            return datePicker.getDayOfMonth();
        } else {
            return Integer.parseInt(documentExpirationDateET.getEditText().getText().toString().split("/")[0]);
        }
    }

    /*
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
                if (inputLength >= 16) {
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

    /*
     * set isDocumentTitleValid to true if document title is valid
     */
    private void setIsDocumentTitleValidToTrueIfValid() {
        int inputLength = documentTitleET.getEditText().getText().toString().length();
        if (inputLength < 16 && inputLength > 0) {
            isDocumentTitleValid = true;
        }
    }

    private void validateDocumentComment() {
        documentCommentET.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                curDocument.setComment(documentCommentET.getEditText().getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * when a file is clicked
     *
     * @param view - view
     */
    public void onClickOpenFile(View view) {
        if (fileDownloadUri != null) {
            Intent intent = new Intent(DocumentActivity.this, DisplayFileActivity.class);
            Log.d(TAG, "file_url " + fileDownloadUri.toString());
            intent.putExtra("file_url", fileDownloadUri.toString());
            startActivity(intent);
        }
    }

    /*
     * upload file to DB
     */
    private void uploadDocumentFileToDB(Context context, String categoryTitle, String documentTitle, final Uri fileUri) {
        isUploadingFile = true;
        progressBar.setVisibility(View.VISIBLE);
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
                            curDocument.setFileDownloadUri(fileDownloadUri.toString());
                            isUploadingFile = false;
                            progressBar.setVisibility(View.INVISIBLE);
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.i(TAG, "Upload is " + progress + "% done");
                int currentProgress = (int) progress;
                progressBar.setProgress(currentProgress);
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "Upload is paused");
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
        Bitmap bmp = curDocument.getBitmap();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bmp, (int) (bmp.getWidth() * 0.5), (int) (bmp.getHeight() * 0.5), true);
        Bitmap compressedBitmap = scaleDownBitmap(previewBitmap, 800, true); //todo check what should be maxImageSize
        compressedBitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        fullScreenIntent.putExtra("image", byteArray);
        Log.d(TAG, "got to end of onImageClick");
        startActivity(fullScreenIntent);
    }

    /*
     * scales down the bitmap
     */
    private static Bitmap scaleDownBitmap(Bitmap realImage, float maxImageSize,
                                          boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    @Override
    public void sendInput(String date) {
        curDocument.setExpirationDate(date);
        documentExpirationDateET.getEditText().setText(date);
    }
}
