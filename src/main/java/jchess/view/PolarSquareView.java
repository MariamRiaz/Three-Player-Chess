package jchess.view;
/*
* Class that is used to set the visuals of the pieces on the circular board
*  */
import jchess.entities.PolarPoint;
import jchess.pieces.PieceVisual;

public class PolarSquareView implements SquareView {

    private PolarPoint centerPoint;

    private double widthInDegrees;

    private double polarHeight;

    private int xIndex;

    private int yIndex;

    private PieceVisual pieceVisual;

    public PolarSquareView(PolarPoint centerPoint, double widthInDegrees, double polarHeight, int xIndex, int yIndex) {
        this.centerPoint = centerPoint;
        this.widthInDegrees = widthInDegrees;
        this.polarHeight = polarHeight;
        this.xIndex = xIndex;
        this.yIndex = yIndex;
    }

    public PolarSquareView(int xIndex, int yIndex) {
        this.xIndex = xIndex;
        this.yIndex = yIndex;
    }

    public PolarPoint getCenterPoint() {
        return centerPoint;
    }

    public double getWidthInDegrees() {
        return widthInDegrees;
    }

    public double getPolarHeight() {
        return polarHeight;
    }

    public int getxIndex() {
        return xIndex;
    }

    public int getyIndex() {
        return yIndex;
    }

    public PieceVisual getPieceVisual() {
        return pieceVisual;
    }

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
