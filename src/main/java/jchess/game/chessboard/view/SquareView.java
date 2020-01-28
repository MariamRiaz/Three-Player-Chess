package jchess.game.chessboard.view;

import jchess.pieces.PieceVisual;

/**
 * Abstract representation of the view of a Square.
 */
public interface SquareView {

    /**
     * Retrieves the X index of the square.
     * @return The X index.
     */
    int getxIndex();

    /**
     * Retrieves the Y index of a the square.
     * @return The Y index.
     */
    int getyIndex();

    /**
     * Retrieves the pieces visual situated on this suare view.
     * @return A piece visual object.
     */
    PieceVisual getPieceVisual();

    /**
     * Sets the given piece visual on this square view.
     * @param pieceVisual The given piece visual.
     */
    void setPieceVisual(PieceVisual pieceVisual);

    /**
     * Retrieves the top bound of the square view.
     * @return The top bound.
     */
    double getTopBound();

    /**
     * Retrieves the bottom bound of the square view.
     * @return The bottom bound.
     */
    double getBottomBound();

    /**
     * Retrieves the left bound fo the square view.
     * @return The left bound
     */
    double getLeftBound();

    /**
     * Retrieves the right bound of the square view.
     * @return The right bound of the square view.
     */
    double getRightBound();

}
