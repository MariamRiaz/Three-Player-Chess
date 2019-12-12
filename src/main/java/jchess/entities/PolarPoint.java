package jchess.entities;

/**
 * Class that represents a point on a circle using a polar coordinate system
 */
public class PolarPoint {

    private double radius;

    private double degrees;

    public PolarPoint(double radius, double degrees) {
        this.degrees = degrees;
        this.radius = radius;
    }

    /**
     * Getter for the point radius
     * @return The radius coordinate of the point
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Getter for the point degrees
     * @return The degrees coordinate of the point
     */
    public double getDegrees() {
        return degrees;
    }

}
