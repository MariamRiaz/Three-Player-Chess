package jchess.UI.board;

import java.awt.*;

public class PolarCell {

    private PolarPoint centerPoint;

    private double widthInDegrees;

    private double polarHeight;

    public PolarCell(PolarPoint centerPoint, double widthInDegrees, double polarHeight) {
        this.centerPoint = centerPoint;
        this.widthInDegrees = widthInDegrees;
        this.polarHeight = polarHeight;
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

    public static PolarCell FromCartesian(Point circleCenter, Point cartesianPoint,
                                          double widthInDegrees, double polarHeight) {
        CartesianPolarConverter converter = new CartesianPolarConverter();
        double radius = converter.getRadiusFromCartesian(cartesianPoint, circleCenter);
        double degrees = converter.getDegreesFromCartesian(cartesianPoint, circleCenter);
        return new PolarCell(new PolarPoint(radius, degrees), widthInDegrees, polarHeight);
    }


}
