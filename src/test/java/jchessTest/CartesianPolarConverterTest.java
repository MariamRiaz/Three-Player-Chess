package jchessTest;

import jchess.helper.CartesianPolarConverter;
import jchess.helper.PolarPoint;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class CartesianPolarConverterTest {

    CartesianPolarConverter cartesianPolarConverter  = new CartesianPolarConverter();

    @Test
    public void getPolarFromCartesian() {
        PolarPoint polarPoint = cartesianPolarConverter.getPolarFromCartesian(new Point(1, 0), new Point(0,0));
        assert polarPoint.getDegrees() == 0.0;
        assert polarPoint.getRadius() == 1.0;
    }

    @Test
    public void getCartesianPointFromPolar() {
        Point point = cartesianPolarConverter.getCartesianPointFromPolar(new PolarPoint(2.0,30.0), new Point());
        assert point.getX() == 1;
        assert point.getY() == 1;
    }
}