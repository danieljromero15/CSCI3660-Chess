package com.danielromero.chess;

public class ChessPiece extends Chess {
    private int column;
    private int row;
    private final pieceName name;
    private int pieceImage;
    private int pieceColor;

    ChessPiece(int column, int row, pieceName name) {
        this.column = column;
        this.row = row;
        this.name = name;
        setPieceColor();
        switch (this.name) { // white
            case wPAWN:
                this.pieceImage = R.drawable.chess_piece_pawn_white;
                break;
            case wROOK:
                this.pieceImage = R.drawable.chess_piece_rook_white;
                break;
            case wKNIGHT:
                this.pieceImage = R.drawable.chess_piece_knight_white;
                break;
            case wBISHOP:
                this.pieceImage = R.drawable.chess_piece_bishop_white;
                break;
            case wQUEEN:
                this.pieceImage = R.drawable.chess_piece_queen_white;
                break;
            case wKING:
                this.pieceImage = R.drawable.chess_piece_king_white;
                break;
            case bPAWN:
                this.pieceImage = R.drawable.chess_piece_pawn_black;
                break;
            case bROOK:
                this.pieceImage = R.drawable.chess_piece_rook_black;
                break;
            case bKNIGHT:
                this.pieceImage = R.drawable.chess_piece_knight_black;
                break;
            case bBISHOP:
                this.pieceImage = R.drawable.chess_piece_bishop_black;
                break;
            case bQUEEN:
                this.pieceImage = R.drawable.chess_piece_queen_black;
                break;
            case bKING:
                this.pieceImage = R.drawable.chess_piece_king_black;
                break;
        }
    }

    ChessPiece(String pos, pieceName name) {
        this(Chess.getNumsfromID(pos)[0], Chess.getNumsfromID(pos)[1], name);
    }

    public int getColumn() {
        return column;
    }

    public int getX(){
        return getColumn();
    }

    private void setColumn(int x) {
        this.column = x;
    }

    public int getRow() {
        return row;
    }

    public int getY(){
        return getRow();
    }

    private void setRow(int y) {
        this.row = y;
    }

    public void setPosition(int x, int y) {
        this.setColumn(x);
        this.setRow(y);
    }

    public void setPosition(String pos) {
        setPosition(getNumsfromID(pos)[0], getNumsfromID(pos)[1]);
    }

    public String getPosition(){
        return Chess.getIDfromNums(this.getX(), this.getY());
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

    public void setPieceColor() {
        switch (this.getPieceName()) {
            case wPAWN:
            case wROOK:
            case wKNIGHT:
            case wBISHOP:
            case wQUEEN:
            case wKING:
                this.pieceColor = R.color.white;
                break;
            case bPAWN:
            case bROOK:
            case bKNIGHT:
            case bBISHOP:
            case bQUEEN:
            case bKING:
                this.pieceColor = R.color.black;
                break;
        }
    }
}
