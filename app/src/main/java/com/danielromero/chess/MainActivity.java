package com.danielromero.chess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Chess mChess;
    ChessPiece selectedPiece = null;
    ArrayList<View> possibleSelections = new ArrayList<>();

    // Defines colors for the selection, we should probably change these since I just chose them since they were easy to write
    final int pieceSelectColor = R.color.yellow;
    final int possibleSelectColor = R.color.blue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChess = new Chess();

        Button new_game = findViewById(R.id.new_game_button);
        new_game.setOnClickListener(this::newGame); // sets New Game button

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                mChess.setChessSquare(getSquareView(i, j), i, j); // assigns imageview widgets to the array backend
                mChess.getChessSquare(i, j).setOnClickListener(this::selectSquare); // makes each tile clickable
                getSquareView(i, j).setTag(null); // Shouldn't need this but it's there just in case
            }
        }

        mChess.newGame(); // starts game
        clearSelections();

    }

    private void selectSquare(View view) { // code for when a square is tapped
        resetColors();
        setColor(view, pieceSelectColor); // sets square color to yellow

        String viewString = String.valueOf(view);
        String square = viewString.substring(viewString.lastIndexOf("app:id/") + 7, viewString.length() - 1);
        //Log.d("square", square); // prints ID of square selected

        ChessPiece currentPiece = mChess.getPiece(square);

        if (selectedPiece != null
                && (currentPiece == null || selectedPiece.getPieceColor() != currentPiece.getPieceColor())
        ) { // move to other space
            if ((view.getTag() != null) && (view.getTag() == "possibleMove")) {
                mChess.setChessPieces(null, selectedPiece.getColumn(), selectedPiece.getRow()); // removes from array

                currentPiece = selectedPiece;
                currentPiece.setPosition(square);

                selectedPiece = null; // removes old piece (either other color or none) from board

                resetColors();
                mChess.setChessPieces(currentPiece, square); // sets piece to new place in array
                mChess.updateBoard();
                clearSelections();
            }
        } else if (currentPiece != null) {
            selectedPiece = currentPiece;

            clearSelections(); // reset selections

            switch (selectedPiece.getPieceName()) { // highlights where the piece can move
                case wPAWN:
                    possibleSelections.add(getSquareView(selectedPiece.getColumn(), selectedPiece.getRow() + 1)); // adds views to an arraylist
                    possibleSelections.add(getSquareView(selectedPiece.getColumn(), selectedPiece.getRow() + 2));
                    break;
            } // TODO: Add rules for all the other pieces

            for (View selection : possibleSelections) { // sets all views in the arraylist to be a certain color and selectable
                setColor(selection, possibleSelectColor);
                selection.setTag("possibleMove");
            }
        } else {
            Log.wtf("wtf", "help");
        }
    }

    @SuppressLint("DiscouragedApi")
    public ImageView getSquareView(int x, int y) { // gets the square from activity main, used for adding all views to the array
        if (x < 8 && y < 8) return findViewById(getResources().getIdentifier(Chess.getIDfromNums(x, y), "id", getPackageName()));
        else return null;
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
        if (view != null) view.setBackgroundColor(ContextCompat.getColor(this, color));
    }
    
    public void clearSelections(){ // clears selections array and resets all tags
        for(View view : possibleSelections) view.setTag(null);
        possibleSelections.clear();
    }
}