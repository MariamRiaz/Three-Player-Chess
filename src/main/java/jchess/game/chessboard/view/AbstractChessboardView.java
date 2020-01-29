package jchess.game.chessboard.view;

import jchess.game.chessboard.model.Square;
import jchess.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

/**
 * Abstract Class a Chessboard View must extend.
 */
public abstract class AbstractChessboardView extends JPanel {

    /**
     * sets active cell to null.
     */
    public abstract void resetActiveCell();

    /**
     * clears all possible moves of the selected piece
     */
    public abstract void resetPossibleMoves();

    /**
     * getter for the list of Square Views of the Chessboard
     *
     * @return List of Square Views
     */
    public abstract List<? extends SquareView> getListOfSquareViews();

    /**
     * sets the active cell by the selected x and y coordinate.
     *
     * @param x int x index
     * @param y int y index
     */
    public abstract void setActiveCell(int x, int y);

    /**
     * setter for the possible moves of the selected piece
     *
     * @param moves HashSet<Square> set of possible squares to move the selected piece to
     */
    public abstract void setMoves(Set<Square> moves);

    /**
     * updates the RoundChessboardView after move was done
     */
    public abstract void updateAfterMove();

    /**
     * remove  visual for a piece at the given index
     *
     * @param x int x index of the piece
     * @param y int y index of the piece
     */
    public abstract void removeVisual(int x, int y);

    /**
     * sets visuals for a piece
     *
     * @param piece piece to set the visual of
     * @param x     x index of the piece
     * @param y     y index of the piece
     */
    public abstract void setVisual(Piece piece, int x, int y);

    /**
     * getter for the center of the chessboard
     *
     * @return Point of the center
     */
    public abstract Point getCircleCenter();

    /**
     * removes the Visual for the given Square.
     *
     * @param square Square of which the Visual shall be removed
     */
    public abstract void removeVisual(Square square);

}
