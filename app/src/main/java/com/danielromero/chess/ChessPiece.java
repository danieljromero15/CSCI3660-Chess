package com.danielromero.chess;

public class ChessPiece extends Chess {
    private int column;
    private int row;
    private final String pieceName;
    private int pieceImage;
    private final int pieceColor;

    ChessPiece(int column, int row, String pieceName, int pieceColor) {
        this.column = column;
        this.row = row;
        this.pieceName = pieceName;
        this.pieceColor = pieceColor;
        if (this.pieceColor == R.color.white) { // white
            switch (this.pieceName) {
                case "pawn":
                    this.pieceImage = R.drawable.chess_piece_pawn_white;
                    break;
                case "rook":
                    this.pieceImage = R.drawable.chess_piece_rook_white;
                    break;
                case "knight":
                    this.pieceImage = R.drawable.chess_piece_knight_white;
                    break;
                case "bishop":
                    this.pieceImage = R.drawable.chess_piece_bishop_white;
                    break;
                case "queen":
                    this.pieceImage = R.drawable.chess_piece_queen_white;
                    break;
                case "king":
                    this.pieceImage = R.drawable.chess_piece_king_white;
                    break;
            }
        } else { // black
            switch (this.pieceName) {
                case "pawn":
                    this.pieceImage = R.drawable.chess_piece_pawn_black;
                    break;
                case "rook":
                    this.pieceImage = R.drawable.chess_piece_rook_black;
                    break;
                case "knight":
                    this.pieceImage = R.drawable.chess_piece_knight_black;
                    break;
                case "bishop":
                    this.pieceImage = R.drawable.chess_piece_bishop_black;
                    break;
                case "queen":
                    this.pieceImage = R.drawable.chess_piece_queen_black;
                    break;
                case "king":
                    this.pieceImage = R.drawable.chess_piece_king_black;
                    break;
            }
        }


    }

    ChessPiece(String pos, String pieceName, int pieceColor) {
        this(Chess.getNumsfromID(pos)[0], Chess.getNumsfromID(pos)[1], pieceName, pieceColor);
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

    public String getPieceName() {
        return pieceName;
    }
}
