package com.save.saveme.document;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.save.saveme.R;

/**
 * a class to display the document image on full screen
 */
public class DisplayImageActivity extends AppCompatActivity {

    private static final String TAG = "DocumentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        Log.d(TAG, "entered display image activity");
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        ImageView imageView = findViewById(R.id.iv_full_screen_doc);
        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }
}
