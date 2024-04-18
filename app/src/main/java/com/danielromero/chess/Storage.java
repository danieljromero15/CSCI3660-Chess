package com.danielromero.chess;

import android.content.Context;
import android.content.SharedPreferences;

public class Storage {
    private static SharedPreferences sharedPrefs;
    private SharedPreferences.Editor edit;

    private Storage() { //the wawa
    }

    public static void make(Context context) { //Use this to initialize the Storage class
        if (sharedPrefs == null) {
            sharedPrefs = context.getSharedPreferences("stats", Context.MODE_PRIVATE);
        }
    }

    public static String getString(String key, String defValue) {
        return sharedPrefs.getString(key, defValue);
    }

}
