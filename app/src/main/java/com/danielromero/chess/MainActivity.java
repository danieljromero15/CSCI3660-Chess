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
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    boolean debug_printing = true;

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

        //Initialized storage and leaves a test message in log to see if it works
        // !! Log will only work on my phone rn because I was testing persistance
        Storage.make(this.getApplicationContext());
        //Log.w( "please",Storage.getString("test", "defaultValue"));

        mChess.newGame(); // starts game
        clearSelections();

        if (debug_printing) mChess.debug_printChess();
    }

    private void selectSquare(View view) { // code for when a square is tapped
        resetColors();
        setColor(view, pieceSelectColor); // sets square color to yellow

        String square = getIDfromView(view);
        //Log.d("square", square); // prints ID of square selected

        ChessPiece currentPiece = mChess.getPiece(square);

        if (selectedPiece != null && (currentPiece == null || selectedPiece.getPieceColor() != currentPiece.getPieceColor())) { // move to other space
            if ((view.getTag() != null) && (view.getTag() == "possibleMove")) {
                mChess.setChessPieces(null, selectedPiece.getColumn(), selectedPiece.getRow()); // removes from array

                currentPiece = selectedPiece;
                currentPiece.setPosition(square);

                selectedPiece = null; // removes old piece (either other color or none) from board

                // end of turn
                resetColors();
                mChess.setChessPieces(currentPiece, square); // sets piece to new place in array
                mChess.updateBoard();
                clearSelections();

                if (debug_printing) mChess.debug_printChess();

                // player2_move();
            }
        } else if (currentPiece != null) {
            selectedPiece = currentPiece;

            clearSelections(); // reset selections

            switch (selectedPiece.getPieceName()) { // highlights where the piece can move
                case wPAWN:
                    possibleSelections.add(getSquareView(selectedPiece.getColumn(), selectedPiece.getRow() + 1)); // adds views to an arraylist
                    switch(selectedPiece.getPosition()){
                        case "a2":
                        case "b2":
                        case "c2":
                        case "d2":
                        case "e2":
                        case "f2":
                        case "g2":
                        case "h2":
                            possibleSelections.add(getSquareView(selectedPiece.getColumn(), selectedPiece.getRow() + 2));
                            break;
                    }
                    break;
                case wKNIGHT:
                    possibleSelections.add(getSquareView(selectedPiece.getColumn() + 2, selectedPiece.getRow() + 1));
                    possibleSelections.add(getSquareView(selectedPiece.getColumn() - 2, selectedPiece.getRow() + 1));
                    possibleSelections.add(getSquareView(selectedPiece.getColumn() + 1, selectedPiece.getRow() + 2));
                    possibleSelections.add(getSquareView(selectedPiece.getColumn() - 1, selectedPiece.getRow() + 2));
                    possibleSelections.add(getSquareView(selectedPiece.getColumn() - 2, selectedPiece.getRow() - 1));
                    possibleSelections.add(getSquareView(selectedPiece.getColumn() + 2, selectedPiece.getRow() - 1));
                    possibleSelections.add(getSquareView(selectedPiece.getColumn() - 1, selectedPiece.getRow() - 2));
                    possibleSelections.add(getSquareView(selectedPiece.getColumn() + 1, selectedPiece.getRow() - 2));
                    break;
                case wBISHOP:
                    for(int i = 0; i < 8; i++){
                        possibleSelections.add(getSquareView(selectedPiece.getColumn() + i, selectedPiece.getRow() + i));
                        possibleSelections.add(getSquareView(selectedPiece.getColumn() - i, selectedPiece.getRow() - i));
                        possibleSelections.add(getSquareView(selectedPiece.getColumn() + i, selectedPiece.getRow() - i));
                        possibleSelections.add(getSquareView(selectedPiece.getColumn() - i, selectedPiece.getRow() + i));
                }
                    break;
                case wROOK:
                    for(int i = 0; i < 8; i++){
                        possibleSelections.add(getSquareView(selectedPiece.getColumn(), selectedPiece.getRow() + i));
                        possibleSelections.add(getSquareView(selectedPiece.getColumn(), selectedPiece.getRow() - i));
                        possibleSelections.add(getSquareView(selectedPiece.getColumn() + i, selectedPiece.getRow()));
                        possibleSelections.add(getSquareView(selectedPiece.getColumn() - i, selectedPiece.getRow()));
                    }
                    break;
                case wQUEEN:
                    for(int i = 0; i < 8; i++)
                    {
                        possibleSelections.add(getSquareView(selectedPiece.getColumn() + i, selectedPiece.getRow() + i));
                        possibleSelections.add(getSquareView(selectedPiece.getColumn() - i, selectedPiece.getRow() - i));
                        possibleSelections.add(getSquareView(selectedPiece.getColumn() + i, selectedPiece.getRow() - i));
                        possibleSelections.add(getSquareView(selectedPiece.getColumn() - i, selectedPiece.getRow() + i));
                        possibleSelections.add(getSquareView(selectedPiece.getColumn(), selectedPiece.getRow() + i));
                        possibleSelections.add(getSquareView(selectedPiece.getColumn(), selectedPiece.getRow() - i));
                        possibleSelections.add(getSquareView(selectedPiece.getColumn() + i, selectedPiece.getRow()));
                        possibleSelections.add(getSquareView(selectedPiece.getColumn() - i, selectedPiece.getRow()));
                    }
                    break;
                case wKING:
                    for(int i = 0; i < 2; i++)
                    {
                        possibleSelections.add(getSquareView(selectedPiece.getColumn(), selectedPiece.getRow() + i));
                        possibleSelections.add(getSquareView(selectedPiece.getColumn(), selectedPiece.getRow() - i));
                        possibleSelections.add(getSquareView(selectedPiece.getColumn() + i, selectedPiece.getRow()));
                        possibleSelections.add(getSquareView(selectedPiece.getColumn() - i, selectedPiece.getRow()));
                        possibleSelections.add(getSquareView(selectedPiece.getColumn() + i, selectedPiece.getRow() + i));
                        possibleSelections.add(getSquareView(selectedPiece.getColumn() + i, selectedPiece.getRow() - i));
                        possibleSelections.add(getSquareView(selectedPiece.getColumn() - i, selectedPiece.getRow() + i));
                        possibleSelections.add(getSquareView(selectedPiece.getColumn() - i, selectedPiece.getRow() - i));
                    }
            } // TODO: Add rules for all the other pieces

            for (View selection : possibleSelections) { // sets all views in the arraylist to be a certain color and selectable
                if (selection != null) {
                    int[] currentNums = Chess.getNumsfromID(getIDfromView(selection));
                    if(mChess.getPiece(currentNums[0], currentNums[1]) == null){
                        setColor(selection, possibleSelectColor);
                        selection.setTag("possibleMove");
                    }
                }
            }
        } else {
            Log.wtf("wtf", "help");
        }
    }

    @SuppressLint("DiscouragedApi")
    public ImageView getSquareView(int x, int y) { // gets the square from activity main, used for adding all views to the array
        if (x < 8 && y < 8)
            return findViewById(getResources().getIdentifier(Chess.getIDfromNums(x, y), "id", getPackageName()));
        else return null;
    }

    private void newGame(View view) { // starts new game
        resetColors();
        mChess.newGame();
        if (debug_printing) mChess.debug_printChess();
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

    public void clearSelections() { // clears selections array and resets all tags
        for (View view : possibleSelections) {
            if (view != null) view.setTag(null);
        }
        possibleSelections.clear();
    }

    private void player2_move(){
        Random randy = new Random();

        int randyX = randy.nextInt(8);
        int randyY = randy.nextInt(8);
        ChessPiece randyPiece = mChess.getPiece(randyX, randyY);

        if(randyPiece != null && randyPiece.getPieceColor() == R.color.black){
            View currentView = getSquareView(randyPiece.getX(), randyPiece.getY());
            setColor(currentView, R.color.red);
            //selectSquare(currentView);
        }else{
            player2_move();
        }
    }

    public String getIDfromView(View view){
        String viewString = view.toString();
        return viewString.substring(viewString.lastIndexOf("app:id/") + 7, viewString.length() - 1);
    }
}