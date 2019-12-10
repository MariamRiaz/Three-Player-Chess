package jchess.common;

import javafx.util.Pair;
import jchess.UI.board.Square;
import jchess.model.ChessboardModel;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoundChessboardViewInitializer {

    private int imageSize;

    private int cellsPerRow;

    private int rows;

    private CartesianPolarConverter converter;

    public RoundChessboardViewInitializer(int imageSize, int rows, int cellsPerRow) {
        this.converter = new CartesianPolarConverter();
        this.imageSize = imageSize;
        this.cellsPerRow = cellsPerRow;
        this.rows = rows;
    }

    private Point getCenter() {
        return new Point(imageSize/2, imageSize/2);
    }

    public List<PolarCell> createCells() {
        List<PolarCell> cells = new ArrayList<>();

        double degreesIncrement = 360.0/rows;
        double degreesCenter = degreesIncrement/2;

        double radiusIncrement = imageSize/16.0;
        for(int i=0; i<rows; i++) {
            double radiusCenter = imageSize/16.0 + radiusIncrement * 1.5;
            for(int j=0; j<cellsPerRow; j++) {
                PolarPoint cellCenter = new PolarPoint(radiusCenter, degreesCenter);
                cells.add(new PolarCell(cellCenter, degreesIncrement, radiusIncrement, j, i));
                radiusCenter += radiusIncrement;
            }
            degreesCenter += degreesIncrement;
        }
        return cells;
    }

}
