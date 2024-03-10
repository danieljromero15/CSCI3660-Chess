package com.danielromero.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView[][] chessGrid = new ImageView[8][8];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                chessGrid[i][j] = findViewById(getResources().getIdentifier(getIDfromNums(i, j), "id", getPackageName()));
                chessGrid[i][j].setOnClickListener(this::printDebug);
            }
        }

        newGame();

    }

    private void printDebug(View view) {
        String viewString = String.valueOf(view);
        String square = viewString.substring(viewString.lastIndexOf("app:id/") + 7, viewString.length() - 1);
        Log.d("square", square);
    }

    private String getIDfromNums(int x, int y) {
        return String.valueOf((char) (x + 97)) + String.valueOf(y + 1);
    }

    public void newGame(){
        // initial setup
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String currentSquare = getIDfromNums(i, j);
                switch(currentSquare){
                    case "a1":
                    case "h1":
                        chessGrid[i][j].setImageResource(R.drawable.chess_piece_rook_white);
                        break;
                    case "a8":
                    case "h8":
                        chessGrid[i][j].setImageResource(R.drawable.chess_piece_rook_black);
                        break;
                    case "b1":
                    case "g1":
                        chessGrid[i][j].setImageResource(R.drawable.chess_piece_knight_white);
                        break;
                    case "b8":
                    case "g8":
                        chessGrid[i][j].setImageResource(R.drawable.chess_piece_knight_black);
                        break;
                    case "c1":
                    case "f1":
                        chessGrid[i][j].setImageResource(R.drawable.chess_piece_bishop_white);
                        break;
                    case "c8":
                    case "f8":
                        chessGrid[i][j].setImageResource(R.drawable.chess_piece_bishop_black);
                        break;
                    case "d1":
                        chessGrid[i][j].setImageResource(R.drawable.chess_piece_queen_white);
                        break;
                    case "d8":
                        chessGrid[i][j].setImageResource(R.drawable.chess_piece_queen_black);
                        break;
                    case "e1":
                        chessGrid[i][j].setImageResource(R.drawable.chess_piece_king_white);
                        break;
                    case "e8":
                        chessGrid[i][j].setImageResource(R.drawable.chess_piece_king_black);
                        break;
                    case "a2":
                    case "b2":
                    case "c2":
                    case "d2":
                    case "e2":
                    case "f2":
                    case "g2":
                    case "h2":
                        chessGrid[i][j].setImageResource(R.drawable.chess_piece_pawn_white);
                        break;
                    case "a7":
                    case "b7":
                    case "c7":
                    case "d7":
                    case "e7":
                    case "f7":
                    case "g7":
                    case "h7":
                        chessGrid[i][j].setImageResource(R.drawable.chess_piece_pawn_black);
                        break;
                }
            }
        }
    }
}