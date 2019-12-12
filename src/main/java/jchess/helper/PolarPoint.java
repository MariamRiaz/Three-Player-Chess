package jchess.helper;

/*
* Class that provides a template for polar point which will be then used in cartesianpolarconverter and polarcell class
* to gets the currents coordinates for each square of the board
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
