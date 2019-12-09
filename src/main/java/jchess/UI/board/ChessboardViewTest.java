package jchess.UI.board;

import jchess.JChessApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChessboardViewTest extends JPanel implements MouseListener {

    private Image backgroundImage;
    private int width;
    private int height;

    public ChessboardViewTest(int width, int height) {
        super();
        URL url = JChessApp.class.getClassLoader().getResource("images.org/3-player-board.png");
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image image = tk.getImage(url);
        image = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        setPreferredSize(new Dimension(width, height));
        this.backgroundImage = image;
        this.width = width;
        this.height = height;
        this.addMouseListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, this);
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        CartesianPolarConverter converter = new CartesianPolarConverter();
        Point centerPoint = new Point(width/2, height/2);
        PolarCell polarCell = PolarCell.FromCartesian(centerPoint, e.getPoint(), 7.5, height/16);
        System.out.println("X: " + String.valueOf(e.getX()) + " ,Y: " + String.valueOf(e.getY()));
        System.out.println("Degrees: " + String.valueOf(polarCell.getCenterPoint().getDegrees()));
        System.out.println("Radius: "  + String.valueOf(polarCell.getCenterPoint().getRadius()));
        Point cartPoint = converter.getCartesianPointFromPolar(polarCell.getCenterPoint(), centerPoint);
        System.out.println("X: " + String.valueOf(cartPoint.getX()) + " ,Y: " + String.valueOf(cartPoint.getY()));
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
