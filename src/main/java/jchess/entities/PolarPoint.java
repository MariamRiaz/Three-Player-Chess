package jchess.entities;

/*Class that represents a PolarPoint object, containing a degree and radius, to determine a point in a circle
* */

public class PolarPoint {

    private double radius;

    private double degrees;

    public PolarPoint(double radius, double degrees) {
        this.degrees = degrees;
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setDegrees(double degrees) {
        this.degrees = degrees;
    }

    public double getDegrees() {
        return degrees;
    }

}
