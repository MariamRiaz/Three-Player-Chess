package jchess.game.chessboard.view;

import jchess.game.chessboard.CartesianPolarConverter;
import jchess.game.chessboard.PolarPoint;
import jchess.game.chessboard.RoundChessboardViewInitializer;
import jchess.game.chessboard.model.Square;
import jchess.io.Images;
import jchess.io.ResourceLoader;
import jchess.logging.Log;
import jchess.pieces.Piece;
import jchess.pieces.PieceVisual;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Class to generate the view of a circular board
 */
public class RoundChessboardView extends AbstractChessboardView {

    private Image boardImage;
    private Image selectedSquareImage;// image of highlighted square
    private Image ableSquareImage;// image of square where piece can go
    private int chessBoardSize;
    private Point topLeft = new Point(0, 0);
    private Set<Square> moves = new HashSet<>();
    private List<PolarSquareView> cells;
    private Point circleCenter;
    private PolarSquareView activeCell;

    /**
     * Constructor
     *
     * @param chessBoardSize      int     Size of the chessboard to be viewed
     * @param chessBoardImagePath String  Path of the chessboard image
     * @param rows                int     count of rows
     * @param cellsPerRow         int     count of cells per row
     * @param squares             List<Square>    List of square objects from the corresponding model
     */
    public RoundChessboardView(int chessBoardSize, String chessBoardImagePath, int rows, int cellsPerRow, List<Square> squares) {
        super();
        this.boardImage = ResourceLoader.loadImage(chessBoardImagePath);
        this.boardImage = boardImage.getScaledInstance(chessBoardSize, chessBoardSize, Image.SCALE_DEFAULT);
        this.selectedSquareImage = ResourceLoader.loadImage(Images.SQUARE_SELECTION);
        this.ableSquareImage = ResourceLoader.loadImage(Images.SQUARE);
        this.chessBoardSize = chessBoardSize;
        setSize(chessBoardSize, chessBoardSize);
        circleCenter = new Point(chessBoardSize / 2, chessBoardSize / 2);
        populatePieceVisuals(squares, rows, cellsPerRow);
        setVisible(true);
    }

    /**
     * {@inheritDoc}
     */
    public List<PolarSquareView> getListOfSquareViews() {
        return cells;
    }

    /**
     * {@inheritDoc}
     */
    public Point getCircleCenter() {
        return circleCenter;
    }

    private void initializeCells(int rows, int cellsPerRow) {
        RoundChessboardViewInitializer initializer = new RoundChessboardViewInitializer(chessBoardSize, rows, cellsPerRow);
        this.cells = initializer.createCells();
    }

    private void drawFigure(PolarSquareView cell, PieceVisual visual, Graphics g) {
        PolarSquareView polarSquareView = cell;
        CartesianPolarConverter converter = new CartesianPolarConverter();
        Point center = converter.getCartesianPointFromPolar(polarSquareView.getCenterPoint(), circleCenter);
        int figureSize = getImageSizeForCell(polarSquareView);
        visual.draw(g, center.x - figureSize / 2, center.y - figureSize / 2, figureSize, figureSize, this);
    }

    private int getImageSizeForCell(PolarSquareView cell) {
        CartesianPolarConverter converter = new CartesianPolarConverter();
        double bottomRadius = cell.getBottomBound();
        Point bottomLeft = converter.getCartesianPointFromPolar(new PolarPoint(bottomRadius, cell.getLeftBound()), circleCenter);
        Point bottomRight = converter.getCartesianPointFromPolar(new PolarPoint(bottomRadius, cell.getRightBound()), circleCenter);
        int verticalSize = (int) Math.abs(cell.getTopBound() - cell.getBottomBound());
        int horizontalSize = (int) Math.sqrt(Math.pow(bottomLeft.x - bottomRight.x, 2) + Math.pow(bottomLeft.y - bottomRight.y, 2));
        return Math.min(verticalSize, horizontalSize);
    }

    private void drawImage(PolarSquareView cell, Image image, Graphics g) {
        CartesianPolarConverter converter = new CartesianPolarConverter();
        Point center = converter.getCartesianPointFromPolar(cell.getCenterPoint(), circleCenter);
        int imageSize = getImageSizeForCell(cell);
        g.drawImage(image, center.x - imageSize / 2, center.y - imageSize / 2, imageSize, imageSize, this);
    }

    @Override
    public void update(Graphics g) {
        repaint();
    }

    /**
     * {@inheritDoc}
     */
    public void setActiveCell(int x, int y) {
        activeCell = getCellByPosition(x, y);
    }

    /**
     * {@inheritDoc}
     */
    public void resetActiveCell() {
        activeCell = null;
    }

    /**
     * {@inheritDoc}
     */
    public void resetPossibleMoves() {
        moves.clear();
    }

    /**
     * {@inheritDoc}
     */
    public void setMoves(Set<Square> moves) {
        this.moves = moves;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoardImage(g);
        drawPieceVisuals(g);
        if (activeCell != null) {
            drawImage(activeCell, selectedSquareImage, g);

            try {
                for (Square move : moves) {
                    PolarSquareView cell = getCellByPosition(move.getPozX(), move.getPozY());
                    drawImage(cell, ableSquareImage, g);
                }
            } catch (NullPointerException e) {
                Log.log(Level.SEVERE, "List of moves is empty or not initialized");
            }
        }
    }

    private void drawPieceVisuals(Graphics g) {
        for (PolarSquareView cell : cells) {
            if (cell.getPieceVisual() != null) {
                drawFigure(cell, cell.getPieceVisual(), g);
            }
        }
    }

    private void drawBoardImage(Graphics g) {
        g.drawImage(boardImage, topLeft.x, topLeft.y, this);
    }

    /**
     * {@inheritDoc}
     */
    public void updateAfterMove() {
        moves.clear();
        activeCell = null;
        repaint();
    }

    /**
     * {@inheritDoc}
     */
    public void setVisual(Piece piece, int x, int y) {
        if (piece == null)//TODO
            return;
        SquareView cell = getCellByPosition(x, y);

        PieceVisual visual = new PieceVisual(piece);
        cell.setPieceVisual(visual);
    }

    /**
     * {@inheritDoc}
     */
    public void removeVisual(int x, int y) {
        SquareView cell = getCellByPosition(x, y);
        cell.setPieceVisual(null);
    }

    /**
     * {@inheritDoc}
     */
    public void removeVisual(Square square) {
        if (square == null) {
            return;
        }
        removeVisual(square.getPozX(), square.getPozY());
    }

    private PolarSquareView getCellByPosition(int x, int y) {
        return cells.stream()
                .filter(c -> c.getxIndex() == x && c.getyIndex() == y).findFirst().get();
    }

    private void populatePieceVisuals(List<Square> squares, int rows, int cellsPerRow) {
        initializeCells(rows, cellsPerRow);
        for (Square square : squares) {
            if (square.getPiece() != null) {
                setVisual(square.getPiece(), square.getPozX(), square.getPozY());
            }
        }
    }
}
