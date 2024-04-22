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
import android.widget.Toast;

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
    final ArrayList<View> possibleSelections = new ArrayList<>();

    // Defines colors for the selection, we should probably change these since I just chose them since they were easy to write
    final int pieceSelectColor = R.color.yellow;
    final int possibleSelectColor = R.color.blue;
    final int checkColor = R.color.red;
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
                        .setMessage(getString(R.string.check_exit))
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.yes), (dialog, which) -> requireActivity().finish())
                        .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel());

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

        if (getArguments() != null) {
            Toast.makeText(getContext(), "Loading game...", Toast.LENGTH_SHORT).show();
            loadGame();
        } else {
            newGame();
        }

        Log.d("wKing", wKing.toString());
        Log.d("bKing", bKing.toString());

        if (debug_printing) mChess.debug_printChess();

        return rootView;
    }

    private void newGame() { // I'm tired of typing null
        newGame(null);
    }
    private void newGame(View view) { // starts new game
        resetColors();
        possibleSelections.clear();
        mChess.newGame();
        selectedPiece = null;
        p2turn = false;
        isGameOver = false;
        if (debug_printing) mChess.debug_printChess();

        wKing = getPieceFromPos(4, 0);
        bKing = getPieceFromPos(4, 7);
    }

    private void loadGame(){
        loadGame(null);
    }
    private void loadGame(View view) { // starts new game
        resetColors();
        possibleSelections.clear();
        mChess.setBoard();
        selectedPiece = null;
        p2turn = false;
        isGameOver = false;
        if (debug_printing) mChess.debug_printChess();

        wKing = null;
        bKing = null;

        for (int i = 0; i < 8; i++) { // can't just assign based on position so searches array
            for (int j = 0; j < 8; j++) {
                if (isPieceOnView(getViewFromPos(i, j))) {
                    ChessPiece piece = getPieceFromPos(i, j);
                    if (piece.getPieceName() == Chess.pieceName.wKING) {
                        wKing = piece;
                    }
                    if (piece.getPieceName() == Chess.pieceName.bKING) {
                        bKing = piece;
                    }
                }
            }
        }

        if (wKing == null || bKing == null) {
            Toast.makeText(getContext(), R.string.prev_game_ended, Toast.LENGTH_LONG).show();
            newGame();
        } else {
            Toast.makeText(getContext(), R.string.prev_game_loaded, Toast.LENGTH_SHORT).show();
        }
    }

    private void selectSquare(View view) { // code for when a square is tapped
        if (!isGameOver) {
            resetColors();

            String square = getIDfromView(view);
            //Log.d("square", square); // prints ID of square selected

            ChessPiece currentPiece = mChess.getPiece(square);

            if (possibleSelections.contains(view)) { // if previous selection exists, then if the square is within possibleSelectionsFinal

                // Game over code
                if (currentPiece != null && (currentPiece.getPieceName() == Chess.pieceName.wKING || currentPiece.getPieceName() == Chess.pieceName.bKING)) {
                    Log.d("game over", "game over");

                    AlertDialog.Builder game_over_alert = new AlertDialog.Builder(getContext())
                            .setCancelable(true)
                            .setPositiveButton(getString(R.string.okay), null);

                    String win = "";
                    if (currentPiece.getPieceName() == Chess.pieceName.wKING) {
                        // black wins
                        Log.d("black win", "black win");
                        win = getResources().getStringArray(R.array.piece_colors)[1];
                        Storage.upCount("lose");
                    } else if (currentPiece.getPieceName() == Chess.pieceName.bKING) {
                        // white wins
                        Log.d("white win", "white win");
                        win = getResources().getStringArray(R.array.piece_colors)[0];
                        Storage.upCount("win");
                    } else {
                        Log.wtf("help", "Oh god how did this even happen? Is it a tie or something?");
                    }

                    isGameOver = true;
                    game_over_alert.setMessage(getString(R.string.checkmate, win));
                    game_over_alert.show();
                }

                mChess.setChessPieces(null, selectedPiece.getColumn(), selectedPiece.getRow()); // removes from array

                currentPiece = selectedPiece; // copies old piece into new location
                currentPiece.setPosition(square); // moves copy onto new position in piece data

                //Makes the number go up of the moved piece
                Storage.upCount(currentPiece.getPieceName().toString());

                selectedPiece = null; // removes old piece (either other color or none) from board

                // end of turn
                resetColors();
                mChess.setChessPieces(currentPiece, square); // sets piece to new place in array
                mChess.updateBoard();
                possibleSelections.clear();
                isKingChecked();
                Storage.saveBoard();
                //Log.w("board", Storage.getString("Board"));
                if (isGameOver) return;

                if (debug_printing) mChess.debug_printChess();

                p2turn = !p2turn;
                if (p2turn) player2_move();
            } else if (currentPiece != null) {
                setColor(getViewFromPiece(currentPiece), pieceSelectColor); // sets square color to yellow
                possibleSelections.clear();
                pieceSelection(currentPiece);
            } else {
                possibleSelections.clear();
                //Log.wtf("wtf", "help");
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

    // player 2 movement method
    // generates random numbers and checks if that square has a valid piece, otherwise runs again
    // once it finds a valid piece, it chooses a random possible selection, and moves there.
    private void player2_move() {
        //selectSquare(getViewFromPos(0, 7));
        Random randy = new Random();

        int randyX = randy.nextInt(8);
        int randyY = randy.nextInt(8);
        ChessPiece randyPiece = mChess.getPiece(randyX, randyY);

        if (randyPiece != null && randyPiece.getPieceColor() == R.color.black) {
            View currentView = getViewFromPiece(randyPiece);
            //setColor(currentView, R.color.red);
            selectSquare(currentView);

            if (!possibleSelections.isEmpty()) { // if there are possible moves
                Log.d("p2", "p2 move");

                int rand = randy.nextInt(possibleSelections.size());
                selectSquare(possibleSelections.get(rand));
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
    public void pieceSelection(ChessPiece currentPiece) {
        selectedPiece = currentPiece;

        Log.d("selected", String.valueOf(selectedPiece));

        possibleSelections.clear(); // reset selections

        ArrayList<View> piecePath = pieceSelectionPath(selectedPiece);

        for (View selection : piecePath) { // sets all views in the arraylist to be a certain color and selectable
            if (selection != null) {
                if (!isPieceOnView(selection) || getPieceFromView(selection).getPieceColor() != selectedPiece.getPieceColor()) {
                    setColor(selection, possibleSelectColor);
                    possibleSelections.add(selection);
                }
            }
        }
    }

    public ArrayList<View> pieceSelectionPath(ChessPiece pieceToFindPath) {
        if (pieceToFindPath == null) return null;

        ArrayList<View> viewsSelection = new ArrayList<>();

        int x = pieceToFindPath.getX();
        int y = pieceToFindPath.getY();

        switch (pieceToFindPath.getPieceName()) { // highlights where the piece can move
            case wPAWN:
                int wMovement = 1;
                if (y == 1) wMovement = 2;
                for (int i = 1; i <= wMovement; i++) {
                    if (getPieceFromPos(x, y + i) == null) {
                        viewsSelection.add(getViewFromPos(x, y + i));
                    } else break;
                }

                ChessPiece[] wPiecesToKill = {getPieceFromPos(x + 1, y + 1), getPieceFromPos(x - 1, y + 1)};
                for (ChessPiece piece : wPiecesToKill) {
                    if (piece != null)
                        viewsSelection.add(getViewFromPos(piece.getX(), piece.getY()));
                }
                break;
            case bPAWN:
                int bMovement = 1;
                if (y == 6) bMovement = 2;
                for (int i = 1; i <= bMovement; i++) {
                    if (getPieceFromPos(x, y - i) == null) {
                        viewsSelection.add(getViewFromPos(x, y - i));
                    } else break;
                }

                ChessPiece[] bPiecesToKill = {getPieceFromPos(x + 1, y - 1), getPieceFromPos(x - 1, y - 1)};
                for (ChessPiece piece : bPiecesToKill) {
                    if (piece != null)
                        viewsSelection.add(getViewFromPos(piece.getX(), piece.getY()));
                }
                break;
            case wKNIGHT:
            case bKNIGHT:
                for (int i = -2; i <= 2; i++) {
                    int j = 0;
                    if (i == 0) continue;
                    if (Math.abs(i) == 2) j = 1;
                    if (Math.abs(i) == 1) j = 2;
                    viewsSelection.add(getViewFromPos(x - i, y - j));
                    viewsSelection.add(getViewFromPos(x - i, y + j));
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

                    int currentIndex = possible.indexOf(getViewFromPiece(pieceToFindPath));

                    int a = currentIndex - 1;
                    int b = currentIndex + 1;

                    while (a > 0 && !isPieceOnView(possible.get(a))) {
                        viewsSelection.add(possible.get(a));
                        a--;
                    }
                    while (b < possible.size() && !isPieceOnView(possible.get(b))) {
                        viewsSelection.add(possible.get(b));
                        b++;
                    }

                    if (a >= 0) viewsSelection.add(possible.get(a));
                    if (b < possible.size()) viewsSelection.add(possible.get(b));
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

                    int currentIndex = possible.indexOf(getViewFromPiece(pieceToFindPath));

                    int a = currentIndex - 1;
                    int b = currentIndex + 1;

                    while (a > 0 && !isPieceOnView(possible.get(a))) {
                        viewsSelection.add(possible.get(a));
                        a--;
                    }
                    while (b < possible.size() && !isPieceOnView(possible.get(b))) {
                        viewsSelection.add(possible.get(b));
                        b++;
                    }

                    if (a >= 0) viewsSelection.add(possible.get(a));
                    if (b < possible.size()) viewsSelection.add(possible.get(b));
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

                    int currentIndex = possible.indexOf(getViewFromPiece(pieceToFindPath));

                    int a = currentIndex - 1;
                    int b = currentIndex + 1;

                    while (a > 0 && !isPieceOnView(possible.get(a))) {
                        viewsSelection.add(possible.get(a));
                        a--;
                    }
                    while (b < possible.size() && !isPieceOnView(possible.get(b))) {
                        viewsSelection.add(possible.get(b));
                        b++;
                    }

                    if (a >= 0) viewsSelection.add(possible.get(a));
                    if (b < possible.size()) viewsSelection.add(possible.get(b));
                }
                break;
            case wKING:
            case bKING:
                for (int offsetX = -1; offsetX <= 1; offsetX++) {
                    for (int offsetY = -1; offsetY <= 1; offsetY++) {
                        viewsSelection.add(getViewFromPos(x + offsetX, y + offsetY));
                    }
                }
                break;
        }

        return viewsSelection;
    }

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

    public void isKingChecked() {
        //Use getPieceFromView to find what piece is in the sight of the king,
        //then pull the possible selection of that piece to make sure it can check the king.
        ChessPiece[] kings = new ChessPiece[]{wKing, bKing};//Creates an array of the two kings
        for (ChessPiece king : kings) {
            boolean inCheck = false;

            int x = king.getX();
            int y = king.getY();

            ArrayList<View> possibleAttackers = new ArrayList<>();
            ArrayList<View> possibleKnightSpaces = new ArrayList<>(); // Knights don't follow normal movement

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

                int currentIndex = possible.indexOf(getViewFromPiece(king));

                int a = currentIndex - 1;
                int b = currentIndex + 1;

                while (a > 0 && !isPieceOnView(possible.get(a))) {
                    //possibleAttackers.add(possible.get(a));
                    a--;
                }
                while (b < possible.size() && !isPieceOnView(possible.get(b))) {
                    //possibleAttackers.add(possible.get(b));
                    b++;
                }

                if (a >= 0) {
                    possibleAttackers.add(possible.get(a));
                    //setColor(possible.get(a), R.color.brown);
                }
                if (b < possible.size()) {
                    possibleAttackers.add(possible.get(b));
                    //setColor(possible.get(b), R.color.brown);
                }
            }

            for (int i = -2; i <= 2; i++) {
                int j = 0;
                if (i == 0) continue;
                if (Math.abs(i) == 2) j = 1;
                if (Math.abs(i) == 1) j = 2;
                possibleKnightSpaces.add(getViewFromPos(x - i, y - j));
                possibleKnightSpaces.add(getViewFromPos(x - i, y + j));
            }

            for (View attackers : possibleAttackers) {
                if (isPieceOnView(attackers) && getPieceFromView(attackers).getPieceColor() != king.getPieceColor()) {
                    ArrayList<View> possibleAttackerPath = pieceSelectionPath(getPieceFromView(attackers));
                    if (possibleAttackerPath.contains(getViewFromPiece(king))) {
                        setColor(attackers, checkColor);
                        inCheck = true;
                    }
                }
            }

            for (View knightSpace : possibleKnightSpaces) {
                if (isPieceOnView(knightSpace) &&
                        (getPieceFromView(knightSpace).getPieceName() == Chess.pieceName.wKNIGHT || getPieceFromView(knightSpace).getPieceName() == Chess.pieceName.bKNIGHT)) {
                    if (getPieceFromView(knightSpace).getPieceColor() != king.getPieceColor()) {
                        setColor(knightSpace, checkColor);
                        inCheck = true;
                    }
                }
            }

            if (inCheck) {
                setColor(getViewFromPiece(king), checkColor);
                if (!p2turn) { // Toast should only go off once
                    Log.d("makeToast", "toast generated");
                    Toast.makeText(getContext(), R.string.check, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Gets the view that a specified piece is in
    public View getViewFromPiece(ChessPiece piece) {
        return getViewFromPos(piece.getX(), piece.getY());
    }

    public boolean isPieceOnView(View view) { // checks if a certain view has a piece or not
        return view != null && getPieceFromView(view) != null;
    }
}