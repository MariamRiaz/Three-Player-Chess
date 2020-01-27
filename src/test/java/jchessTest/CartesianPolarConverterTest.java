package jchessTest;

import jchess.game.chessboard.CartesianPolarConverter;
import jchess.game.chessboard.PolarPoint;
import org.junit.Test;

import java.awt.*;

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