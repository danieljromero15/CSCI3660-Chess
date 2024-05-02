package com.danielromero.chess

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.danielromero.chess.Chess.pieceName

object Storage {
    private var sharedPrefs: SharedPreferences? = null
    private var edit: SharedPreferences.Editor? = null
    private var mChess: Chess? = null

    //Use Storage.make(x,y) to initialize the Storage class, don't make an object
    //Call Storage.whatever() to implement things
    //Make for title screen since mChess doesn't exist yet
    fun make(context: Context) {
        if (sharedPrefs == null) {
            sharedPrefs = context.getSharedPreferences("stats", Context.MODE_PRIVATE)
        }
        edit = sharedPrefs!!.edit()
        vibeCheck()
    }

    //Overload make for game fragment since mChess will exist
    fun make(context: Context, bingus: Chess?) {
        if (sharedPrefs == null) {
            sharedPrefs = context.getSharedPreferences("stats", Context.MODE_PRIVATE)
        }
        edit = sharedPrefs!!.edit()
        mChess = bingus
        vibeCheck()
    }

    //Initializing the numbers imma work with, pretty boring
    //Alternatively call this to reset for testing
    //No, I will not rename this
    fun vibeCheck() {
        if (getInt("wPAWN") == -1) {
            //My apolocheese if this is annoying to read
            edit!!.putString(
                "Board",
                "bROOK,bKNIGHT,bBISHOP,bQUEEN,bKING,bBISHOP,bKNIGHT,bROOK,bPAWN,bPAWN,bPAWN,bPAWN,bPAWN,bPAWN,bPAWN,bPAWN,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,wPAWN,wPAWN,wPAWN,wPAWN,wPAWN,wPAWN,wPAWN,wPAWN,wROOK,wKNIGHT,wBISHOP,wQUEEN,wKING,wBISHOP,wKNIGHT,wROOK"
            )
            edit!!.putInt("wPAWN", 0)
            edit!!.putInt("wROOK", 0)
            edit!!.putInt("wKNIGHT", 0)
            edit!!.putInt("wBISHOP", 0)
            edit!!.putInt("wKING", 0)
            edit!!.putInt("wQUEEN", 0)
            edit!!.putInt("bPAWN", 0)
            edit!!.putInt("bROOK", 0)
            edit!!.putInt("bKNIGHT", 0)
            edit!!.putInt("bBISHOP", 0)
            edit!!.putInt("bKING", 0)
            edit!!.putInt("bQUEEN", 0)
            edit!!.putInt("win", 0)
            edit!!.putInt("lose", 0)
            edit!!.apply()
        }
    }

    //Use to parse the "Board" sharedpref because I can't store arrays (tragic, I know)
    fun theParsening(): Array<String> {
        return getString("Board")!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
    }

    //Heavily modified version of the newGame() from Chess.java to take the string and make pieces\
    //TODO make this work
    fun setBoard() {
        val board = theParsening()
        for (s in board) {
            Log.w("test", s)
        }
        for (i in 0..7) {
            for (j in 0..7) {
                val pos = i + j

                //mChess.setChessPieces(null, i, j);
                //mChess.getChessSquare(i,j).setImageDrawable(null);
                var piece: ChessPiece? = null
                val square: String = Chess.Companion.getIDfromNums(i, j)
                when (board[pos]) {
                    "wPAWN" -> piece = ChessPiece(square, pieceName.wPAWN)
                    "wKNIGHT" -> piece = ChessPiece(square, pieceName.wKNIGHT)
                    "wROOK" -> piece = ChessPiece(square, pieceName.wROOK)
                    "wBISHOP" -> piece = ChessPiece(square, pieceName.wBISHOP)
                    "wKING" -> piece = ChessPiece(square, pieceName.wKING)
                    "wQUEEN" -> piece = ChessPiece(square, pieceName.wQUEEN)
                    "bPAWN" -> piece = ChessPiece(square, pieceName.bPAWN)
                    "bKNIGHT" -> piece = ChessPiece(square, pieceName.bKNIGHT)
                    "bROOK" -> piece = ChessPiece(square, pieceName.bROOK)
                    "bBISHOP" -> piece = ChessPiece(square, pieceName.bBISHOP)
                    "bKING" -> piece = ChessPiece(square, pieceName.bKING)
                    "bQUEEN" -> piece = ChessPiece(square, pieceName.bQUEEN)
                    "00" -> {}
                }
                mChess!!.setPiece(piece)
            }
        }
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
    fun getString(key: String?): String? {
        return sharedPrefs!!.getString(key, "Jumpscare")
    }

    fun getInt(key: String?): Int {
        return sharedPrefs!!.getInt(key, -1)
    }

    //setters, don't use the int one thxvm
    fun setString(key: String?, string: String?) {
        edit!!.putString(key, string)
        edit!!.apply()
    }

    fun setInt(key: String?, value: Int) {
        edit!!.putInt(key, value)
        edit!!.apply()
    }

    //counter, use this to bump integers
    fun upCount(piece: String?) {
        edit!!.putInt(piece, getInt(piece) + 1)
        edit!!.apply()
    }
}
