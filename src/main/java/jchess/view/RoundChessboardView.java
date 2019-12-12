package jchess.view;

import jchess.GUI;
import jchess.Log;
import jchess.Player;
import jchess.UI.board.Square;
import jchess.helper.CartesianPolarConverter;
import jchess.pieces.Piece;
import jchess.pieces.PieceVisual;
import jchess.helper.PolarPoint;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.HashSet;
import java.util.logging.Level;

public class RoundChessboardView extends JPanel {

    private Image boardImage;
    private Image selectedSquareImage;// image of highlighted square
    private Image ableSquareImage;// image of square where piece can go
    private int chessBoardSize;
    private Point topLeft = new Point(0, 0);
    private HashSet<Square> moves = new HashSet<>();
    private List<PolarCell> cells;
    private Point circleCenter;
    private PolarCell activeCell;

    public RoundChessboardView(int chessBoardSize, String chessBoardImagePath, int rows, int cellsPerRow, List<Square> squares) {
        super();
        this.boardImage = GUI.loadImage(chessBoardImagePath);
        this.boardImage = boardImage.getScaledInstance(chessBoardSize, chessBoardSize, Image.SCALE_DEFAULT);
        this.selectedSquareImage = GUI.loadImage("sel_square.png");
        this.ableSquareImage = GUI.loadImage("able_square.png");
        this.chessBoardSize = chessBoardSize;
        setSize(chessBoardSize, chessBoardSize);
        circleCenter = new Point(chessBoardSize / 2, chessBoardSize / 2);
        populatePieceVisuals(squares, rows, cellsPerRow);
        setVisible(true);
    }

    private int getCellHeight() {
        return (chessBoardSize / 16) - 5;
    }

    public List<PolarCell> getCells() {
        return cells;
    }

    public Point getCircleCenter() {
        return circleCenter;
    }

    private void initializeCells(int rows, int cellsPerRow) {
        RoundChessboardViewInitializer initializer = new RoundChessboardViewInitializer(chessBoardSize, rows, cellsPerRow);
        this.cells = initializer.createCells();
    }

    private void drawFigure(PolarCell cell, PieceVisual visual, Graphics g) {
        CartesianPolarConverter converter = new CartesianPolarConverter();
        Point center = converter.getCartesianPointFromPolar(cell.getCenterPoint(), circleCenter);
        int figureSize = getImageSizeForCell(cell);
        visual.draw(g, center.x - figureSize / 2, center.y - figureSize / 2, figureSize, figureSize, this);
    }

    private int getImageSizeForCell(PolarCell cell) {
        CartesianPolarConverter converter = new CartesianPolarConverter();
        double bottomRadius = cell.getBottomBound();
        Point bottomLeft = converter.getCartesianPointFromPolar(new PolarPoint(bottomRadius, cell.getLeftBound()), circleCenter);
        Point bottomRight = converter.getCartesianPointFromPolar(new PolarPoint(bottomRadius, cell.getRightBound()), circleCenter);
        int verticalSize = (int) Math.abs(cell.getTopBound() - cell.getBottomBound());
        int horizontalSize = (int) Math.sqrt(Math.pow(bottomLeft.x - bottomRight.x, 2) + Math.pow(bottomLeft.y - bottomRight.y, 2));
        return Math.min(verticalSize, horizontalSize);
    }

    private void drawImage(PolarCell cell, Image image, Graphics g) {
        CartesianPolarConverter converter = new CartesianPolarConverter();
        Point center = converter.getCartesianPointFromPolar(cell.getCenterPoint(), circleCenter);
        int imageSize = getImageSizeForCell(cell);
        g.drawImage(image, center.x - imageSize / 2, center.y - imageSize / 2, imageSize, imageSize, this);
    }

    /**
     * Annotations to superclass Game updateing and painting the crossboard
     */
    @Override
    public void update(Graphics g) {
        repaint();
    }


    public void setActiveCell(int x, int y) {
        activeCell = getCellByPosition(x, y);
    }

    public void resetActiveCell() {
        activeCell = null;
    }

    public void resetPossibleMoves() {
        moves.clear();
    }

    public void setMoves(HashSet<Square> moves) {
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
                    PolarCell cell = getCellByPosition(move.getPozX(), move.getPozY());
                    drawImage(cell, ableSquareImage, g);
                }
            } catch (NullPointerException e) {
                Log.log(Level.SEVERE, "List of moves is empty or not initialized");
            }
        }
    }

    private void drawPieceVisuals(Graphics g) {
        for (PolarCell cell : cells) {
            if (cell.getPieceVisual() != null) {
                drawFigure(cell, cell.getPieceVisual(), g);
            }
        }
    }

    private void drawBoardImage(Graphics g) {
        g.drawImage(boardImage, topLeft.x, topLeft.y, this);
    }

//    public void resizeChessboard(int height) {
//        BufferedImage resized = new BufferedImage(height, height, BufferedImage.TYPE_INT_ARGB_PRE);
//        Graphics g = resized.createGraphics();
//        g.drawImage(boardImage, 0, 0, height, height, null);
//        g.dispose();
//        boardImage = resized.getScaledInstance(height, height, 0);
//        this.square_height = (float) (height / 8);
//        if (settings.renderLabels) {
//            height += 2 * (this.upDownLabel.getHeight(null));
//        }
//        this.setSize(height, height);
//
//        resized = new BufferedImage((int) square_height, (int) square_height, BufferedImage.TYPE_INT_ARGB_PRE);
//        g = resized.createGraphics();
//        g.drawImage(this.ableSquareImage, 0, 0, (int) square_height, (int) square_height, null);
//        g.dispose();
//        this.ableSquareImage = resized.getScaledInstance((int) square_height, (int) square_height, 0);
//
//        resized = new BufferedImage((int) square_height, (int) square_height, BufferedImage.TYPE_INT_ARGB_PRE);
//        g = resized.createGraphics();
//        g.drawImage(this.selectedSquareImage, 0, 0, (int) square_height, (int) square_height, null);
//        g.dispose();
//        this.selectedSquareImage = resized.getScaledInstance((int) square_height, (int) square_height, 0);
//    }

    public void updateAfterMove(Piece piece, int oldX, int oldY, int newX, int newY) {
        removeVisual(piece, oldX, oldY);
        setVisual(piece, newX, newY);
        moves.clear();
        activeCell = null;
        repaint();
    }

    public void setVisual(Piece piece, Square square) {
    	if (square != null)
    		setVisual(piece, square.getPozX(), square.getPozY());
    }
    
    public void setVisual(Piece piece, int x, int y) {
        if (piece == null)//TODO
            return;
        PolarCell cell = getCellByPosition(x, y);
        PieceVisual visual = new PieceVisual(piece.player.color == Player.colors.black ? piece.type + "-B.png" : piece.type + "-W.png");
        cell.setPieceVisual(visual);
    }
    
    public void removeVisual(Piece piece, Square square) {
        if (square != null)
        	removeVisual(piece, square.getPozX(), square.getPozY());
    }
    
    public void removeVisual(Piece piece, int x, int y) {
        PolarCell cell = getCellByPosition(x, y);
        cell.setPieceVisual(null);
    }

    private PolarCell getCellByPosition(int x, int y) {
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
