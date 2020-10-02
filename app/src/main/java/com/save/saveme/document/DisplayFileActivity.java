package com.save.saveme.document;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.save.saveme.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * a class that displays a document file
 */
public class DisplayFileActivity extends AppCompatActivity {

    public static final String GOOGLE_PREFIX_URL = "https://drive.google.com/viewerng/viewer?embedded=true&url=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_file);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();

        final Intent intentCreatedMe = getIntent();
        final String file = intentCreatedMe.getStringExtra("file_url");
        String url = null;
        try {
            url = URLEncoder.encode(file, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String doc = GOOGLE_PREFIX_URL + url;
        final WebView webView = (WebView) findViewById(R.id.webView);
        webView.setVisibility(View.INVISIBLE);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                actionBar.setTitle("Loading..");
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                    actionBar.setTitle("SaveMe");
                }
            }
        });
        webView.loadUrl(doc);
    }
}
