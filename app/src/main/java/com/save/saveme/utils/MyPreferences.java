package com.save.saveme.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * a class for preferences
 */
public class MyPreferences {

    /**
     * save the document path
     *
     * @param context          - context
     * @param userDocumentPath - user document path
     */
    public static void saveUserDocumentPathToPreferences(Context context, String userDocumentPath) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userDocumentPath", userDocumentPath);
        editor.apply();
    }

    /**
     * get user document path
     * @param context - context
     * @return the path
     */
    public static String getUserDocumentPathFromPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        return sharedPreferences.getString("userDocumentPath", null);

    }

    /**
     * check if user's first time
     * @param context - context
     * @return if first time or not
     */
    public static boolean isFirstTime(Context context) {
        String userDocumentPath = getUserDocumentPathFromPreferences(context);
        return userDocumentPath == null;
    }
}
