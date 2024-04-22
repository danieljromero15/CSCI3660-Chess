package com.danielromero.chess;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class GameFragment extends Fragment {
    private View rootView;
    // for logging board state after each turn
    final boolean debug_printing = false;
    public ChessPiece wKing;
    public ChessPiece bKing;
    Chess mChess;
    ChessPiece selectedPiece = null;
    ArrayList<View> possibleSelections = new ArrayList<>();
    ArrayList<View> possibleSelectionsFinal = new ArrayList<>();

    // Defines colors for the selection, we should probably change these since I just chose them since they were easy to write
    final int pieceSelectColor = R.color.yellow;
    final int possibleSelectColor = R.color.blue;
    // defines whether or not it's the second player's turn
    boolean p2turn;
    boolean isGameOver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_game, container, false);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //Log.w("back", "back pressed in fragment " + getParentFragment());
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext())
                        .setMessage("Are you sure you want to exit?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", (dialog, which) -> requireActivity().finish())
                        .setNegativeButton("No", (dialog, which) -> dialog.cancel());

                alertBuilder.show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);


        mChess = new Chess();

        Button new_game = rootView.findViewById(R.id.new_game_button);
        new_game.setOnClickListener(this::newGame); // sets New Game button

        Button load_game = rootView.findViewById(R.id.load_game_button);
        load_game.setOnClickListener(this::loadGame); // sets New Game button

        // sets listeners to each tile
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                mChess.setChessSquare(getViewFromPos(i, j), i, j); // assigns imageview widgets to the array backend
                mChess.getChessSquare(i, j).setOnClickListener(this::selectSquare); // makes each tile clickable
                getViewFromPos(i, j).setTag(null); // Shouldn't need this but it's there just in case
            }
        }

        //Initialized storage and leaves a test message in log to see if it works
        Storage.make(requireActivity().getApplicationContext(), mChess);

        newGame(null);

        // defines kings
        wKing = getPieceFromPos(4, 0);
        bKing = getPieceFromPos(4, 7);

        Log.d("wKing", wKing.toString());
        Log.d("bKing", bKing.toString());

        if (debug_printing) mChess.debug_printChess();

        return rootView;
    }

    private void newGame(View view) { // starts new game
        resetColors();
        clearSelections();
        mChess.newGame();
        selectedPiece = null;
        p2turn = false;
        isGameOver = false;
        if (debug_printing) mChess.debug_printChess();
    }

    private void loadGame(View view) { // starts new game
        resetColors();
        clearSelections();
        mChess.setBoard();
        selectedPiece = null;
        p2turn = false;
        isGameOver = false;
        if (debug_printing) mChess.debug_printChess();
    }

    private void selectSquare(View view) { // code for when a square is tapped
        if (!isGameOver) {
            resetColors();
            setColor(view, pieceSelectColor); // sets square color to yellow

            String square = getIDfromView(view);
            //Log.d("square", square); // prints ID of square selected
            //Log.d("withinBounds", String.valueOf(isWithinBoard(Chess.getNumsfromID(square)[0], Chess.getNumsfromID(square)[1])));

            ChessPiece currentPiece = mChess.getPiece(square);

            if (selectedPiece != null && (currentPiece == null || selectedPiece.getPieceColor() != currentPiece.getPieceColor())) { // if previous selection exists, then if the square is either empty or a different color than previous selection
                if ((view.getTag() != null) && (view.getTag() == "possibleMove")) { // if it has a tag and the tag is possibleMove

                    // Game over code
                    if (currentPiece != null && (currentPiece.getPieceName() == Chess.pieceName.wKING || currentPiece.getPieceName() == Chess.pieceName.bKING)) {
                        Log.d("game over", "game over");

                        AlertDialog.Builder game_over_alert = new AlertDialog.Builder(getContext())
                                .setCancelable(true)
                                .setPositiveButton("Okay", null); // set gameover var here

                        String win = "";
                        if (currentPiece.getPieceName() == Chess.pieceName.wKING) {
                            // black wins
                            win = getResources().getStringArray(R.array.piece_colors)[1];
                        } else if (currentPiece.getPieceName() == Chess.pieceName.bKING) {
                            // white wins
                            win = getResources().getStringArray(R.array.piece_colors)[0];
                        } else {
                            Log.wtf("help", "Oh god how did this even happen? Is it a tie or something?");
                        }

                        isGameOver = true;
                        game_over_alert.setMessage(getString(R.string.game_over, win));
                        game_over_alert.show();
                    }

                    mChess.setChessPieces(null, selectedPiece.getColumn(), selectedPiece.getRow()); // removes from array

                    currentPiece = selectedPiece; // copies old piece into new location
                    currentPiece.setPosition(square); // moves copy onto new position in piece data

                    //Makes the number go up of the moved piece
                    if (selectedPiece != null) {
                        Storage.upCount(selectedPiece.getPieceName().toString());
                    }

                    selectedPiece = null; // removes old piece (either other color or none) from board

                    // end of turn
                    resetColors();
                    mChess.setChessPieces(currentPiece, square); // sets piece to new place in array
                    mChess.updateBoard();
                    clearSelections();
                    Storage.saveBoard();
                    Log.w("board", Storage.getString("Board"));
                    if(isGameOver) return;

                    if (debug_printing) mChess.debug_printChess();

                    p2turn = !p2turn;
                    if (p2turn) player2_move();

                    // Testing some stuff out. This code takes the piece at 0, 1 (the pawn at a2) and prints out the selections.
                    // In theory, we could take this code, use it for the king with the path of a queen, and check every tile to see if there's a piece, then test that piece's possible selections to see if a king is there.
                    // It would be a bit convoluted but it's just an idea.
                /*
                pieceSelectionPath(getPieceFromPos(0, 1));
                for (View view1 : possibleSelectionsFinal) {
                    try {
                        Log.d("path", getIDfromView(view1));
                    } catch (NullPointerException e) {
                        Log.w("path", "null");
                    }
                }
                clearSelections();
                */

                } else clearSelections();
            } else if (currentPiece != null) {
                pieceSelectionPath(currentPiece);
            } else {
                clearSelections();
                Log.wtf("wtf", "help");
            }
        }
    }

    // awful method using a discouraged API but it works well so I don't wanna touch it
    @SuppressLint("DiscouragedApi")
    public ImageView getViewFromPos(int x, int y) { // gets the square from activity main, used for adding all views to the array
        if (x < 8 && y < 8)
            return rootView.findViewById(getResources().getIdentifier(Chess.getIDfromNums(x, y), "id", requireActivity().getPackageName()));
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
        if (view != null) view.setBackgroundColor(ContextCompat.getColor(requireActivity(), color));
    }

    public void clearSelections() { // clears selections arrays and resets all tags
        for (View view : possibleSelections) {
            if (view != null) view.setTag(null);
        }
        possibleSelections.clear();
        possibleSelectionsFinal.clear();
    }

    // player 2 movement method
    // generates random numbers and checks if that square has a valid piece, otherwise runs again
    // once it finds a valid piece, it chooses a random possible selection, and moves there.
    private void player2_move() {
        Log.d("p2", "p2 move");
        //selectSquare(getViewFromPos(0, 7));
        Random randy = new Random();

        int randyX = randy.nextInt(8);
        int randyY = randy.nextInt(8);
        ChessPiece randyPiece = mChess.getPiece(randyX, randyY);

        if (randyPiece != null && randyPiece.getPieceColor() == R.color.black) {
            View currentView = getViewFromPiece(randyPiece);
            //setColor(currentView, R.color.red);
            selectSquare(currentView);

            if (!possibleSelectionsFinal.isEmpty()) { // if there are possible moves
                int rand = randy.nextInt(possibleSelectionsFinal.size());
                selectSquare(possibleSelectionsFinal.get(rand));
            } else {
                player2_move(); // if there are no possible moves with that piece run again
            }
        } else {
            player2_move();
        }
        p2turn = false;
    }

    // Checks if a certain position is within the appropriate bounds of a Chess board, and returns a boolean value
    public boolean isWithinBoard(int x, int y) {
        if (x < 0) return false;
        if (y < 0) return false;
        if (x > 7) return false;
        return y <= 7;
    }

    // Selection path code (the thing that shows the colored path of possible locations)
    public void pieceSelectionPath(ChessPiece currentPiece) {
        selectedPiece = currentPiece;

        Log.d("selected", String.valueOf(selectedPiece));

        clearSelections(); // reset selections
        int x = selectedPiece.getX();
        int y = selectedPiece.getY();

        switch (selectedPiece.getPieceName()) { // highlights where the piece can move
            case wPAWN:
                int wMovement = 1;
                if (y == 1) wMovement = 2;
                for (int i = 1; i <= wMovement; i++) {
                    if (getPieceFromPos(x, y + i) == null) {
                        possibleSelections.add(getViewFromPos(x, y + i));
                    } else break;
                }

                ChessPiece[] wPiecesToKill = {getPieceFromPos(x + 1, y + 1), getPieceFromPos(x - 1, y + 1)};
                for (ChessPiece piece : wPiecesToKill) {
                    if (piece != null)
                        possibleSelections.add(getViewFromPos(piece.getX(), piece.getY()));
                }
                break;
            case bPAWN:
                int bMovement = 1;
                if (y == 6) bMovement = 2;
                for (int i = 1; i <= bMovement; i++) {
                    if (getPieceFromPos(x, y - i) == null) {
                        possibleSelections.add(getViewFromPos(x, y - i));
                    } else break;
                }

                ChessPiece[] bPiecesToKill = {getPieceFromPos(x + 1, y - 1), getPieceFromPos(x - 1, y - 1)};
                for (ChessPiece piece : bPiecesToKill) {
                    if (piece != null)
                        possibleSelections.add(getViewFromPos(piece.getX(), piece.getY()));
                }
                break;
            case wKNIGHT:
            case bKNIGHT:
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
            case bBISHOP:
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
            case bROOK:
                // this is just the same code as bishop lmao
                for (int i = 0; i < 2; i++) {
                    ArrayList<View> possible = new ArrayList<>();
                    for (int j = -8; j < 8; j++) {
                        int posX;
                        int posY;

                        if (i == 0) {
                            posX = x;
                            posY = y + j;
                        } else {
                            posX = x + j;
                            posY = y;
                        }

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
            case wQUEEN:
            case bQUEEN:
                for (int i = 0; i < 4; i++) {
                    ArrayList<View> possible = new ArrayList<>();
                    for (int j = -8; j < 8; j++) {
                        int posX = x;
                        int posY = y;

                        switch (i) {
                            case 0: // rook code
                                //posX = x;
                                posY = y + j;
                                break;
                            case 1: // rook code
                                posX = x + j;
                                //posY = y;
                                break;
                            case 2: // bishop code
                                posX = x + j;
                                posY = y + j;
                                break;
                            case 3: // bishop code
                                posX = x - j;
                                posY = y + j;
                                break;
                        }

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
            case wKING:
            case bKING:
                for (int offsetX = -1; offsetX <= 1; offsetX++) {
                    for (int offsetY = -1; offsetY <= 1; offsetY++) {
                        possibleSelections.add(getViewFromPos(x + offsetX, y + offsetY));
                    }
                }
                break;
            default:
                clearSelections();
                selectedPiece = null;
                return;
        }

        for (View selection : possibleSelections) { // sets all views in the arraylist to be a certain color and selectable
            if (selection != null) {
                if (getPieceFromView(selection) == null || getPieceFromView(selection).getPieceColor() != selectedPiece.getPieceColor()) {
                    setColor(selection, possibleSelectColor);
                    selection.setTag("possibleMove");
                    possibleSelectionsFinal.add(selection);
                }
            }
        }
    }

    //
    // Getter methods
    //

    // gets the ID of a certain view, ex. "a1" or "e5"
    public static String getIDfromView(View view) {
        String viewString = view.toString();
        return viewString.substring(viewString.lastIndexOf("app:id/") + 7, viewString.length() - 1);
    }

    // gets the ChessPiece object that is currently in a specified view
    public ChessPiece getPieceFromView(View view) {
        int[] currentNums = Chess.getNumsfromID(getIDfromView(view));
        return mChess.getPiece(currentNums[0], currentNums[1]);
    }

    // gets the ChessPiece object that is at a specified position
    public ChessPiece getPieceFromPos(int x, int y) {
        try {
            View square = getViewFromPos(x, y);
            return getPieceFromView(square);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public boolean isKingChecked() {
        //Use getPieceFromView to find what piece is in the sight of the king,
        //then pull the possible selection of that piece to make sure it can check the king.
        ChessPiece[] kings = new ChessPiece[]{wKing, bKing};//Creates an array of the two kings
        for (ChessPiece king : kings) {
            //Checks a queens movement from the king to see if there is a piece in sight
            for (int i = 0; i < 4; i++) {
                ArrayList<View> possible = new ArrayList<>();
                for (int j = -8; j < 8; j++) {
                    int posX = king.getX();
                    int posY = king.getY();

                    switch (i) {
                        case 0: // rook code
                            //posX = x;
                            posY = king.getY() + j;
                            break;
                        case 1: // rook code
                            posX = king.getX() + j;
                            //posY = y;
                            break;
                        case 2: // bishop code
                            posX = king.getX() + j;
                            posY = king.getY() + j;
                            break;
                        case 3: // bishop code
                            posX = king.getX() - j;
                            posY = king.getY() + j;
                            break;
                    }

                    if (isWithinBoard(posX, posY)) {
                        View tempView = getViewFromPos(posX, posY);
                        possible.add(tempView);
                    }
                }

                int currentIndex = possible.indexOf(getViewFromPiece(king));

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
            return true;
        }
        return false;
    }
        // Gets the view that a specified piece is in
        public View getViewFromPiece (ChessPiece piece){
            return getViewFromPos(piece.getX(), piece.getY());
        }
}