package com.danielromero.chess;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Storage {
    private static SharedPreferences sharedPrefs;
    private static SharedPreferences.Editor edit;
    private static Chess mChess;

    //the wawa 2
    private Storage() {
    }

    //Use this to initialize the Storage class, don't make an object
    //Call Storage.whatever() to implement things

    public static void make(Context context) {
        if (sharedPrefs == null) {
            sharedPrefs = context.getSharedPreferences("stats", Context.MODE_PRIVATE);
        }
        edit = sharedPrefs.edit();
        //mChess = bingus;
        vibeCheck();
    }

    public static void make(Context context, Chess bingus) {
        if (sharedPrefs == null) {
            sharedPrefs = context.getSharedPreferences("stats", Context.MODE_PRIVATE);
        }
        edit = sharedPrefs.edit();
        mChess = bingus;
        vibeCheck();
    }

    //Initializing the numbers imma work with, probably boring and long
    //Alternatively call this to reset for testing
    //No, I will not rename this
    public static void vibeCheck(){
        if(getInt("wPAWN") == -1){
            edit.putString("Board", "bROOK,bKNIGHT,bBISHOP,bQUEEN,bKING,bBISHOP,bKNIGHT,pROOK," + //my apolocheese if this is annoying to read
                            "bPAWN,bPAWN,bPAWN,bPAWN,bPAWN,bPAWN,bPAWN,bPAWN," +
                            "00,00,00,00,00,00,00,00," +
                            "00,00,00,00,00,00,00,00," +
                            "00,00,00,00,00,00,00,00," +
                            "00,00,00,00,00,00,00,00," +
                            "wPAWN,wPAWN,wPAWN,wPAWN,wPAWN,wPAWN,wPAWN,wPAWN," +
                            "wROOK,wKNIGHT,wBISHOP,wQUEEN,wKING,wBISHOP,wKNIGHT,wROOK");
            edit.putInt("wPAWN", 0);
            edit.putInt("wROOK", 0);
            edit.putInt("wKNIGHT", 0);
            edit.putInt("wBISHOP", 0);
            edit.putInt("wKING", 0);
            edit.putInt("wQUEEN", 0);
            edit.putInt("bPAWN", 0);
            edit.putInt("bROOK", 0);
            edit.putInt("bKNIGHT", 0);
            edit.putInt("bBISHOP", 0);
            edit.putInt("bKING", 0);
            edit.putInt("bQUEEN", 0);
            edit.putInt("win", 0);
            edit.putInt("lose", 0);
            edit.apply();
        }
    }

    //Use to parse the "Board" sharedpref because I can't store arrays (tragic, I know)
    public static String[] theParsening(String key){
        String[] gaming = getString(key).split(",");
        return gaming;
    }

    //Heavily modified version of the newGame() from Chess.java to take the string and make pieces\
    //TODO make this work
    public static void setBoard(){
        String[] board = theParsening(getString("Board"));
        for(int i = 0; i < board.length; i++){
            Log.w("test", board[i]);
        }
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                int pos = i+j;
                //mChess.setChessPieces(null, i, j);
                //mChess.getChessSquare(i,j).setImageDrawable(null);

                ChessPiece piece = null;
                String square = Chess.getIDfromNums(i,j);
                switch(board[pos]){
                    case "wPAWN":
                        piece = new ChessPiece(square, Chess.pieceName.wPAWN);
                        break;
                    case "wKNIGHT":
                        piece = new ChessPiece(square, Chess.pieceName.wKNIGHT);
                        break;
                    case "wROOK":
                        piece = new ChessPiece(square, Chess.pieceName.wROOK);
                        break;
                    case "wBISHOP":
                        piece = new ChessPiece(square, Chess.pieceName.wBISHOP);
                        break;
                    case "wKING":
                        piece = new ChessPiece(square, Chess.pieceName.wKING);
                        break;
                    case "wQUEEN":
                        piece = new ChessPiece(square, Chess.pieceName.wQUEEN);
                        break;
                    case "bPAWN":
                        piece = new ChessPiece(square, Chess.pieceName.bPAWN);
                        break;
                    case "bKNIGHT":
                        piece = new ChessPiece(square, Chess.pieceName.bKNIGHT);
                        break;
                    case "bROOK":
                        piece = new ChessPiece(square, Chess.pieceName.bROOK);
                        break;
                    case "bBISHOP":
                        piece = new ChessPiece(square, Chess.pieceName.bBISHOP);
                        break;
                    case "bKING":
                        piece = new ChessPiece(square, Chess.pieceName.bKING);
                        break;
                    case "bQUEEN":
                        piece = new ChessPiece(square, Chess.pieceName.bQUEEN);
                        break;
                    case "00":
                }
                mChess.setPiece(piece);
            }


        }
    }

    //getters, If you see the default value something is wrong (panic)
    //Any int returns should never be negative under any circumstance
    public static String getString(String key) {
        return sharedPrefs.getString(key, "Jumpscare");
    }
    public static int getInt(String key) {
        return sharedPrefs.getInt(key, -1);
    }

    //setters, don't use the int one thxvm
    public static void setString(String key, String string){
        edit.putString(key, string);
        edit.apply();
    }
    public static void setInt(String key, int value){
        edit.putInt(key, value);
        edit.apply();
    }

    //counter, use this to bump integers
    public static void upCount(String piece){
        edit.putInt(piece, getInt(piece)+1);
        edit.apply();
    }
}
