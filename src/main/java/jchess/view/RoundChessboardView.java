package jchess.view;

import jchess.GUI;
import jchess.Settings;
import jchess.UI.board.Square;
import jchess.common.CartesianPolarConverter;
import jchess.common.PolarCell;
import jchess.common.PolarPoint;
import jchess.common.RoundChessboardViewInitializer;
import jchess.controller.RoundChessboardController;
import jchess.pieces.Piece;
import jchess.pieces.PieceVisual;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class RoundChessboardView extends JPanel {

    private Image boardImage;
    private Image selectedSquareImage;// image of highlighted square
    private Image ableSquareImage;// image of square where piece can go
    private int chessBoardSize;
    public Image upDownLabel = null;
    private Point topLeft = new Point(0, 0);
    private HashSet<Square> moves;
    private List<PolarCell> cells;
    private Point circleCenter;

    public PolarCell activeCell;


    public RoundChessboardView(int chessBoardSize, String chessBoardImagePath, int rows, int cellsPerRow, List<Square> squares) {
        super();
        this.boardImage = GUI.loadImage(chessBoardImagePath);
        this.boardImage = boardImage.getScaledInstance(chessBoardSize, chessBoardSize, Image.SCALE_DEFAULT);
        this.selectedSquareImage = GUI.loadImage("sel_square.png");
        this.ableSquareImage = GUI.loadImage("able_square.png");
        this.chessBoardSize = chessBoardSize;
        setPreferredSize(new Dimension(chessBoardSize, chessBoardSize));
        setSize(chessBoardSize, chessBoardSize);
        circleCenter = new Point(chessBoardSize/2, chessBoardSize/2);
        setVisuals4NewGame(squares, rows, cellsPerRow);
        this.setVisible(true);
    }

    private int getCellHeight() {
        return (chessBoardSize / 16) - 5;
    }

    public List<PolarCell> getCells() {
        return cells;
    }

//    public void initView(int chessBoardSize, String chessBoardImagePath, int rows, int cellsPerRow) {
//        this.boardImage = GUI.loadImage(chessBoardImagePath);
//        this.selectedSquareImage = GUI.loadImage("sel_square.png");
//        this.ableSquareImage = GUI.loadImage("able_square.png");
//        this.chessBoardSize = chessBoardSize;
//        RoundChessboardViewInitializer initializer = new RoundChessboardViewInitializer(chessBoardSize, rows, cellsPerRow);
//        this.setSize(chessBoardSize, chessBoardSize);
//        circleCenter = new Point(chessBoardSize/2, chessBoardSize/2);
//        this.setLocation(new Point(0, 0));
//        this.setVisible(true);
//        repaint();
//    }

    public Point getCircleCenter() {
        return circleCenter;
    }

    public void intializeCells(int rows, int cellsPerRow) {
        RoundChessboardViewInitializer initializer = new RoundChessboardViewInitializer(chessBoardSize, rows, cellsPerRow);
        this.cells = initializer.createCells();
    }

    private void drawFigure(PolarCell cell, PieceVisual visual, Point circleCenter, Graphics g) {
        CartesianPolarConverter converter = new CartesianPolarConverter();
        double bottomRadius = cell.getBottomBound();
        Point bottomLeft = converter.getCartesianPointFromPolar(new PolarPoint(bottomRadius, cell.getLeftBound()), circleCenter);
        Point bottomRight = converter.getCartesianPointFromPolar(new PolarPoint(bottomRadius, cell.getRightBound()), circleCenter);
        Point center = converter.getCartesianPointFromPolar(cell.getCenterPoint(), circleCenter);
        int verticalSize = (int) Math.abs(cell.getTopBound() - cell.getBottomBound());
        int horizontalSize = (int) Math.sqrt(Math.pow(bottomLeft.x - bottomRight.x, 2) + Math.pow(bottomLeft.y - bottomRight.y, 2));
        int pieceSize = Math.min(verticalSize, horizontalSize);
        visual.draw(g, center.x - pieceSize/2, center.y - pieceSize/2, pieceSize, pieceSize, this);
    }

    private void drawImage(PolarCell cell, Image image, Graphics g) {
        CartesianPolarConverter converter = new CartesianPolarConverter();
        double bottomRadius = cell.getBottomBound();
        Point bottomLeft = converter.getCartesianPointFromPolar(new PolarPoint(bottomRadius, cell.getLeftBound()), circleCenter);
        Point bottomRight = converter.getCartesianPointFromPolar(new PolarPoint(bottomRadius, cell.getRightBound()), circleCenter);
        Point center = converter.getCartesianPointFromPolar(cell.getCenterPoint(), circleCenter);
        int verticalSize = (int) Math.abs(cell.getTopBound() - cell.getBottomBound());
        int horizontalSize = (int) Math.sqrt(Math.pow(bottomLeft.x - bottomRight.x, 2) + Math.pow(bottomLeft.y - bottomRight.y, 2));
        int pieceSize = Math.min(verticalSize, horizontalSize);
        g.drawImage(image, center.x - pieceSize/2, center.y - pieceSize/2, pieceSize, pieceSize, this);
    }

    /**
     * Annotations to superclass Game updateing and painting the crossboard
     */
    @Override
    public void update(Graphics g) {
        repaint();
    }


    public void setActiveCell(int x, int y) {
        activeCell = getCellByPosition(x,y);
        repaint();
    }

    public void setMoves(HashSet<Square> moves) {
        this.moves = moves;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(boardImage, topLeft.x, topLeft.y, this);// draw an Image of chessboard

        for(PolarCell cell: cells) {
            if(cell.getPieceVisual() != null) {
                drawFigure(cell, cell.getPieceVisual(), circleCenter, g);
//                break;
            }
        }

        if(activeCell != null) {
            drawImage(activeCell, selectedSquareImage, g);

//            for(Square move: moves) {
//                PolarCell cell = getCellByPosition(move.pozX, move.pozY);
//                drawImage(cell, ableSquareImage, g);
//            }
        }


//        for (Iterator<Map.Entry<Piece, PieceVisual>> it = this.pieceVisuals.entrySet().iterator(); it.hasNext(); ) {
//            Point p = new Point();
//            Map.Entry<Piece, PieceVisual> ent = it.next();
//            Square sq = controller.getSquare(ent.getKey());
//            PolarCell cell = cells.get
//
//            if (sq != null && ent.getValue() != null) {
//                p.x = (int) (getTopLeftPoint().x + sq.pozX * square_height);
//                p.y = (int) (getTopLeftPoint().y + sq.pozY * square_height);
//                ent.getValue().draw(g, p.x, p.y, (int) square_height, (int) square_height);// draw image of Piece
//            }
//        }
//        // --endOf--drawPiecesOnSquares
//        if (activeSquare != null) // if some square is active
////        if (((this.activeSquare.pozX + 1) != 0) && ((this.activeSquare.pozY + 1) != 0)) // if some square is active
//        {
//            g2d.drawImage(selectedSquareImage, (this.activeSquare.pozX * (int) square_height) + topLeftPoint.x,
//                    (this.activeSquare.pozY * (int) square_height) + topLeftPoint.y, null);// draw image of selected
//            // square
//            Square tmpSquare = controller.getSquare((int) this.activeSquare.pozX, (int) this.activeSquare.pozY);
//            if (tmpSquare.piece != null)
//                this.moves = controller.getValidTargetSquaresToSavePiece(tmpSquare.piece, controller.getKing(tmpSquare.piece.player));
//
//            for (Iterator it = moves.iterator(); moves != null && it.hasNext(); ) {
//                Square sq = (Square) it.next();
//                g2d.drawImage(ableSquareImage, (sq.pozX * (int) square_height) + topLeftPoint.x,
//                        (sq.pozY * (int) square_height) + topLeftPoint.y, null);
//            }
//        }
    }/*--endOf-paint--*/

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



    /**
     * Method to draw Chessboard and their elements (pieces etc.)
     *
     * @deprecated
     */
    public void draw() {
        this.getGraphics().drawImage(boardImage, this.topLeft.x, this.topLeft.y, null);// draw an Image
        // of chessboard
        this.repaint();
    }/*--endOf-draw--*/

    public void setVisual(Piece piece, int x, int y) {
        if (piece == null)
            return;
        PolarCell cell = getCellByPosition(x, y);
        PieceVisual visual = new PieceVisual(piece.player.color == piece.player.color.black ? piece.type + "-B.png" : piece.type + "-W.png");
        cell.setPieceVisual(visual);
    }

    public void removeVisual(Piece piece, int x, int y) {
        if (piece != null) {
            PolarCell cell = getCellByPosition(x,y);
            cell.setPieceVisual(null);
        }
    }

    private PolarCell getCellByPosition(int x, int y) {
        return cells.stream()
                .filter(c -> c.getxIndex() == x && c.getyIndex() == y).findFirst().get();
    }

    public void setVisuals4NewGame(List<Square> squares, int rows, int cellsPerRow) {
        intializeCells(rows, cellsPerRow);
        for(Square square: squares) {
            if(square.piece != null) {
                setVisual(square.piece, square.pozX, square.pozY);
            }
        }
    }
}
