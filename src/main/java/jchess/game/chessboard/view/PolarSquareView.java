package jchess.game.chessboard.view;
/*
* Class that is used to set the visuals of the pieces on the circular board
*  */
import jchess.game.chessboard.PolarPoint;
import jchess.pieces.PieceVisual;

public class PolarSquareView implements SquareView {

    private PolarPoint centerPoint;

    private double widthInDegrees;

    private double polarHeight;

    private int xIndex;

    private int yIndex;

    private PieceVisual pieceVisual;

    /**
     * Method to get the square parameters in polar form
     * @param centerPoint
     * @param widthInDegrees
     * @param polarHeight
     * @param xIndex
     * @param yIndex
     */

    public PolarSquareView(PolarPoint centerPoint, double widthInDegrees, double polarHeight, int xIndex, int yIndex) {
        this.centerPoint = centerPoint;
        this.widthInDegrees = widthInDegrees;
        this.polarHeight = polarHeight;
        this.xIndex = xIndex;
        this.yIndex = yIndex;
    }

    /**
     *
     * @return Center point of each square
     */

    public PolarPoint getCenterPoint() {
        return centerPoint;
    }


    public int getxIndex() {
        return xIndex;
    }

    public int getyIndex() {
        return yIndex;
    }

    /**
     *
     * @return piece visual on the square
     */

    public PieceVisual getPieceVisual() {
        return pieceVisual;
    }

    /**
     * Method to set the piece visual on valid squares
     * @param pieceVisual
     */

    public void setPieceVisual(PieceVisual pieceVisual) {
        this.pieceVisual = pieceVisual;
    }

    public double getTopBound() {
        return centerPoint.getRadius() + polarHeight / 2;
    }

    public double getBottomBound() {
        return centerPoint.getRadius() - polarHeight / 2;
    }

    public double getLeftBound() {
        return centerPoint.getDegrees() - widthInDegrees / 2;
    }

    public double getRightBound() {
        return centerPoint.getDegrees() + widthInDegrees / 2;
    }


}
