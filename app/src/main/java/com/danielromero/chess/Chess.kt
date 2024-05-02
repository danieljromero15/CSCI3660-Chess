package com.danielromero.chess

import android.util.Log
import android.widget.ImageView

open class Chess {
    val chessGrid: Array<Array<ImageView?>> = Array(8) { arrayOfNulls(8) }
    val chessPieces: Array<Array<ChessPiece?>> = Array(8) { arrayOfNulls(8) }

    enum class pieceName {
        wPAWN, wROOK, wKNIGHT, wBISHOP, wQUEEN, wKING, bPAWN, bROOK, bKNIGHT, bBISHOP, bQUEEN, bKING
    }

    fun updateBoard() {
        for (i in 0..7) {
            for (j in 0..7) {
                val view = getChessSquare(i, j)
                val piece = getPiece(i, j)
                if (piece != null) {
                    setPiece(piece)
                } else {
                    view!!.setImageDrawable(null)
                }
            }
        }
    }

    fun setChessSquare(view: ImageView?, x: Int, y: Int) { // adds view to the array
        //Log.d("view", view.toString())
        chessGrid[x][y] = view
    }

    fun getChessSquare(x: Int, y: Int): ImageView? { // gets a view from the array
        //Log.d("view", chessGrid[x][y].toString())
        return chessGrid[x][y]
    }

    fun setChessPieces(piece: ChessPiece?, x: Int, y: Int) { // adds piece to the array
        chessPieces[x][y] = piece
    }

    fun setChessPieces(piece: ChessPiece?, pos: String) {
        setChessPieces(piece, getNumsfromID(pos)[0], getNumsfromID(pos)[1])
    }

    fun setPiece(piece: ChessPiece?) { // sets a piece to a specific square
        if (piece != null) {
            val x = piece.column
            val y = piece.row
            setChessPieces(piece, x, y) // sets into array
            getChessSquare(x, y)?.setImageResource(piece.pieceImage) // sets onto imageview
        }
    }

    fun getPiece(x: Int, y: Int): ChessPiece? {
        return chessPieces[x][y]
    }

    fun getPiece(pos: String): ChessPiece? {
        return getPiece(getNumsfromID(pos)[0], getNumsfromID(pos)[1])
    }

    fun newGame() {
        Log.d("newGame", "New Game started")

        // initial setup, adds all pieces to board
        for (i in 0..7) {
            for (j in 0..7) {
                // clears any previous pieces on selected tiles in the loop (so every tile)
                setChessPieces(null, i, j) // sets into array
                getChessSquare(i, j)?.setImageDrawable(null) // sets onto imageview


                var currentPiece: ChessPiece? = null
                var tempPieceName: pieceName? = null
                val currentSquare = getIDfromNums(i, j)
                when (currentSquare) {
                    "a1", "h1" -> tempPieceName = pieceName.wROOK
                    "a8", "h8" -> tempPieceName = pieceName.bROOK
                    "b1", "g1" -> tempPieceName = pieceName.wKNIGHT
                    "b8", "g8" -> tempPieceName = pieceName.bKNIGHT
                    "c1", "f1" -> tempPieceName = pieceName.wBISHOP
                    "c8", "f8" -> tempPieceName = pieceName.bBISHOP
                    "d1" -> tempPieceName = pieceName.wQUEEN
                    "d8" -> tempPieceName = pieceName.bQUEEN
                    "e1" -> tempPieceName = pieceName.wKING
                    "e8" -> tempPieceName = pieceName.bKING
                    "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2" -> tempPieceName =
                        pieceName.wPAWN

                    "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7" -> tempPieceName =
                        pieceName.bPAWN
                }
                if (tempPieceName != null) {
                    currentPiece = ChessPiece(currentSquare, tempPieceName)
                }
                setPiece(currentPiece) // sets piece onto board
            }
        }
    }

    fun setBoard() {
        val board = Storage.theParsening() //Makes board string
        var f = 0
        for (i in 7 downTo 0) { //loops from top left to bottom right, important because I don't know how to make things easy on myself
            for (j in 0..7) {
                setChessPieces(null, j, i)
                getChessSquare(j, i)!!.setImageDrawable(null)

                var piece: ChessPiece? = null
                var tempPieceName: pieceName? = null

                val square = getIDfromNums(j, i)
                val position = board!![f]
                when (position) {
                    "wPAWN" -> tempPieceName = pieceName.wPAWN
                    "wKNIGHT" -> tempPieceName = pieceName.wKNIGHT
                    "wROOK" -> tempPieceName = pieceName.wROOK
                    "wBISHOP" -> tempPieceName = pieceName.wBISHOP
                    "wKING" -> tempPieceName = pieceName.wKING
                    "wQUEEN" -> tempPieceName = pieceName.wQUEEN
                    "bPAWN" -> tempPieceName = pieceName.bPAWN
                    "bKNIGHT" -> tempPieceName = pieceName.bKNIGHT
                    "bROOK" -> tempPieceName = pieceName.bROOK
                    "bBISHOP" -> tempPieceName = pieceName.bBISHOP
                    "bKING" -> tempPieceName = pieceName.bKING
                    "bQUEEN" -> tempPieceName = pieceName.bQUEEN
                    "00" -> {}
                }
                if (tempPieceName != null) {
                    piece = ChessPiece(square, tempPieceName)
                }
                setPiece(piece)
                f++
            }
        }
    }

    fun debug_printChess() {
        val out = StringBuilder()
        out.append("--------------------------------------------------------")
            .append(System.lineSeparator())
        for (i in 7 downTo 0) {
            for (j in 0..7) {
                val piece = chessPieces[j][i]
                if (piece != null) {
                    //out.append(chessPieces[j][i].getPieceName()).append(" ");
                    val name = piece.pieceName.toString()
                    when (name.length) {
                        0 -> out.append("").append(name).append("       ")
                        1 -> out.append("   ").append(name).append("   ")
                        2 -> out.append("  ").append(name).append("   ")
                        3 -> out.append("  ").append(name).append("  ")
                        4 -> out.append(" ").append(name).append("  ")
                        5 -> out.append(" ").append(name).append(" ")
                        6 -> out.append("").append(name).append(" ")
                        7 -> out.append(name)
                    }
                } else {
                    out.append("       ")
                }
            }
            out.append(System.lineSeparator())
        }

        out.append("--------------------------------------------------------")
        Log.d("Chess", out.toString())
    }

    companion object {
        fun getIDfromNums(x: Int, y: Int): String { // converts ints to string, eg. 0, 0 to "a1"
            return (x + 97).toChar().toString() + (y + 1)
        }

        fun getNumsfromID(ID: String): IntArray { // converts string to ints, eg. "h8" to {7, 7}
            Log.d("ID", "$ID");
            if (ID.length > 2) {
                return intArrayOf(ID[26].code - 97, ID[27].toString().toInt() - 1)
            } else {
                return intArrayOf(ID[0].code - 97, ID[1].toString().toInt() - 1)
            }
        }
    }
}
