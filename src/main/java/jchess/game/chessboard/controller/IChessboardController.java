package jchess.game.chessboard.controller;

import jchess.game.player.Player;
import jchess.game.chessboard.model.Square;
import jchess.game.chessboard.view.AbstractChessboardView;
import jchess.game.chessboard.model.IChessboardModel;
import jchess.move.effects.BoardTransition;
import jchess.pieces.Piece;

import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.List;
import java.util.Observer;



public interface IChessboardController extends MouseListener {
    /**
     * @return The view of the chessboard.
     */
    AbstractChessboardView getView();
    
    /**
     * @param observer Adds an Observer to the currently selected Square.
     */
    void addSelectSquareObserver(Observer observer);
    
    /**
     * Checks whether a move from a given origin Square to another Square is possible.
     *
     * @param fromX The x index of the origin Square.
     * @param fromY The y index of the origin Square.
     * @param toX   The x index of the target Square.
     * @param toY   The y index of the target Square.
     * @return Whether the move is possible.
     */
    boolean moveIsPossible(int fromX, int fromY, int toX, int toY);

    /**
     * Checks whether a move from a given origin Square to another Square is possible.
     *
     * @param squareFrom The origin Square, containing its x and y indices.
     * @param squareTo   The target Square, containing its x and y indices.
     * @return Whether the move is possible.
     */
    boolean moveIsPossible(Square squareFrom, Square squareTo);

    /**
     * Selectes the given Square.
     *
     * @param sq The Square.
     */
    void select(Square sq);

    /**
     * Checks whether the given Piece cannot be made non-threatened regardless what move its owning Player makes.
     *
     * @param piece The Piece to check.
     * @return Whether the Piece can be saved.
     */
    boolean pieceIsUnsavable(Piece piece);

    /**
     * Gets the Pieces, which, when taken, will cause their owning Player to lose.
     * @param player The Player whose crucial Pieces to retrieve.
     * @return The Player's crucial Pieces.
     */
    HashSet<Piece> getCrucialPieces(Player player);

    /**
     * Gets the Pieces, which, when taken, will cause their owning Player to lose.
     * @return All Players' crucial Pieces.
     */
    HashSet<Piece> getCrucialPieces();

    /**
     * @return The currently selected Square.
     */
    Square getActiveSquare();

    /**
     * Sets the currently selected Square.
     *
     * @param square The Square to select, by indices.
     */
    void setActiveSquare(Square square);

    /**
     * Gets the Square that a given Piece is on, if any.
     *
     * @param piece The Piece whose Square to retrieve.
     * @return The Square of the given Piece, if any.
     */
    Square getSquare(Piece piece);

    /**
     * Gets a List of all Squares in the board.
     *
     * @return The List of Squares.
     */
    List<Square> getSquares();

    /**
     * Method move a Piece from the given Square to a new Square, as defined by their x and y indices.
     *
     * @param begin               The origin Square, where the moving Piece is located.
     * @param end                 The target Square, on which the moving Piece should end.
     * @param refresh             Whether or not to refresh the chessboard.
     * @param clearForwardHistory Whether or not to clear the forward history of the MoveHistoryController instance for this game.
     */
    void move(Square begin, Square end);

    /**
     * Applies the given atomic BoardTransition to the current board state, making the Position and State Changes in sequence.
     * @param me The BoardTransition to apply.
     */
    void applyBoardTransition(BoardTransition me);

    /**
     * Reverses the given atomic BoardTransition to the current board state, making the Position and State Changes in the opposite sequence.
     * @param me The BoardTransition to reverse.
     */
    void reverseBoardTransition(BoardTransition me);

    /**
     * Unselects the currently selected Square.
     */
    void unselect();
    
    /**
     * Undoes the last-played move.
     *
     * @return Whether or not the undo operation was successful.
     */
    boolean undo();

    /**
     * Redoes the move that was undone last.
     *
     * @return Whether or not the redo operation was successful.
     */
    boolean redo();

    /**
     * Gets the Square with the given board indices.
     *
     * @param x The x index of the Square.
     * @param y The y index of the Square.
     * @return The Square.
     */
    Square getSquare(int x, int y);

    /**
     * Retrieves the Controller's board model.
     * @return The model.
     */
    IChessboardModel getModel();

}