package jchess.view;

import jchess.pieces.PieceVisual;

/***
 * Abstract representation of the view of a Square
 */
public interface SquareView {

    int getxIndex();

    int getyIndex();

    PieceVisual getPieceVisual();

    void setPieceVisual(PieceVisual pieceVisual);

    double getTopBound();

    double getBottomBound();

    double getLeftBound();

    double getRightBound();

    double getCenterPoint();

}
