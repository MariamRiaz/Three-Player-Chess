package jchess.UI.board;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class CartesianPolarConverter {

    public double getRadiusFromCartesian(Point point, Point circleCenter) {
        double xDistance = Math.pow(point.getX()-circleCenter.getX(), 2);
        double yDistance = Math.pow(point.getY()-circleCenter.getY(), 2);
        return Math.sqrt(xDistance + yDistance);
    }

    public double getDegreesFromCartesian(Point point, Point circleCenter) {
       return Math.atan2(point.getY()-circleCenter.getY(),
               point.getX()-circleCenter.getX()) * 180/Math.PI;
    }

    private int getXFromPolar(PolarPoint point) {
        return (int) Math.floor(point.getRadius() * Math.cos(Math.toRadians(point.getDegrees())));
    }

    private int getYFromPolar(PolarPoint point) {
        return (int) Math.ceil(point.getRadius() * Math.sin(Math.toRadians(point.getDegrees())));
    }

    public Point getCartesianPointFromPolar(PolarPoint point, Point circleCenter) {
        int x = getXFromPolar(point);
        int y = getYFromPolar(point);
        Point p = new Point(x,y);
        p.translate(circleCenter.x, circleCenter.y);
        return p;
    }


}
