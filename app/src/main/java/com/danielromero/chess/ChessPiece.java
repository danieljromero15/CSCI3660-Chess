package com.danielromero.chess;

public class ChessPiece extends Chess {
    private int column;
    private int row;
    private final pieceName name;
    private int pieceImage;
    private final int pieceColor;

    ChessPiece(int column, int row, pieceName name, int pieceColor) {
        this.column = column;
        this.row = row;
        this.name = name;
        this.pieceColor = pieceColor;
        if (this.pieceColor == R.color.white) { // white
            switch (this.name) {
                case PAWN:
                    this.pieceImage = R.drawable.chess_piece_pawn_white;
                    break;
                case ROOK:
                    this.pieceImage = R.drawable.chess_piece_rook_white;
                    break;
                case KNIGHT:
                    this.pieceImage = R.drawable.chess_piece_knight_white;
                    break;
                case BISHOP:
                    this.pieceImage = R.drawable.chess_piece_bishop_white;
                    break;
                case QUEEN:
                    this.pieceImage = R.drawable.chess_piece_queen_white;
                    break;
                case KING:
                    this.pieceImage = R.drawable.chess_piece_king_white;
                    break;
            }
        } else { // black
            switch (this.name) {
                case PAWN:
                    this.pieceImage = R.drawable.chess_piece_pawn_black;
                    break;
                case ROOK:
                    this.pieceImage = R.drawable.chess_piece_rook_black;
                    break;
                case KNIGHT:
                    this.pieceImage = R.drawable.chess_piece_knight_black;
                    break;
                case BISHOP:
                    this.pieceImage = R.drawable.chess_piece_bishop_black;
                    break;
                case QUEEN:
                    this.pieceImage = R.drawable.chess_piece_queen_black;
                    break;
                case KING:
                    this.pieceImage = R.drawable.chess_piece_king_black;
                    break;
            }
        }


    }

    ChessPiece(String pos, pieceName name, int pieceColor) {
        this(Chess.getNumsfromID(pos)[0], Chess.getNumsfromID(pos)[1], name, pieceColor);
    }

    public int getColumn() {
        return column;
    }

    private void setColumn(int x){
        this.column = x;
    }

    public int getRow() {
        return row;
    }

    private void setRow(int y){
        this.row = y;
    }

    public void setPosition(int x, int y){
        this.setColumn(x);
        this.setRow(y);
    }

    public void setPosition(String pos){
        setPosition(getNumsfromID(pos)[0], getNumsfromID(pos)[1]);
    }

    public int getPieceColor() {
        return pieceColor;
    }

    public int getPieceImage() {
        return pieceImage;
    }

    public pieceName getPieceName() {
        return name;
    }
}
