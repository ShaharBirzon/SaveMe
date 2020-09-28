package com.save.saveme.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class MyPreferences {


    public static void saveUserDocumentPathToPreferences(Context context, String userDocumentPath) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userDocumentPath", userDocumentPath);
        editor.apply();
    }

    public static String getUserDocumentPathFromPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        return sharedPreferences.getString("userDocumentPath", null);

    }

    public static boolean isFirstTime(Context context) {
        String userDocumentPath = getUserDocumentPathFromPreferences(context);
        return userDocumentPath == null;
    }
}
