package jchess.game.chessboard;

import java.awt.*;

/*
 * Class that converts points from a polar coordinate system to cartesian points and back
 * */
public class CartesianPolarConverter {

    /**
     * Method to get radius from a particular point on board to circle center
     * @param point X and Y coordinate of point
     * @param circleCenter
     * @return radius
     */

    private double getRadiusFromCartesian(Point point, Point circleCenter) {
        double xDistance = Math.pow(point.getX() - circleCenter.getX(), 2);
        double yDistance = Math.pow(point.getY() - circleCenter.getY(), 2);
        return Math.sqrt(xDistance + yDistance);
    }

    /**
     * Method to get the degrees from a point at the cicular board
     * @param point
     * @param circleCenter
     * @return
     */

    private double getDegreesFromCartesian(Point point, Point circleCenter) {
        double degrees = Math.atan2(point.getY() - circleCenter.getY(),
                point.getX() - circleCenter.getX()) * 180 / Math.PI;
        return degrees >= 0 ? degrees : degrees + 360;
    }

    /**
     * Converts a cartesian point to a polar point
     * @param point The cartesian point
     * @param circleCenter The point that represents the circle center
     * @return A polar point with polar coordinates relative to the circle center
     */
    public PolarPoint getPolarFromCartesian(Point point, Point circleCenter) {
        double radius = getRadiusFromCartesian(point, circleCenter);
        double degrees = getDegreesFromCartesian(point, circleCenter);
        return new PolarPoint(radius, degrees);
    }

    private int getXFromPolar(PolarPoint point) {
        return (int) Math.floor(point.getRadius() * Math.cos(Math.toRadians(point.getDegrees())));
    }

    private int getYFromPolar(PolarPoint point) {
        return (int) Math.ceil(point.getRadius() * Math.sin(Math.toRadians(point.getDegrees())));
    }

    /**
     * Converts a polar point to a cartesian point
     * @param point A polar point
     * @param circleCenter The point that represents the circle center
     * @return A cartesian point that represents the screen coordinates
     */
    public Point getCartesianPointFromPolar(PolarPoint point, Point circleCenter) {
        int x = getXFromPolar(point);
        int y = getYFromPolar(point);
        Point p = new Point(x, y);
        p.translate(circleCenter.x, circleCenter.y);
        return p;
    }


}
