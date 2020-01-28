package jchess.game.chessboard.controller;

import jchess.game.chessboard.model.IChessboardModel;
import jchess.game.chessboard.model.Square;
import jchess.game.chessboard.view.AbstractChessboardView;
import jchess.game.player.Player;
import jchess.move.IMoveEvaluator;
import jchess.move.MoveEvaluator;
import jchess.move.effects.BoardTransition;
import jchess.pieces.Piece;

import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.List;
import java.util.Observer;

/**
 * Interface that a chessboard controller must implement.
 */
public interface IChessboardController extends MouseListener {

    /**
     * Retrieves the chessboard  view that this controller is responsible for.
     *
     * @return A chessboard view (visual representation of the board)
     */
    AbstractChessboardView getView();

    /**
     * Adds an observer that is responsible of getting notified whenever a new square is selected.
     *
     * @param observer The square observer that will be added.
     */
    void addSelectSquareObserver(Observer observer);

    /**
     * Checks if a certain move is possible given the starting- and destination square.
     *
     * @param squareFrom The starting square of the move
     * @param squareTo   The destination square of the move
     * @return True if the move can be performed, false otherwise
     */
    boolean moveIsPossible(Square squareFrom, Square squareTo, MoveEvaluator evaluator);

    /**
     * Applies changes to model and view whenever a square is selected.
     *
     * @param sq The selected square
     */
    void select(Square sq);

    /**
     * Checks if a piece is threatened by other pieces and if it doesn't have any available moves.
     *
     * @param piece The piece to be checked
     * @return True if the piece cannot be saved (doesn't have any possible moves), false otherwise
     */
    boolean pieceIsUnsavable(Piece piece, IMoveEvaluator evaluator);

    /**
     * Retrieves all the crucial pieces that a player owns. (pieces that cannot be captured)
     *
     * @param player The player who owns the pieces
     * @return The set of all crucial pieces that belong to the given player
     */
    HashSet<Piece> getCrucialPieces(Player player);

    /**
     * Retrieves all crucial pieces owned by all players.
     *
     * @return A set of all crucial pieces
     */
    HashSet<Piece> getCrucialPieces();

    /**
     * Retrieves the currently clicked square.
     *
     * @return The active square
     */
    Square getActiveSquare();

    /**
     * Retrieves a square by looking at the piece that is located on it.
     *
     * @param piece The given piece
     * @return The square where the piece is located
     */
    Square getSquare(Piece piece);

    /**
     * Retrieves all squares of the chessboard.
     *
     * @return A list of all squares
     */
    List<Square> getSquares();

    /**
     * Moves a from a starting square to a destination square.
     *
     * @param begin The given starting square.
     * @param end   The given destination.
     */
    void move(Square begin, Square end);

    /**
     * Applies all the necessary changes to the model- and view during a move.
     *
     * @param boardTransition The given board transition that will be applied.
     */
    void applyBoardTransition(BoardTransition boardTransition);

    /**
     * Reverts a previously applied board transition.
     *
     * @param boardTransition The given board transition that will be reverted.
     */
    void reverseBoardTransition(BoardTransition boardTransition);

    /**
     * Unselects the currently active square.
     * Opposite of select.
     */
    void unselect();

    /**
     * Undoes the last move.
     *
     * @return True when there is a move to be undone.
     */
    boolean undo();

    /**
     * Redoes the last undone move.
     *
     * @return Truen when there is a move to be redone.
     */
    boolean redo();

    /**
     * Retrieves the square that was clicked by looking at the UI coordinates of the click.
     *
     * @param x The X UI coordinate
     * @param y The Y UI coordinate
     * @return The square that was clicked
     */
    Square getSquareFromClick(int x, int y);

    /**
     * Retrieves a square given the row- and column-coordinate.
     *
     * @param x The column coordinate
     * @param y The row coordinate
     * @return The square at column x and row y
     */
    Square getSquare(int x, int y);

    /**
     * Retrieves the chessboard model that this controller is responsible for.
     *
     * @return The chessboard model
     */
    IChessboardModel getModel();

}
