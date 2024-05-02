package com.danielromero.chess

import android.content.Context
import android.content.SharedPreferences

object Storage {
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var edit: SharedPreferences.Editor
    private var mChess: Chess? = null

    //Use Storage.make(x,y) to initialize the Storage class, don't make an object
    //Call Storage.whatever() to implement things
    //Make for title screen since mChess doesn't exist yet
    fun make(context: Context) {
        sharedPrefs = context.getSharedPreferences("stats", Context.MODE_PRIVATE)
        edit = sharedPrefs.edit()
        vibeCheck()
    }

    //Overload make for game fragment since mChess will exist
    fun make(context: Context, bingus: Chess?) {
        sharedPrefs = context.getSharedPreferences("stats", Context.MODE_PRIVATE)
        edit = sharedPrefs.edit()
        mChess = bingus
        vibeCheck()
    }

    //Initializing the numbers imma work with, pretty boring
    //Alternatively call this to reset for testing
    //No, I will not rename this
    private fun vibeCheck() {
        if (getInt("WPawn") == -1) {
            //My apolocheese if this is annoying to read
            edit.putString(
                "Board",
                "BRook,BKnight,BBishop,BQueen,BKing,BBishop,BKnight,BRook,BPawn,BPawn,BPawn,BPawn,BPawn,BPawn,BPawn,BPawn,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,WPawn,WPawn,WPawn,WPawn,WPawn,WPawn,WPawn,WPawn,WRook,WKnight,WBishop,WQueen,WKing,WBishop,WKnight,WRook"
            )
            edit.putInt("WPawn", 0)
            edit.putInt("WRook", 0)
            edit.putInt("WKnight", 0)
            edit.putInt("WBishop", 0)
            edit.putInt("WKing", 0)
            edit.putInt("WQueen", 0)
            edit.putInt("BPawn", 0)
            edit.putInt("BRook", 0)
            edit.putInt("BKnight", 0)
            edit.putInt("BBishop", 0)
            edit.putInt("BKing", 0)
            edit.putInt("BQueen", 0)
            edit.putInt("win", 0)
            edit.putInt("lose", 0)
            edit.apply()
        }
    }

    //Use to parse the "Board" sharedpref because I can't store arrays (tragic, I know)
    fun theParsening(): Array<String> {
        return getBoard()!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
    }

    fun saveBoard() {
        val boardString = StringBuilder()
        for (i in 7 downTo 0) {
            for (j in 0..7) {
                val curPiece = mChess!!.getPiece(j, i)
                if (curPiece != null) {
                    val pieceS = curPiece.toString()
                    boardString.append(pieceS).append(",")
                } else {
                    boardString.append("00,")
                }
            }
        }
        boardString.setLength(boardString.length - 1)
        setString("Board", boardString.toString())
    }

    //getters, If you see the default value something is wrong (panic)
    //Any int returns should never be negative under any circumstance
    @Suppress("SameParameterValue")
    private fun getString(key: String?): String? {
        return sharedPrefs.getString(key, "Jumpscare")
    }

    private fun getBoard(): String? {
        return getString("Board")
    }

    fun getInt(key: String?): Int {
        return sharedPrefs.getInt(key, -1)
    }

    //setters, don't use the int one thxvm
    @Suppress("SameParameterValue")
    private fun setString(key: String?, string: String?) {
        edit.putString(key, string)
        edit.apply()
    }

    //counter, use this to bump integers
    fun upCount(piece: String?) {
        edit.putInt(piece, getInt(piece) + 1)
        edit.apply()
    }
}
