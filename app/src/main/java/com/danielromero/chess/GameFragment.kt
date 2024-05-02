package com.danielromero.chess

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.danielromero.chess.Chess.pieceName
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Random
import kotlin.math.abs

class GameFragment : Fragment() {
    private lateinit var rootView: View

    // for logging board state after each turn
    val debug_printing: Boolean = true
    var wKing: ChessPiece? = null
    var bKing: ChessPiece? = null
    private var wKingMove = 0
    lateinit var mChess: Chess
    var selectedPiece: ChessPiece? = null
    val possibleSelections: ArrayList<View?> = ArrayList()

    // Defines colors for the selection, we should probably change these since I just chose them since they were easy to write
    val pieceSelectColor: Int = R.color.yellow
    val possibleSelectColor: Int = R.color.blue
    val checkColor: Int = R.color.red

    // defines whether or not it's the second player's turn
    var p2turn: Boolean = false
    var isGameOver: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_game, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            val alertBuilder =
                MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme).setTitle(
                    getString(R.string.check_exit)
                ).setCancelable(true)
                    .setPositiveButton(getString(R.string.yes)) { dialog: DialogInterface?, which: Int -> requireActivity().finish() }
                    .setNegativeButton(getString(R.string.no)) { dialog: DialogInterface, which: Int -> dialog.cancel() }

            alertBuilder.show()
        }


        mChess = Chess()

        val new_game = rootView.findViewById<Button>(R.id.new_game_button)
        new_game.setOnClickListener { view -> this.newGame(view) } // sets New Game button

        val load_game = rootView.findViewById<Button>(R.id.load_game_button)
        load_game.setOnClickListener { view -> this.loadGame(view) } // sets New Game button

        // sets listeners to each tile
        for (i in 0..7) {
            for (j in 0..7) {
                mChess.setChessSquare(
                    getViewFromPos(i, j), i, j
                ) // assigns imageview widgets to the array backend
                mChess.getChessSquare(i, j)?.setOnClickListener { view -> this.selectSquare(view) } // makes each tile clickable
                getViewFromPos(i, j)?.tag = null // Shouldn't need this but it's there just in case
            }
        }

        //Initialized storage and leaves a test message in log to see if it works
        Storage.make(requireActivity().applicationContext, mChess)

        if (arguments != null) {
            Toast.makeText(context, getString(R.string.loading_game), Toast.LENGTH_SHORT).show()
            loadGame()
        } else {
            newGame()
        }

        Log.d("wKing", wKing.toString())
        Log.d("bKing", bKing.toString())

        if (debug_printing) mChess.debug_printChess()

        return rootView
    }

    private fun newGame(view: View? = null) { // starts new game
        resetColors()
        possibleSelections.clear()
        mChess.newGame()
        selectedPiece = null
        p2turn = false
        isGameOver = false
        if (debug_printing) mChess.debug_printChess()

        wKing = getPieceFromPos(4, 0)
        bKing = getPieceFromPos(4, 7)

        wKingMove =
            Storage.getInt("wKING") // stores the value of movements the king has made so far to check if it has moved yet
    }

    private fun loadGame(view: View? = null) { // starts new game
        resetColors()
        possibleSelections.clear()
        mChess.setBoard()
        selectedPiece = null
        p2turn = false
        isGameOver = false
        if (debug_printing) mChess.debug_printChess()

        wKing = null
        bKing = null

        for (i in 0..7) { // can't just assign based on position so searches array
            for (j in 0..7) {
                if (isPieceOnView(getViewFromPos(i, j))) {
                    val piece = getPieceFromPos(i, j)
                    if (piece != null) {
                        if (piece.pieceName == pieceName.wKING) {
                            wKing = piece
                        }
                        if (piece.pieceName == pieceName.bKING) {
                            bKing = piece
                        }
                    }
                }
            }
        }

        if (wKing == null || bKing == null) {
            Toast.makeText(context, R.string.prev_game_ended, Toast.LENGTH_LONG).show()
            newGame()
        } else {
            Toast.makeText(context, R.string.prev_game_loaded, Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectSquare(view: View?) { // code for when a square is tapped
        if (!isGameOver) {
            resetColors()

            val square = getIDfromView(view)

            //Log.d("square", square); // prints ID of square selected
            var currentPiece = mChess?.getPiece(square)

            if (possibleSelections.contains(view)) { // if previous selection exists, then if the square is within possibleSelectionsFinal

                // Game over code

                if (requireView().tag == null && currentPiece != null && (currentPiece.pieceName == pieceName.wKING || currentPiece.pieceName == pieceName.bKING)) {
                    Log.d("game over", "game over")

                    val game_over_alert = MaterialAlertDialogBuilder(
                        requireContext(),
                        R.style.AlertDialogTheme
                    ).setCancelable(true).setPositiveButton(getString(R.string.okay), null)

                    var win: String? = ""
                    if (currentPiece.pieceName == pieceName.wKING) {
                        // black wins
                        Log.d("black win", "black win")
                        win = resources.getStringArray(R.array.piece_colors)[1]
                        Storage.upCount("lose")
                    } else if (currentPiece.pieceName == pieceName.bKING) {
                        // white wins
                        Log.d("white win", "white win")
                        win = resources.getStringArray(R.array.piece_colors)[0]
                        Storage.upCount("win")
                    } else {
                        Log.wtf(
                            "help", "Oh god how did this even happen? Is it a tie or something?"
                        )
                    }

                    isGameOver = true
                    game_over_alert.setTitle(getString(R.string.checkmate, win))
                    game_over_alert.show()
                }

                if (view != null && view.tag != null && currentPiece != null) { // castling behavior
                    Log.d("tag", view.tag.toString())
                    mChess?.setChessPieces(
                        null, selectedPiece!!.x, selectedPiece!!.y
                    ) // removes from array
                    mChess?.setChessPieces(
                        null, currentPiece.column, currentPiece.row
                    ) // removes from array

                    var rookX = 0 // rook is current
                    var kingX = 0 // king is selected
                    if (selectedPiece!!.position == "a1") {
                        //Log.d("rook", "left rook");
                        rookX = 3
                        kingX = 2
                    } else if (selectedPiece!!.position == "h1") {
                        //Log.d("rook", "right rook");
                        rookX = 5
                        kingX = 6
                    }
                    if (rookX > 0) {
                        mChess?.setChessPieces(selectedPiece, rookX, 0)
                        mChess?.setChessPieces(currentPiece, kingX, 0)

                        selectedPiece!!.setPosition(rookX, 0)
                        currentPiece.setPosition(kingX, 0)
                    }

                    selectedPiece = null
                } else { // normal behavior
                    mChess?.setChessPieces(
                        null, selectedPiece!!.x, selectedPiece!!.y
                    ) // removes from array

                    currentPiece = selectedPiece // copies old piece into new location
                    currentPiece!!.position = square // moves copy onto new position in piece data

                    //Makes the number go up of the moved piece
                    Storage.upCount(currentPiece.pieceName.toString())

                    selectedPiece =
                        null // removes old piece (either other color or none) from board

                    mChess?.setChessPieces(
                        currentPiece, square
                    ) // sets piece to new place in array
                }

                // Pawn at end behavior
                if (currentPiece.pieceName == pieceName.wPAWN && currentPiece.y == 7) {
                    currentPiece.pieceName = pieceName.wQUEEN
                } else if (currentPiece.pieceName == pieceName.bPAWN && currentPiece.y == 0) {
                    currentPiece.pieceName = pieceName.bQUEEN
                }

                // end of turn
                resetColors()
                resetTags()
                mChess?.updateBoard()
                possibleSelections.clear()
                checkForCheck()
                Storage.saveBoard()
                //Log.w("board", Storage.getString("Board"));
                if (isGameOver) return

                if (debug_printing) mChess?.debug_printChess()

                p2turn = !p2turn
                if (p2turn) p2move()
            } else if (currentPiece != null) {
                setColor(
                    getViewFromPiece(currentPiece), pieceSelectColor
                ) // sets square color to yellow
                resetTags()
                possibleSelections.clear()
                pieceSelection(currentPiece)
            } else {
                resetTags()
                possibleSelections.clear()
                //Log.wtf("wtf", "help");
            }
        }
    }

    private fun resetTags() {
        for (i in 0..7) { // removes any leftover tags from castling
            for (j in 0..7) {
                getViewFromPos(i, j)!!.tag = null
            }
        }
    }

    fun setColor(view: View?, color: Int) { // sets the color for a single view
        view?.setBackgroundColor(ContextCompat.getColor(requireActivity(), color))
    }

    private fun resetColors() { // resets all colors to the standard black and white
        for (i in 0..7) {
            for (j in 0..7) {
                if ((i + j) % 2 == 0) {
                    setColor(mChess?.getChessSquare(i, j), R.color.black)
                } else {
                    setColor(mChess?.getChessSquare(i, j), R.color.white)
                }
            }
        }
    }

    // player 2 movement method
    // generates random numbers and checks if that square has a valid piece, otherwise runs again
    // once it finds a valid piece, it chooses a random possible selection, and moves there.
    private fun p2move() { // TODO: make king move into a non-check space
        val isInCheck = isKingChecked(bKing).first
        val hasCheck = isKingChecked(wKing)
        var randyPiece: ChessPiece?

        if (isInCheck) {
            randyPiece = bKing
        } else if (hasCheck.first) {
            Log.d("check", "black has check")
            randyPiece = getPieceFromView(hasCheck.second[0])
        } else {
            randyPiece = randomPiece
        }

        while (p2turn) {
            if (randyPiece != null && randyPiece.pieceColor == R.color.black) {
                val currentView = getViewFromPiece(randyPiece)
                selectSquare(currentView)

                if (!possibleSelections.isEmpty()) { // if there are possible moves
                    Log.d("p2", "p2 move")
                    var spaceToMoveTo = if (possibleSelections.contains(getViewFromPiece(wKing))) {
                        getViewFromPiece(wKing)
                    } else {
                        possibleSelections[Random().nextInt(possibleSelections.size)]
                    }
                    selectSquare(spaceToMoveTo)
                    p2turn = false
                } else {
                    randyPiece =
                        randomPiece // if there are no possible moves with that piece run again
                }
            } else {
                randyPiece = randomPiece
            }
        }
    }

    // Selection path code (the thing that shows the colored path of possible locations)
    fun pieceSelection(currentPiece: ChessPiece?) {
        selectedPiece = currentPiece

        if (!p2turn) {
            Log.d("p1selected", selectedPiece.toString())
        } else {
            Log.d("p2selected", selectedPiece.toString())
        }

        possibleSelections.clear() // reset selections

        val piecePath = pieceSelectionPath(selectedPiece)

        for (selection in piecePath!!) { // sets all views in the arraylist to be a certain color and selectable
            if (selection != null) {
                if (!isPieceOnView(selection) || getPieceFromView(selection)!!.pieceColor != selectedPiece!!.pieceColor) {
                    setColor(selection, possibleSelectColor)
                    possibleSelections.add(selection)
                } else if (selectedPiece!!.pieceName == pieceName.wROOK && getPieceFromView(
                        selection
                    )!!.pieceName == pieceName.wKING
                ) {
                    if (!hasKingMoved() && getIDfromView(selection) == "e1") {
                        setColor(selection, R.color.green)
                        possibleSelections.add(selection)
                        selection.tag = "castling"
                    }
                }
            }
        }
    }

    fun pieceSelectionPath(pieceToFindPath: ChessPiece?): ArrayList<View?>? {
        if (pieceToFindPath == null) return null

        val viewsSelection = ArrayList<View?>()

        val x = pieceToFindPath.x
        val y = pieceToFindPath.y

        when (pieceToFindPath.pieceName) {
            pieceName.wPAWN -> {
                var wMovement = 1
                if (y == 1) wMovement = 2
                var i = 1
                while (i <= wMovement) {
                    if (getPieceFromPos(x, y + i) == null) {
                        viewsSelection.add(getViewFromPos(x, y + i))
                    } else break
                    i++
                }

                val wPiecesToKill =
                    arrayOf(getPieceFromPos(x + 1, y + 1), getPieceFromPos(x - 1, y + 1))
                for (piece in wPiecesToKill) {
                    if (piece != null) viewsSelection.add(getViewFromPos(piece.x, piece.y))
                }
            }

            pieceName.bPAWN -> {
                var bMovement = 1
                if (y == 6) bMovement = 2
                var i = 1
                while (i <= bMovement) {
                    if (getPieceFromPos(x, y - i) == null) {
                        viewsSelection.add(getViewFromPos(x, y - i))
                    } else break
                    i++
                }

                val bPiecesToKill =
                    arrayOf(getPieceFromPos(x + 1, y - 1), getPieceFromPos(x - 1, y - 1))
                for (piece in bPiecesToKill) {
                    if (piece != null) viewsSelection.add(getViewFromPos(piece.x, piece.y))
                }
            }

            pieceName.wKNIGHT, pieceName.bKNIGHT -> {
                var i = -2
                while (i <= 2) {
                    var j = 0
                    if (i == 0) {
                        i++
                        continue
                    }
                    if (abs(i) == 2) j = 1
                    if (abs(i) == 1) j = 2
                    viewsSelection.add(getViewFromPos(x - i, y - j))
                    viewsSelection.add(getViewFromPos(x - i, y + j))
                    i++
                }
            }

            pieceName.wBISHOP, pieceName.bBISHOP ->                 // I hate this but it works so well
            {
                var i = 0
                while (i < 2) {
                    val possible = ArrayList<View?>()
                    var j = -8
                    while (j < 8) {
                        val posY = y + j

                        var posX = if (i == 0) x + j
                        else x - j

                        if (isWithinBoard(posX, posY)) {
                            val tempView: View? = getViewFromPos(posX, posY)
                            possible.add(tempView)
                        }
                        j++
                    }

                    val currentIndex = possible.indexOf(getViewFromPiece(pieceToFindPath))

                    var a = currentIndex - 1
                    var b = currentIndex + 1

                    while (a > 0 && !isPieceOnView(possible[a])) {
                        viewsSelection.add(possible[a])
                        a--
                    }
                    while (b < possible.size && !isPieceOnView(possible[b])) {
                        viewsSelection.add(possible[b])
                        b++
                    }

                    if (a >= 0) viewsSelection.add(possible[a])
                    if (b < possible.size) viewsSelection.add(possible[b])
                    i++
                }
            }

            pieceName.wROOK, pieceName.bROOK ->                 // this is just the same code as bishop lmao
            {
                var i = 0
                while (i < 2) {
                    val possible = ArrayList<View?>()
                    var j = -8
                    while (j < 8) {
                        var posX: Int
                        var posY: Int

                        if (i == 0) {
                            posX = x
                            posY = y + j
                        } else {
                            posX = x + j
                            posY = y
                        }

                        if (isWithinBoard(posX, posY)) {
                            val tempView: View? = getViewFromPos(posX, posY)
                            possible.add(tempView)
                        }
                        j++
                    }

                    val currentIndex = possible.indexOf(getViewFromPiece(pieceToFindPath))

                    var a = currentIndex - 1
                    var b = currentIndex + 1

                    while (a > 0 && !isPieceOnView(possible[a])) {
                        viewsSelection.add(possible[a])
                        a--
                    }
                    while (b < possible.size && !isPieceOnView(possible[b])) {
                        viewsSelection.add(possible[b])
                        b++
                    }

                    if (a >= 0) viewsSelection.add(possible[a])
                    if (b < possible.size) viewsSelection.add(possible[b])
                    i++
                }
            }

            pieceName.wQUEEN, pieceName.bQUEEN -> {
                var i = 0
                while (i < 4) {
                    val possible = ArrayList<View?>()
                    var j = -8
                    while (j < 8) {
                        var posX = x
                        var posY = y

                        when (i) {
                            0 ->                                 //posX = x;
                                posY = y + j

                            1 -> posX = x + j
                            2 -> {
                                posX = x + j
                                posY = y + j
                            }

                            3 -> {
                                posX = x - j
                                posY = y + j
                            }
                        }
                        if (isWithinBoard(posX, posY)) {
                            val tempView: View? = getViewFromPos(posX, posY)
                            possible.add(tempView)
                        }
                        j++
                    }

                    val currentIndex = possible.indexOf(getViewFromPiece(pieceToFindPath))

                    var a = currentIndex - 1
                    var b = currentIndex + 1

                    while (a > 0 && !isPieceOnView(possible[a])) {
                        viewsSelection.add(possible[a])
                        a--
                    }
                    while (b < possible.size && !isPieceOnView(possible[b])) {
                        viewsSelection.add(possible[b])
                        b++
                    }

                    if (a >= 0) viewsSelection.add(possible[a])
                    if (b < possible.size) viewsSelection.add(possible[b])
                    i++
                }
            }

            pieceName.wKING, pieceName.bKING -> {
                var offsetX = -1
                while (offsetX <= 1) {
                    var offsetY = -1
                    while (offsetY <= 1) {
                        viewsSelection.add(getViewFromPos(x + offsetX, y + offsetY))
                        offsetY++
                    }
                    offsetX++
                }
            }
        }
        return viewsSelection
    }

    // awful method using a discouraged API but it works well so I don't wanna touch it
    fun getViewFromPos(
        x: Int?, y: Int?
    ): ImageView? { // gets the square from activity main, used for adding all views to the array
        return getViewFromPos(rootView, x, y)
    }

    @SuppressLint("DiscouragedApi")
    fun getViewFromPos(
        root: View?, x: Int?, y: Int?
    ): ImageView?{
        return if(isWithinBoard(x, y)) root?.findViewById(
            resources.getIdentifier(
                Chess.Companion.getIDfromNums(x!!, y!!), "id", requireActivity().packageName
            )
        )
        else null
    }

    // gets the ChessPiece object that is currently in a specified view
    fun getPieceFromView(view: View?): ChessPiece? {
        val currentNums: IntArray = Chess.Companion.getNumsfromID(getIDfromView(view))
        return mChess?.getPiece(currentNums[0], currentNums[1])
    }

    // gets the ChessPiece object that is at a specified position
    fun getPieceFromPos(x: Int, y: Int): ChessPiece? {
        try {
            val square: View? = getViewFromPos(x, y)
            return getPieceFromView(square)
        } catch (e: NullPointerException) {
            return null
        }
    }

    // Gets the view that a specified piece is in
    fun getViewFromPiece(piece: ChessPiece?): View? {
        return if (piece != null) {
            getViewFromPos(piece.x, piece.y)
        } else {
            null
        }
    }

    fun isPieceOnView(view: View?): Boolean { // checks if a certain view has a piece or not
        return view != null && getPieceFromView(view) != null
    }

    // Checks if a certain position is within the appropriate bounds of a Chess board, and returns a boolean value
    fun isWithinBoard(x: Int?, y: Int?): Boolean {
        x ?: return false
        y ?: return false
        if (x < 0) return false
        if (y < 0) return false
        if (x > 7) return false
        if (y > 7) return false

        return true
    }

    fun checkForCheck() {
        //Use getPieceFromView to find what piece is in the sight of the king,
        //then pull the possible selection of that piece to make sure it can check the king.
        val kings = arrayOf(wKing, bKing) //Creates an array of the two kings
        val inCheck = booleanArrayOf(false, false)
        //for (ChessPiece king : kings) {
        for (i in kings.indices) {
            inCheck[i] = isKingChecked(kings[i]).first
        }

        if ((inCheck[0] || inCheck[1]) && !p2turn) { // checks if the bKing is in check
            Log.d("makeToast", "toast generated")
            Toast.makeText(context, R.string.check, Toast.LENGTH_SHORT).show()
        }
    }

    // Returns a pair containing a boolean whether or not the King is checked, and an ArrayList of the pieces checking it
    fun isKingChecked(king: ChessPiece?): Pair<Boolean, ArrayList<View?>> {
        var inCheck = false
        val piecesChecking = ArrayList<View?>()

        val x = king?.x
        val y = king?.y

        val possibleAttackers = ArrayList<View?>()
        val possibleKnightSpaces = ArrayList<View?>() // Knights don't follow normal movement

        for (i in 0..3) {
            val possible = ArrayList<View?>()
            for (j in -8..7) {
                var posX = x
                var posY = y

                when (i) {
                    0 ->                         //posX = x;
                        posY = y?.plus(j)

                    1 -> posX = x?.plus(j)
                    2 -> {
                        posX = x?.plus(j)
                        posY = y?.plus(j)
                    }

                    3 -> {
                        posX = x?.minus(j)
                        posY = y?.plus(j)
                    }
                }
                if (isWithinBoard(posX, posY)) {
                    val tempView: View? = getViewFromPos(posX, posY)
                    possible.add(tempView)
                }
            }

            val currentIndex = possible.indexOf(getViewFromPiece(king))

            var a = currentIndex - 1
            var b = currentIndex + 1

            while (a > 0 && !isPieceOnView(possible[a])) {
                //possibleAttackers.add(possible.get(a));
                a--
            }
            while (b < possible.size && !isPieceOnView(possible[b])) {
                //possibleAttackers.add(possible.get(b));
                b++
            }

            if (a >= 0) {
                possibleAttackers.add(possible[a])
                //setColor(possible.get(a), R.color.brown);
            }
            if (b < possible.size) {
                possibleAttackers.add(possible[b])
                //setColor(possible.get(b), R.color.brown);
            }
        }

        for (i in -2..2) {
            var j = 0
            if (i == 0) continue
            if (abs(i) == 2) j = 1
            if (abs(i) == 1) j = 2
            possibleKnightSpaces.add(getViewFromPos(x?.minus(i), y?.minus(j)))
            possibleKnightSpaces.add(getViewFromPos(x?.minus(i), y?.plus(j)))
        }

        for (attackers in possibleAttackers) {
            if (isPieceOnView(attackers) && getPieceFromView(attackers)!!.pieceColor != king!!.pieceColor) {
                val possibleAttackerPath = pieceSelectionPath(getPieceFromView(attackers))
                if (possibleAttackerPath!!.contains(getViewFromPiece(king))) {
                    piecesChecking.add(attackers)
                }
            }
        }

        for (knightSpace in possibleKnightSpaces) {
            if (isPieceOnView(knightSpace) && (getPieceFromView(knightSpace)!!.pieceName == pieceName.wKNIGHT || getPieceFromView(
                    knightSpace
                )!!.pieceName == pieceName.bKNIGHT)
            ) {
                if (getPieceFromView(knightSpace)!!.pieceColor != king!!.pieceColor) {
                    piecesChecking.add(knightSpace)
                }
            }
        }

        for (pieces in piecesChecking) {
            setColor(pieces, checkColor)
            inCheck = true
        }

        if (inCheck) setColor(getViewFromPiece(king), checkColor)

        return Pair(inCheck, piecesChecking)
    }

    private fun hasKingMoved(): Boolean {
        return Storage.getInt("wKING") > wKingMove
    }

    val randomPiece: ChessPiece?
        get() {
            val randy = Random()
            return mChess?.getPiece(randy.nextInt(8), randy.nextInt(8))
        }

    companion object {
        // gets the ID of a certain view, ex. "a1" or "e5"
        fun getIDfromView(view: View?): String {
            val viewString = view.toString()
            Log.d("viewString", view?.resources!!.getResourceName(view.id))
            return view.resources!!.getResourceName(view.id)
            /*return viewString.substring(
                viewString.lastIndexOf("app:id/") + 7, viewString.length - 1
            )*/
        }
    }
}