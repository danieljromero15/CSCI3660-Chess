package com.danielromero.chess;

import android.widget.ImageView;

public class Chess {
    ImageView[][] chessGrid = new ImageView[8][8];
    ChessPiece[][] chessPieces = new ChessPiece[8][8];

    public void updateBoard(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ImageView view = getChessSquare(i, j);
                ChessPiece piece = getPiece(i, j);
                if(piece != null){
                    setPiece(piece);
                }else{
                    view.setImageDrawable(null);
                }
            }
        }
    }

    public void setChessSquare(ImageView view, int x, int y) { // adds view to the array
        chessGrid[x][y] = view;
    }

    public ImageView getChessSquare(int x, int y) { // gets a view from the array
        return chessGrid[x][y];
    }

    public void setChessPieces(ChessPiece piece, int x, int y) { // adds piece to the array
        chessPieces[x][y] = piece;
    }

    public void setChessPieces(ChessPiece piece, String pos){
        setChessPieces(piece, getNumsfromID(pos)[0], getNumsfromID(pos)[1]);
    }

    public static String getIDfromNums(int x, int y) { // converts ints to string, eg. 0, 0 to "a1"
        return String.valueOf((char) (x + 97)) + String.valueOf(y + 1); // haven't tested if the valueofs are unnecessary like lint says but it works so I'm not going to touch it
    }

    public static int[] getNumsfromID(String ID) { // converts string to ints, eg. "h8" to {7, 7}
        return new int[]{ID.charAt(0) - 97, Integer.parseInt(String.valueOf(ID.charAt(1))) - 1};
    }

    public void setPiece(ChessPiece piece) { // sets a piece to a specific square
        if (piece != null) {
            int x = piece.getColumn();
            int y = piece.getRow();
            setChessPieces(piece, x, y); // sets into array
            getChessSquare(x, y).setImageResource(piece.getPieceImage()); // sets onto imageview
        }
    }

    public ChessPiece getPiece(int x, int y) {
        return chessPieces[x][y];
    }

    public ChessPiece getPiece(String pos){
        return getPiece(getNumsfromID(pos)[0], getNumsfromID(pos)[1]);
    }

    public void newGame() {
        // initial setup, adds all pieces to board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // clears any previous pieces on selected tiles in the loop (so every tile)
                setChessPieces(null, i, j); // sets into array
                getChessSquare(i, j).setImageDrawable(null); // sets onto imageview


                ChessPiece currentPiece = null;
                String currentSquare = getIDfromNums(i, j);
                switch (currentSquare) {
                    case "a1":
                    case "h1":
                        currentPiece = new ChessPiece(currentSquare, "rook", R.color.white);
                        break;
                    case "a8":
                    case "h8":
                        currentPiece = new ChessPiece(currentSquare, "rook", R.color.black);
                        break;
                    case "b1":
                    case "g1":
                        currentPiece = new ChessPiece(currentSquare, "knight", R.color.white);
                        break;
                    case "b8":
                    case "g8":
                        currentPiece = new ChessPiece(currentSquare, "knight", R.color.black);
                        break;
                    case "c1":
                    case "f1":
                        currentPiece = new ChessPiece(currentSquare, "bishop", R.color.white);
                        break;
                    case "c8":
                    case "f8":
                        currentPiece = new ChessPiece(currentSquare, "bishop", R.color.black);
                        break;
                    case "d1":
                        currentPiece = new ChessPiece(currentSquare, "queen", R.color.white);
                        break;
                    case "d8":
                        currentPiece = new ChessPiece(currentSquare, "queen", R.color.black);
                        break;
                    case "e1":
                        currentPiece = new ChessPiece(currentSquare, "king", R.color.white);
                        break;
                    case "e8":
                        currentPiece = new ChessPiece(currentSquare, "king", R.color.black);
                        break;
                    case "a2":
                    case "b2":
                    case "c2":
                    case "d2":
                    case "e2":
                    case "f2":
                    case "g2":
                    case "h2":
                        currentPiece = new ChessPiece(currentSquare, "pawn", R.color.white);
                        break;
                    case "a7":
                    case "b7":
                    case "c7":
                    case "d7":
                    case "e7":
                    case "f7":
                    case "g7":
                    case "h7":
                        currentPiece = new ChessPiece(currentSquare, "pawn", R.color.black);
                        break;
                }

                setPiece(currentPiece); // sets piece onto board
            }
        }
    }
}
