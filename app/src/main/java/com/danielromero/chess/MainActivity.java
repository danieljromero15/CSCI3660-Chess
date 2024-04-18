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
    boolean debug_printing = false;

    Chess mChess;
    ChessPiece selectedPiece = null;
    ArrayList<View> possibleSelections = new ArrayList<>();

    // Defines colors for the selection, we should probably change these since I just chose them since they were easy to write
    final int pieceSelectColor = R.color.yellow;
    final int possibleSelectColor = R.color.blue;
    boolean p2turn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChess = new Chess();

        Button new_game = findViewById(R.id.new_game_button);
        new_game.setOnClickListener(this::newGame); // sets New Game button

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                mChess.setChessSquare(getViewFromPos(i, j), i, j); // assigns imageview widgets to the array backend
                mChess.getChessSquare(i, j).setOnClickListener(this::selectSquare); // makes each tile clickable
                getViewFromPos(i, j).setTag(null); // Shouldn't need this but it's there just in case
            }
        }

        //Initialized storage and leaves a test message in log to see if it works
        // !! Log will only work on my phone rn because I was testing persistance
        Storage.make(this.getApplicationContext());
        //Log.w( "please",Storage.getString("test", "defaultValue"));

        newGame(null);

        if (debug_printing) mChess.debug_printChess();
    }

    private void newGame(View view) { // starts new game
        resetColors();
        clearSelections();
        mChess.newGame();
        selectedPiece = null;
        p2turn = false;
        if (debug_printing) mChess.debug_printChess();
    }

    private void selectSquare(View view) { // code for when a square is tapped
        resetColors();
        setColor(view, pieceSelectColor); // sets square color to yellow

        String square = getIDfromView(view);
        //Log.d("square", square); // prints ID of square selected
        //Log.d("withinBounds", String.valueOf(isWithinBoard(Chess.getNumsfromID(square)[0], Chess.getNumsfromID(square)[1])));

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

                //player2_move();

            } else clearSelections();
        } else if (currentPiece != null) {
            selectedPiece = currentPiece;

            Log.d("selected", String.valueOf(selectedPiece));

            clearSelections(); // reset selections
            int x = selectedPiece.getX();
            int y = selectedPiece.getY();

            switch (selectedPiece.getPieceName()) { // highlights where the piece can move
                case wPAWN:
                    int movement = 1;
                    if (y == 1) movement = 2;
                    for (int i = 1; i <= movement; i++) {
                        if (getPieceFromPos(x, y + i) == null) {
                            possibleSelections.add(getViewFromPos(x, y + i));
                        } else break;
                    }

                    ChessPiece[] piecesToKill = {getPieceFromPos(x + 1, y + 1), getPieceFromPos(x - 1, y + 1)};
                    for (ChessPiece piece : piecesToKill) {
                        if (piece != null)
                            possibleSelections.add(getViewFromPos(piece.getX(), piece.getY()));
                    }
                    break;
                case wKNIGHT:
                    for (int i = -2; i <= 2; i++) {
                        int j = 0;
                        if (i == 0) continue;
                        if (Math.abs(i) == 2) j = 1;
                        if (Math.abs(i) == 1) j = 2;
                        possibleSelections.add(getViewFromPos(x - i, y - j));
                        possibleSelections.add(getViewFromPos(x - i, y + j));
                    }
                    break;
                case wBISHOP:
                    // I hate this but it works so well
                    for (int i = 0; i < 2; i++) {
                        ArrayList<View> possible = new ArrayList<>();
                        for (int j = -8; j < 8; j++) {
                            int posX;
                            int posY = y + j;

                            if (i == 0) posX = x + j;
                            else posX = x - j;

                            if (isWithinBoard(posX, posY)) {
                                View tempView = getViewFromPos(posX, posY);
                                possible.add(tempView);
                            }
                        }

                        int currentIndex = possible.indexOf(getViewFromPiece(currentPiece));

                        int a = currentIndex - 1;
                        int b = currentIndex + 1;

                        while (a > 0 && getPieceFromView(possible.get(a)) == null) {
                            possibleSelections.add(possible.get(a));
                            a--;
                        }
                        while (b < possible.size() && getPieceFromView(possible.get(b)) == null) {
                            possibleSelections.add(possible.get(b));
                            b++;
                        }

                        if (a >= 0) possibleSelections.add(possible.get(a));
                        if (b < possible.size()) possibleSelections.add(possible.get(b));
                    }

                    break;
                case wROOK:
                    for (int i = 0; i < 8; i++) {
                        possibleSelections.add(getViewFromPos(x, y + i));
                        possibleSelections.add(getViewFromPos(x, y - i));
                        possibleSelections.add(getViewFromPos(x + i, y));
                        possibleSelections.add(getViewFromPos(x - i, y));
                    }
                    break;
                case wQUEEN:
                    for (int i = 0; i < 8; i++) {
                        possibleSelections.add(getViewFromPos(x + i, y + i));
                        possibleSelections.add(getViewFromPos(x - i, y - i));
                        possibleSelections.add(getViewFromPos(x + i, y - i));
                        possibleSelections.add(getViewFromPos(x - i, y + i));
                        possibleSelections.add(getViewFromPos(x, y + i));
                        possibleSelections.add(getViewFromPos(x, y - i));
                        possibleSelections.add(getViewFromPos(x + i, y));
                        possibleSelections.add(getViewFromPos(x - i, y));
                    }
                    break;
                case wKING:
                    for (int i = 0; i < 2; i++) {
                        possibleSelections.add(getViewFromPos(x, y + i));
                        possibleSelections.add(getViewFromPos(x, y - i));
                        possibleSelections.add(getViewFromPos(x + i, y));
                        possibleSelections.add(getViewFromPos(x - i, y));
                        possibleSelections.add(getViewFromPos(x + i, y + i));
                        possibleSelections.add(getViewFromPos(x + i, y - i));
                        possibleSelections.add(getViewFromPos(x - i, y + i));
                        possibleSelections.add(getViewFromPos(x - i, y - i));
                    }
                    break;
                default:
                    clearSelections();
                    selectedPiece = null;
                    return;
            } // TODO: Add rules for all the other pieces

            for (View selection : possibleSelections) { // sets all views in the arraylist to be a certain color and selectable
                if (selection != null) {
                    if (getPieceFromView(selection) == null || getPieceFromView(selection).getPieceColor() != selectedPiece.getPieceColor()) {
                        setColor(selection, possibleSelectColor);
                        selection.setTag("possibleMove");
                    }
                }
            }
        } else {
            clearSelections();
            Log.wtf("wtf", "help");
        }
    }

    @SuppressLint("DiscouragedApi")
    public ImageView getViewFromPos(int x, int y) { // gets the square from activity main, used for adding all views to the array
        if (x < 8 && y < 8)
            return findViewById(getResources().getIdentifier(Chess.getIDfromNums(x, y), "id", getPackageName()));
        else return null;
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

    private void player2_move() {
        p2turn = true;
        Log.d("p2", "p2 move");
        selectSquare(getViewFromPos(0, 7));
        /*Random randy = new Random();

        int randyX = randy.nextInt(8);
        int randyY = randy.nextInt(8);
        ChessPiece randyPiece = mChess.getPiece(randyX, randyY);

        if (randyPiece != null && randyPiece.getPieceColor() == R.color.black) {
            View currentView = getViewFromPos(randyPiece.getX(), randyPiece.getY());
            setColor(currentView, R.color.red);
            //selectSquare(currentView);
        } else {
            player2_move();
        }*/
        p2turn = false;
    }

    public static String getIDfromView(View view) {
        String viewString = view.toString();
        return viewString.substring(viewString.lastIndexOf("app:id/") + 7, viewString.length() - 1);
    }

    public ChessPiece getPieceFromView(View view) {
        int[] currentNums = Chess.getNumsfromID(getIDfromView(view));
        return mChess.getPiece(currentNums[0], currentNums[1]);
    }

    public ChessPiece getPieceFromPos(int x, int y) {
        try {
            View square = getViewFromPos(x, y);
            return getPieceFromView(square);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public boolean isWithinBoard(int x, int y) {
        if (x < 0) return false;
        if (y < 0) return false;
        if (x > 7) return false;
        return y <= 7;
    }

    public View getViewFromPiece(ChessPiece piece) {
        int x = piece.getX();
        int y = piece.getY();
        return getViewFromPos(x, y);
    }
}