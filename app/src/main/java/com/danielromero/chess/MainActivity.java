package com.danielromero.chess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    Chess mChess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChess = new Chess();

        Button new_game = findViewById(R.id.new_game_button);
        new_game.setOnClickListener(this::newGame);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                mChess.setChessSquare(getSquareView(i, j), i, j); // assigns imageview widgets to the array backing
                //mChess.chessGrid[i][j].setOnClickListener(this::printDebug);
                mChess.getChessSquare(i, j).setOnClickListener(this::selectSquare);
            }
        }

        mChess.newGame();

    }

    private void selectSquare(View view) {
        resetColors();
        setColor(view, R.color.yellow);

        String viewString = String.valueOf(view);
        String square = viewString.substring(viewString.lastIndexOf("app:id/") + 7, viewString.length() - 1);
        Log.d("square", square);
    }

    public ImageView getSquareView(int x, int y) {
        return findViewById(getResources().getIdentifier(Chess.getIDfromNums(x, y), "id", getPackageName()));
    }

    private void newGame(View view) {
        resetColors();
        mChess.newGame();
    }

    private void resetColors(){
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

    public void setColor(View view, int color) {
        view.setBackgroundColor(ContextCompat.getColor(this, color));
    }
}