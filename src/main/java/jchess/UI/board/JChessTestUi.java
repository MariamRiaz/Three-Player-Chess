package jchess.UI.board;

import org.jdesktop.application.SingleFrameApplication;

import javax.swing.*;
import java.awt.*;

public class JChessTestUi extends SingleFrameApplication {


    public static void main(String...args) {
        launch(JChessTestUi.class, args);
    }
    @Override
    protected void startup() {
        JFrame frame = new JFrame();
        frame.setSize(new Dimension(1200,1200));
//        frame.setSize(new Dimension(800,800));
        int xCenter = 500;
        int yCenter = 500;
        frame.add(new ChessboardViewTest(1000,1000));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationByPlatform(true);

        frame.setMinimumSize(frame.getSize());
        frame.setVisible(true);
    }



}
