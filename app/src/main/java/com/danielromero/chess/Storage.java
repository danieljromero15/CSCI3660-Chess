package com.danielromero.chess;

import android.content.Context;
import android.content.SharedPreferences;

public class Storage {
    private static SharedPreferences sharedPrefs;
    private static SharedPreferences.Editor edit;

    private Storage() {
    }

    public static void make(Context context) { //Use this to initialize the Storage class, don't make an object
        if (sharedPrefs == null) {
            sharedPrefs = context.getSharedPreferences("stats", Context.MODE_PRIVATE);
        }
        edit = sharedPrefs.edit();
        vibeCheck();
    }

    //Initializing the numbers imma work with, probably boring and long
    //Alternatively call this to reset for testing
    public static void vibeCheck(){
        if(getInt("wPAWN") == -1){
            edit.putString("Board", "00"); //TODO set initial board state, don't know how im gonna declare peices yet
            edit.putInt("wPAWN", 0);
            edit.putInt("wROOK", 0);
            edit.putInt("wKNIGHT", 0);
            edit.putInt("wBISHOP", 0);
            edit.putInt("wKING", 0);
            edit.putInt("wQUEEN", 0);
            edit.putInt("win", 0);
            edit.putInt("lose", 0);
            //edit.putInt("ComedyCounter", 0);
            edit.apply();
        }
    }

    //getters, If you see the default value something is wrong (panic)
    //Any int returns should never be negative under any circumstance
    public static String getString(String key) {
        return sharedPrefs.getString(key, "defaultValue");
    }
    public static int getInt(String key) {
        return sharedPrefs.getInt(key, -1);
    }

    //setters, don't use the int one thx
    public static void setString(String key, String string){
        edit.putString(key, string);
        edit.apply();
    }
    public static void setInt(String key, int value){
        edit.putInt(key, value);
        edit.apply();
    }

    //counters, use these to update integers
    public static void upCount(String piece){
        edit.putInt(piece, getInt(piece)+1);
        edit.apply();
    }
}
