package jchess.common;

import jchess.Settings;
import jchess.UI.board.Square;
import jchess.controller.ChessboardController;
import jchess.controller.RoundChessboardController;
import jchess.view.ChessboardView;
import jchess.view.RoundChessboardView;
import org.jdesktop.application.SingleFrameApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;

public class JChessTestUi extends SingleFrameApplication{


    public static void main(String...args) {
        launch(JChessTestUi.class, args);
    }
    @Override
    protected void startup() {
        JFrame frame = new JFrame();
        frame.setSize(new Dimension(1200,1200));
//        frame.setSize(new Dimension(800,800));
//        frame.add(new RoundChessboardView(1000, "3-player-board.png", 24, 6));
//        frame.add(new ChessboardViewTest(1000, 1000));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationByPlatform(true);
        Settings settings = new Settings();
        RoundChessboardController controller = new RoundChessboardController(settings);
        frame.add(controller.getView());
        controller.getView().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Square square = controller.getSquareFromClick(e.getX(), e.getY());
                controller.select(square);
                if(square.getPiece() != null) {
                    HashSet<Square> validSquares = controller.getValidTargetSquares(square.getPiece());
                    controller.getView().setMoves(validSquares);
                }
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
        });
        frame.setMinimumSize(frame.getSize());
        frame.setVisible(true);
    }


}
