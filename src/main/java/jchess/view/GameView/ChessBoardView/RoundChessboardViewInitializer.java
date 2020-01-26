package jchess.view.GameView.ChessBoardView;

import jchess.entities.PolarPoint;

import java.util.ArrayList;
import java.util.List;

/* Class to generate the view according to the reducing size of the circular rows */

public class RoundChessboardViewInitializer {

    private int imageSize;

    private int cellsPerRow;

    private int rows;

    public RoundChessboardViewInitializer(int imageSize, int rows, int cellsPerRow) {
        this.imageSize = imageSize;
        this.cellsPerRow = cellsPerRow;
        this.rows = rows;
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
