package jchess.UI.board;

import jchess.Settings;
import jchess.pieces.MoveHistory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ChessboardView extends JPanel {

    private List<List<JButton>> squares;// squares of chessboard
//    private GridLayout layout;

    public ChessboardView(int width, int height) {
        super(new GridLayout(width, height));
        this.squares = new ArrayList<List<JButton>>();
        fillSquares();
//        this.layout = new GridLayout(squares.get(0).size(), squares.size());
        for (List<JButton> row : squares
        ) {
            for (JButton square : row
            ) {
                this.add(square);
            }
        }
    }

    private void fillSquares() {
        int squareHeight = 64;
        int squareWidth = 64;
        for (int i = 0; i < 4; i++) {
            squares.add(squareRow(squareHeight, squareWidth, Color.BLACK, Color.WHITE));
            squares.add(squareRow(squareHeight, squareWidth, Color.WHITE, Color.BLACK));
        }
    }

    private List<JButton> squareRow(int squareHeight, int squareWidth, Color firstColor, Color secondColor) {
        List<JButton> row = new ArrayList();
        for (int i = 0; i < 4; i++) {
            row.add(createSquare(firstColor, squareHeight, squareWidth));
            row.add(createSquare(secondColor, squareHeight, squareWidth));
        }
        return row;
    }


    private JButton createSquare(Color color, int height, int width) {
        JButton button = new JButton();
        button.setMargin(new Insets(0, 0, 0, 0));
        ImageIcon image = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
        button.setIcon(image);
        button.setBackground(color);
//        button.setForeground(color);
//        button.setPreferredSize(new Dimension(64,64));
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        return button;
    }
}
