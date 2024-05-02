package com.danielromero.chess

class ChessPiece internal constructor(var column: Int, var row: Int, private var name: PieceName) :
    Chess() {
    var pieceImage: Int = 0
        private set
    var pieceColor: Int = 0
        private set

    init {
        setPieceColor()
        setPieceImage()
    }

    internal constructor(pos: String, name: PieceName) : this(
        getNumsfromID(pos)[0], getNumsfromID(pos)[1], name
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
        get() = getIDfromNums(this.x, this.y)
        set(pos) {
            setPosition(
                getNumsfromID(pos)[0],
                getNumsfromID(pos)[1]
            )
        }

    var pieceName: PieceName
        get() = name
        set(name) {
            this.name = name
            setPieceImage()
        }

    override fun toString(): String {
        //return Chess.pieceName.toString()
        return this.pieceName.toString()
    }

    private fun setPieceColor() {
        when (this.pieceName) {
            PieceName.WPawn, PieceName.WRook, PieceName.WKnight, PieceName.WBishop, PieceName.WQueen, PieceName.WKing -> this.pieceColor =
                R.color.white

            PieceName.BPawn, PieceName.BRook, PieceName.BKnight, PieceName.BBishop, PieceName.BQueen, PieceName.BKing -> this.pieceColor =
                R.color.black
        }
    }

    private fun setPieceImage() {
        when (this.pieceName) {
            PieceName.WPawn -> this.pieceImage = R.drawable.chess_piece_pawn_white
            PieceName.WRook -> this.pieceImage = R.drawable.chess_piece_rook_white
            PieceName.WKnight -> this.pieceImage = R.drawable.chess_piece_knight_white
            PieceName.WBishop -> this.pieceImage = R.drawable.chess_piece_bishop_white
            PieceName.WQueen -> this.pieceImage = R.drawable.chess_piece_queen_white
            PieceName.WKing -> this.pieceImage = R.drawable.chess_piece_king_white
            PieceName.BPawn -> this.pieceImage = R.drawable.chess_piece_pawn_black
            PieceName.BRook -> this.pieceImage = R.drawable.chess_piece_rook_black
            PieceName.BKnight -> this.pieceImage = R.drawable.chess_piece_knight_black
            PieceName.BBishop -> this.pieceImage = R.drawable.chess_piece_bishop_black
            PieceName.BQueen -> this.pieceImage = R.drawable.chess_piece_queen_black
            PieceName.BKing -> this.pieceImage = R.drawable.chess_piece_king_black
        }
    }
}
