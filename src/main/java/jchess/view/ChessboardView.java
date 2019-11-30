package jchess.view;

import jchess.GUI;
import jchess.UI.board.Chessboard;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ChessboardView extends JPanel {
    private Image boardImage;
    private Image selectedSquareImage = GUI.loadImage("sel_square.png");// image of highlighted square
    private Image ableSquareImage = GUI.loadImage("able_square.png");// image of square where piece can go
    private int chessBoardHeight = 480;
    private int chessBoardWidth = chessBoardHeight;
    private Image upDownLabel = null;
    private Image LeftRightLabel = null;

    public ChessboardView() {
    }

    public void initView(String chessBoardImagePath){
        this.boardImage = GUI.loadImage(chessBoardImagePath);
        this.setSize(chessBoardHeight, chessBoardWidth);
        this.setVisible(true);
        this.setLocation(new Point(0, 0));
    }

    public void resizeChessboard(boolean renderLabels) {
        int height = this.get_height(renderLabels);
        BufferedImage resized = new BufferedImage(height, height, BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics g = resized.createGraphics();
        g.drawImage(Chessboard.orgImage, 0, 0, height, height, null);
        g.dispose();
        Chessboard.image = resized.getScaledInstance(height, height, 0);
        this.square_height = (float) (height / 8);
        if (this.settings.renderLabels) {
            height += 2 * (this.upDownLabel.getHeight(null));
        }
        this.setSize(height, height);

        resized = new BufferedImage((int) square_height, (int) square_height, BufferedImage.TYPE_INT_ARGB_PRE);
        g = resized.createGraphics();
        g.drawImage(Chessboard.org_able_square, 0, 0, (int) square_height, (int) square_height, null);
        g.dispose();
        Chessboard.able_square = resized.getScaledInstance((int) square_height, (int) square_height, 0);

        resized = new BufferedImage((int) square_height, (int) square_height, BufferedImage.TYPE_INT_ARGB_PRE);
        g = resized.createGraphics();
        g.drawImage(Chessboard.org_sel_square, 0, 0, (int) square_height, (int) square_height, null);
        g.dispose();
        Chessboard.sel_square = resized.getScaledInstance((int) square_height, (int) square_height, 0);
        this.drawLabels();
    }

    public int get_height(boolean renderLabels) {
        if (renderLabels) {
            return this.boardImage.getHeight(null) + upDownLabel.getHeight(null);
        }
        return boardImage.getHeight(null);
    }

    public int getWidth(){
        return this.getHeight();
    }

}
