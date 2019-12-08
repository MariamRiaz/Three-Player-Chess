package jchess.view;

import jchess.GUI;
import jchess.Player;
import jchess.Settings;
import jchess.UI.board.Square;
import jchess.controller.ChessboardController;
import jchess.model.ChessboardModel;
import jchess.pieces.Piece;
import jchess.pieces.PieceFactory;
import jchess.pieces.PieceVisual;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class ChessboardView extends JPanel {
    private Settings settings;
    private Image boardImage;
    private Image selectedSquareImage = GUI.loadImage("sel_square.png");// image of highlighted square
    private Image ableSquareImage = GUI.loadImage("able_square.png");// image of square where piece can go
    private int chessBoardHeight = 480;
    private int chessBoardWidth = chessBoardHeight;
    public float square_height;
    public Image upDownLabel = null;
    private Image LeftRightLabel = null;
    private Point topLeft = new Point(0, 0);
    public HashMap<Piece, PieceVisual> pieceVisuals = new HashMap<Piece, PieceVisual>();
    private HashSet<Square> moves;

    public ChessboardController controller;
    public Square activeSquare;


    public ChessboardView(ChessboardController controller, Square activeSquare) {
        this.controller = controller;
        this.activeSquare = activeSquare;
    }

    public void initView(String chessBoardImagePath, Settings settings) {
        this.settings = settings;
        this.boardImage = GUI.loadImage(chessBoardImagePath);
        this.setSize(chessBoardHeight, chessBoardWidth);
        this.square_height = chessBoardHeight / 8;
        this.setVisible(true);
        this.setLocation(new Point(0, 0));
        this.drawLabels();
    }

    /**
     * Annotations to superclass Game updateing and painting the crossboard
     */
    @Override
    public void update(Graphics g) {
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Point topLeftPoint = this.getTopLeftPoint();
        if (this.settings.renderLabels) {
            if (topLeftPoint.x <= 0 && topLeftPoint.y <= 0) // if renderLabels and (0,0), than draw it! (for first run)
            {
                this.drawLabels();
            }
            g2d.drawImage(this.upDownLabel, 0, 0, null);
            g2d.drawImage(this.upDownLabel, 0, boardImage.getHeight(null) + topLeftPoint.y, null);
            g2d.drawImage(this.LeftRightLabel, 0, 0, null);
            g2d.drawImage(this.LeftRightLabel, boardImage.getHeight(null) + topLeftPoint.x, 0, null);
        }
        g2d.drawImage(boardImage, topLeftPoint.x, topLeftPoint.y, null);// draw an Image of chessboard

        for (Iterator<Map.Entry<Piece, PieceVisual>> it = this.pieceVisuals.entrySet().iterator(); it.hasNext(); ) {
            Point p = new Point();
            Map.Entry<Piece, PieceVisual> ent = it.next();
            Square sq = controller.getSquare(ent.getKey());

            if (sq != null && ent.getValue() != null) {
                p.x = (int) (getTopLeftPoint().x + sq.pozX * square_height);
                p.y = (int) (getTopLeftPoint().y + sq.pozY * square_height);
                ent.getValue().draw(g, p.x, p.y, (int) square_height, (int) square_height);// draw image of Piece
            }
        }
        // --endOf--drawPiecesOnSquares
        if (activeSquare != null) // if some square is active
//        if (((this.activeSquare.pozX + 1) != 0) && ((this.activeSquare.pozY + 1) != 0)) // if some square is active
        {
            g2d.drawImage(selectedSquareImage, (this.activeSquare.pozX * (int) square_height) + topLeftPoint.x,
                    (this.activeSquare.pozY * (int) square_height) + topLeftPoint.y, null);// draw image of selected
            // square
            Square tmpSquare = controller.getSquare((int) this.activeSquare.pozX, (int) this.activeSquare.pozY);
            if (tmpSquare.piece != null)
                this.moves = controller.getValidTargetSquaresToSavePiece(tmpSquare.piece, controller.getKing(tmpSquare.piece.player));

            for (Iterator it = moves.iterator(); moves != null && it.hasNext(); ) {
                Square sq = (Square) it.next();
                g2d.drawImage(ableSquareImage, (sq.pozX * (int) square_height) + topLeftPoint.x,
                        (sq.pozY * (int) square_height) + topLeftPoint.y, null);
            }
        }
    }/*--endOf-paint--*/

    public void resizeChessboard(int height) {
        BufferedImage resized = new BufferedImage(height, height, BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics g = resized.createGraphics();
        g.drawImage(boardImage, 0, 0, height, height, null);
        g.dispose();
        boardImage = resized.getScaledInstance(height, height, 0);
        this.square_height = (float) (height / 8);
        if (settings.renderLabels) {
            height += 2 * (this.upDownLabel.getHeight(null));
        }
        this.setSize(height, height);

        resized = new BufferedImage((int) square_height, (int) square_height, BufferedImage.TYPE_INT_ARGB_PRE);
        g = resized.createGraphics();
        g.drawImage(this.ableSquareImage, 0, 0, (int) square_height, (int) square_height, null);
        g.dispose();
        this.ableSquareImage = resized.getScaledInstance((int) square_height, (int) square_height, 0);

        resized = new BufferedImage((int) square_height, (int) square_height, BufferedImage.TYPE_INT_ARGB_PRE);
        g = resized.createGraphics();
        g.drawImage(this.selectedSquareImage, 0, 0, (int) square_height, (int) square_height, null);
        g.dispose();
        this.selectedSquareImage = resized.getScaledInstance((int) square_height, (int) square_height, 0);
        this.drawLabels();
    }


    protected void drawLabels() {
        this.drawLabels((int) this.square_height);
    }

    protected final void drawLabels(int square_height) {

        // BufferedImage uDL = new BufferedImage(800, 800,
        // BufferedImage.TYPE_3BYTE_BGR);
        int min_label_height = 20;
        int labelHeight = (int) Math.ceil(square_height / 4);
        labelHeight = (labelHeight < min_label_height) ? min_label_height : labelHeight;
        int labelWidth = (int) Math.ceil(square_height * 8 + (2 * labelHeight));
        BufferedImage uDL = new BufferedImage(labelWidth + min_label_height, labelHeight, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D uDL2D = (Graphics2D) uDL.createGraphics();
        uDL2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        uDL2D.setColor(Color.white);

        uDL2D.fillRect(0, 0, labelWidth + min_label_height, labelHeight);
        uDL2D.setColor(Color.black);
        uDL2D.setFont(new Font("Arial", Font.BOLD, 12));
        int addX = (square_height / 2);
        if (this.settings.renderLabels) {
            addX += labelHeight;
        }

        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        if (!this.settings.upsideDown) {
            for (int i = 1; i <= letters.length; i++) {
                uDL2D.drawString(letters[i - 1], (square_height * (i - 1)) + addX, 10 + (labelHeight / 3));
            }
        } else {
            int j = 1;
            for (int i = letters.length; i > 0; i--, j++) {
                uDL2D.drawString(letters[i - 1], (square_height * (j - 1)) + addX, 10 + (labelHeight / 3));
            }
        }
        uDL2D.dispose();
        this.upDownLabel = uDL;

        uDL = new BufferedImage(labelHeight, labelWidth + min_label_height, BufferedImage.TYPE_3BYTE_BGR);
        uDL2D = (Graphics2D) uDL.createGraphics();
        uDL2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        uDL2D.setColor(Color.white);
        // uDL2D.fillRect(0, 0, 800, 800);
        uDL2D.fillRect(0, 0, labelHeight, labelWidth + min_label_height);
        uDL2D.setColor(Color.black);
        uDL2D.setFont(new Font("Arial", Font.BOLD, 12));

        if (this.settings.upsideDown) {
            for (int i = 1; i <= 8; i++) {
                uDL2D.drawString(new Integer(i).toString(), 3 + (labelHeight / 3), (square_height * (i - 1)) + addX);
            }
        } else {
            int j = 1;
            for (int i = 8; i > 0; i--, j++) {
                uDL2D.drawString(new Integer(i).toString(), 3 + (labelHeight / 3), (square_height * (j - 1)) + addX);
            }
        }
        uDL2D.dispose();
        this.LeftRightLabel = uDL;
    }

    /**
     * Method to draw Chessboard and their elements (pieces etc.)
     *
     * @deprecated
     */
    public void draw() {
        this.getGraphics().drawImage(boardImage, this.getTopLeftPoint().x, this.getTopLeftPoint().y, null);// draw an Image
        // of chessboard
        this.drawLabels();
        this.repaint();
    }/*--endOf-draw--*/

    public Point getTopLeftPoint() {
        if (this.settings.renderLabels) {
            return new Point(this.topLeft.x + this.upDownLabel.getHeight(null),
                    this.topLeft.y + this.upDownLabel.getHeight(null));
        }
        return this.topLeft;
    }

    public int get_height(boolean renderLabels) {
        if (renderLabels) {
            return this.boardImage.getHeight(null) + upDownLabel.getHeight(null);
        }
        return boardImage.getHeight(null);
    }

    public int getWidth() {
        return this.getHeight();
    }

    public void setVisual(Piece piece) {
        if (piece == null)
            return;
        this.pieceVisuals.put(piece, new PieceVisual(piece.player.color == piece.player.color.black ? piece.type + "-B.png" : piece.type + "-W.png"));
    }

    public void removeVisual(Piece piece) {
        if (piece != null && this.pieceVisuals.containsKey(piece))
            this.pieceVisuals.remove(piece);
    }

    public void setVisuals4NewGame() {
        setVisualsForPlayer(1);
        setVisualsForPlayer(2);
    }

    public void setVisualsForPlayer(int player){
        int rowPawns = player==1 ? 1 : 6;
        int rowPieces = player==1 ? 0 : 7;

        for (int x = 0; x < 8; x++) {
            setVisual(controller.getSquare(x, rowPawns).piece);
        }
        setVisual(controller.getSquare(0, rowPieces).piece);
        setVisual(controller.getSquare(1, rowPieces).piece);
        setVisual(controller.getSquare(2, rowPieces).piece);
        setVisual(controller.getSquare(3, rowPieces).piece);
        setVisual(controller.getSquare(4, rowPieces).piece);
        setVisual(controller.getSquare(5, rowPieces).piece);
        setVisual(controller.getSquare(6, rowPieces).piece);
        setVisual(controller.getSquare(7, rowPieces).piece);
    }

}
