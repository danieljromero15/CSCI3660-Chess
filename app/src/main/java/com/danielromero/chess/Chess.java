package com.danielromero.chess;

import android.util.Log;
import android.widget.ImageView;

public class Chess {
    final ImageView[][] chessGrid = new ImageView[8][8];
    final ChessPiece[][] chessPieces = new ChessPiece[8][8];

    public enum pieceName {
        wPAWN, wROOK, wKNIGHT, wBISHOP, wQUEEN, wKING, bPAWN, bROOK, bKNIGHT, bBISHOP, bQUEEN, bKING
    }

    public void updateBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ImageView view = getChessSquare(i, j);
                ChessPiece piece = getPiece(i, j);
                if (piece != null) {
                    setPiece(piece);
                } else {
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

    public void setChessPieces(ChessPiece piece, String pos) {
        setChessPieces(piece, getNumsfromID(pos)[0], getNumsfromID(pos)[1]);
    }

    public static String getIDfromNums(int x, int y) { // converts ints to string, eg. 0, 0 to "a1"
        return String.valueOf((char) (x + 97)) + (y + 1);
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

    public ChessPiece getPiece(String pos) {
        return getPiece(getNumsfromID(pos)[0], getNumsfromID(pos)[1]);
    }

    public void newGame() {
        Log.d("newGame", "New Game started");

        // initial setup, adds all pieces to board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // clears any previous pieces on selected tiles in the loop (so every tile)
                setChessPieces(null, i, j); // sets into array
                getChessSquare(i, j).setImageDrawable(null); // sets onto imageview


                ChessPiece currentPiece = null;
                pieceName tempPieceName = null;
                String currentSquare = getIDfromNums(i, j);
                switch (currentSquare) {
                    case "a1":
                    case "h1":
                        tempPieceName = pieceName.wROOK;
                        break;
                    case "a8":
                    case "h8":
                        tempPieceName = pieceName.bROOK;
                        break;
                    case "b1":
                    case "g1":
                        tempPieceName = pieceName.wKNIGHT;
                        break;
                    case "b8":
                    case "g8":
                        tempPieceName = pieceName.bKNIGHT;
                        break;
                    case "c1":
                    case "f1":
                        tempPieceName = pieceName.wBISHOP;
                        break;
                    case "c8":
                    case "f8":
                        tempPieceName = pieceName.bBISHOP;
                        break;
                    case "d1":
                        tempPieceName = pieceName.wQUEEN;
                        break;
                    case "d8":
                        tempPieceName = pieceName.bQUEEN;
                        break;
                    case "e1":
                        tempPieceName = pieceName.wKING;
                        break;
                    case "e8":
                        tempPieceName = pieceName.bKING;
                        break;
                    case "a2":
                    case "b2":
                    case "c2":
                    case "d2":
                    case "e2":
                    case "f2":
                    case "g2":
                    case "h2":
                        tempPieceName = pieceName.wPAWN;
                        break;
                    case "a7":
                    case "b7":
                    case "c7":
                    case "d7":
                    case "e7":
                    case "f7":
                    case "g7":
                    case "h7":
                        tempPieceName = pieceName.bPAWN;
                        break;
                }

                if (tempPieceName != null) {
                    currentPiece = new ChessPiece(currentSquare, tempPieceName);
                }
                setPiece(currentPiece); // sets piece onto board
            }
        }
    }

    public void setBoard() {
        String[] board = Storage.theParsening(); //Makes board string
        int f = 0;
        for (int i = 7; i >= 0; i--) { //loops from top left to bottom right, important because I don't know how to make things easy on myself
            for (int j = 0; j < 8; j++) {
                setChessPieces(null, j, i);
                getChessSquare(j, i).setImageDrawable(null);

                ChessPiece piece = null;
                pieceName tempPieceName = null;

                String square = Chess.getIDfromNums(j, i);
                String position = board[f];
                switch (position) {
                    case "wPAWN":
                        tempPieceName = pieceName.wPAWN;
                        break;
                    case "wKNIGHT":
                        tempPieceName = pieceName.wKNIGHT;
                        break;
                    case "wROOK":
                        tempPieceName = pieceName.wROOK;
                        break;
                    case "wBISHOP":
                        tempPieceName = pieceName.wBISHOP;
                        break;
                    case "wKING":
                        tempPieceName = pieceName.wKING;
                        break;
                    case "wQUEEN":
                        tempPieceName = pieceName.wQUEEN;
                        break;
                    case "bPAWN":
                        tempPieceName = pieceName.bPAWN;
                        break;
                    case "bKNIGHT":
                        tempPieceName = pieceName.bKNIGHT;
                        break;
                    case "bROOK":
                        tempPieceName = pieceName.bROOK;
                        break;
                    case "bBISHOP":
                        tempPieceName = pieceName.bBISHOP;
                        break;
                    case "bKING":
                        tempPieceName = pieceName.bKING;
                        break;
                    case "bQUEEN":
                        tempPieceName = pieceName.bQUEEN;
                        break;
                    case "00":
                }
                if (tempPieceName != null) {
                    piece = new ChessPiece(square, tempPieceName);
                }
                setPiece(piece);
                f++;
            }
        }
    }

    public void debug_printChess() {
        StringBuilder out = new StringBuilder();
        out.append("--------------------------------------------------------").append(System.lineSeparator());
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = chessPieces[j][i];
                if (piece != null) {
                    //out.append(chessPieces[j][i].getPieceName()).append(" ");
                    String name = String.valueOf(piece.getPieceName());
                    switch (name.length()) { // awful code btw
                        case 0:
                            out.append("").append(name).append("       ");
                            break;
                        case 1:
                            out.append("   ").append(name).append("   ");
                            break;
                        case 2:
                            out.append("  ").append(name).append("   ");
                            break;
                        case 3:
                            out.append("  ").append(name).append("  ");
                            break;
                        case 4:
                            out.append(" ").append(name).append("  ");
                            break;
                        case 5:
                            out.append(" ").append(name).append(" ");
                            break;
                        case 6:
                            out.append("").append(name).append(" ");
                            break;
                        case 7:
                            out.append(name);
                            break;
                    }
                } else {
                    out.append("       ");
                }
            }
            out.append(System.lineSeparator());
        }

        out.append("--------------------------------------------------------");
        Log.d("Chess", out.toString());
    }
}
