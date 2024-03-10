package com.danielromero.chess;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;

class chessPiece{
    int column;
    int row;

    private void setPosition(int x, int y){}

    private void getPositon(ImageView view){}

}

public class Chess {
    ImageView[][] chessGrid = new ImageView[8][8];

    public void setChessSquare(ImageView view, int x, int y){
        chessGrid[x][y] = view;
    }

    public ImageView getChessSquare(int x, int y) {
        return chessGrid[x][y];
    }

    public void newGame(View view) {
        newGame();
    }

    public void newGame(){
        // initial setup
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String currentSquare = getIDfromNums(i, j);
                switch(currentSquare){
                    case "a1":
                    case "h1":
                        getChessSquare(i, j).setImageResource(R.drawable.chess_piece_rook_white);
                        break;
                    case "a8":
                    case "h8":
                        getChessSquare(i, j).setImageResource(R.drawable.chess_piece_rook_black);
                        break;
                    case "b1":
                    case "g1":
                        getChessSquare(i, j).setImageResource(R.drawable.chess_piece_knight_white);
                        break;
                    case "b8":
                    case "g8":
                        getChessSquare(i, j).setImageResource(R.drawable.chess_piece_knight_black);
                        break;
                    case "c1":
                    case "f1":
                        getChessSquare(i, j).setImageResource(R.drawable.chess_piece_bishop_white);
                        break;
                    case "c8":
                    case "f8":
                        getChessSquare(i, j).setImageResource(R.drawable.chess_piece_bishop_black);
                        break;
                    case "d1":
                        getChessSquare(i, j).setImageResource(R.drawable.chess_piece_queen_white);
                        break;
                    case "d8":
                        getChessSquare(i, j).setImageResource(R.drawable.chess_piece_queen_black);
                        break;
                    case "e1":
                        getChessSquare(i, j).setImageResource(R.drawable.chess_piece_king_white);
                        break;
                    case "e8":
                        getChessSquare(i, j).setImageResource(R.drawable.chess_piece_king_black);
                        break;
                    case "a2":
                    case "b2":
                    case "c2":
                    case "d2":
                    case "e2":
                    case "f2":
                    case "g2":
                    case "h2":
                        getChessSquare(i, j).setImageResource(R.drawable.chess_piece_pawn_white);
                        break;
                    case "a7":
                    case "b7":
                    case "c7":
                    case "d7":
                    case "e7":
                    case "f7":
                    case "g7":
                    case "h7":
                        getChessSquare(i, j).setImageResource(R.drawable.chess_piece_pawn_black);
                        break;
                }
            }
        }
    }

    public static String getIDfromNums(int x, int y) {
        return String.valueOf((char) (x + 97)) + String.valueOf(y + 1);
    }

    public static int[] getNumsfromID(String ID){
        return new int[]{ID.charAt(0) - 97, Integer.parseInt(String.valueOf(ID.charAt(1))) - 1};
    }
}
