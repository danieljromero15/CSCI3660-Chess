package com.danielromero.chess

class ChessPiece internal constructor(var column: Int, var row: Int, private var name: pieceName) :
    Chess() {
    var pieceImage: Int = 0
        private set
    var pieceColor: Int = 0
        private set

    init {
        setPieceColor()
        setPieceImage()
    }

    internal constructor(pos: String, name: pieceName) : this(
        Chess.Companion.getNumsfromID(pos).get(0), Chess.Companion.getNumsfromID(pos).get(1), name
    )

    val x: Int
        get() = column

    val y: Int
        get() = row

    fun setPosition(x: Int, y: Int) {
        this.column = x
        this.row = y
    }

    var position: String
        get() = Chess.Companion.getIDfromNums(this.x, this.y)
        set(pos) {
            setPosition(
                Chess.Companion.getNumsfromID(pos).get(0),
                Chess.Companion.getNumsfromID(pos).get(1)
            )
        }

    var pieceName: pieceName
        get() = name
        set(name) {
            this.name = name
            setPieceImage()
        }

    override fun toString(): String {
        //return Chess.pieceName.toString()
        return this.pieceName.toString();
    }

    fun setPieceColor() {
        when (this.pieceName) {
            Chess.pieceName.wPAWN, Chess.pieceName.wROOK, Chess.pieceName.wKNIGHT, Chess.pieceName.wBISHOP, Chess.pieceName.wQUEEN, Chess.pieceName.wKING -> this.pieceColor =
                R.color.white

            Chess.pieceName.bPAWN, Chess.pieceName.bROOK, Chess.pieceName.bKNIGHT, Chess.pieceName.bBISHOP, Chess.pieceName.bQUEEN, Chess.pieceName.bKING -> this.pieceColor =
                R.color.black
        }
    }

    private fun setPieceImage() {
        when (this.pieceName) {
            Chess.pieceName.wPAWN -> this.pieceImage = R.drawable.chess_piece_pawn_white
            Chess.pieceName.wROOK -> this.pieceImage = R.drawable.chess_piece_rook_white
            Chess.pieceName.wKNIGHT -> this.pieceImage = R.drawable.chess_piece_knight_white
            Chess.pieceName.wBISHOP -> this.pieceImage = R.drawable.chess_piece_bishop_white
            Chess.pieceName.wQUEEN -> this.pieceImage = R.drawable.chess_piece_queen_white
            Chess.pieceName.wKING -> this.pieceImage = R.drawable.chess_piece_king_white
            Chess.pieceName.bPAWN -> this.pieceImage = R.drawable.chess_piece_pawn_black
            Chess.pieceName.bROOK -> this.pieceImage = R.drawable.chess_piece_rook_black
            Chess.pieceName.bKNIGHT -> this.pieceImage = R.drawable.chess_piece_knight_black
            Chess.pieceName.bBISHOP -> this.pieceImage = R.drawable.chess_piece_bishop_black
            Chess.pieceName.bQUEEN -> this.pieceImage = R.drawable.chess_piece_queen_black
            Chess.pieceName.bKING -> this.pieceImage = R.drawable.chess_piece_king_black
        }
    }
}
