package com.danielromero.chess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    Chess mChess;
    ChessPiece selectedPiece = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChess = new Chess();

        Button new_game = findViewById(R.id.new_game_button);
        new_game.setOnClickListener(this::newGame); // sets newgame button

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                mChess.setChessSquare(getSquareView(i, j), i, j); // assigns imageview widgets to the array backend
                mChess.getChessSquare(i, j).setOnClickListener(this::selectSquare); // makes each tile clickable
            }
        }

        mChess.newGame(); // starts game

    }

    private void selectSquare(View view) { // code for when a square is tapped
        resetColors();
        setColor(view, R.color.yellow); // sets square color to yellow

        String viewString = String.valueOf(view);
        String square = viewString.substring(viewString.lastIndexOf("app:id/") + 7, viewString.length() - 1);
        //Log.d("square", square); // prints ID of sqare selected

        ChessPiece currentPiece = mChess.getPiece(square);

        if (selectedPiece != null
                && (currentPiece == null || selectedPiece.getPieceColor() != currentPiece.getPieceColor())
        ) { // move to other space
            mChess.setChessPieces(null, selectedPiece.getColumn(), selectedPiece.getRow()); // removes from array

            currentPiece = selectedPiece;
            currentPiece.setPosition(square);

            selectedPiece = null;

            resetColors();
            mChess.setChessPieces(currentPiece, square);
            mChess.updateBoard();
        } else if (currentPiece != null) {
            selectedPiece = currentPiece;
        } else {
            Log.wtf("wtf", "help");
        }
    }

    @SuppressLint("DiscouragedApi")
    public ImageView getSquareView(int x, int y) { // gets the square from activity main, used for adding all views to the array
        return findViewById(getResources().getIdentifier(Chess.getIDfromNums(x, y), "id", getPackageName()));
    }

    private void newGame(View view) { // starts new game
        resetColors();
        mChess.newGame();
    }

    private void resetColors() { // resets all colors to the standard black and white
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) {
                    setColor(mChess.getChessSquare(i, j), R.color.black);
                } else {
                    setColor(mChess.getChessSquare(i, j), R.color.white);
                }
            }
        }
    }

    public void setColor(View view, int color) { // sets the color for a single view
        view.setBackgroundColor(ContextCompat.getColor(this, color));
    }
}