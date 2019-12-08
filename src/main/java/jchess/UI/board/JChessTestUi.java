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
        frame.add(new ChessboardView(8,8));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationByPlatform(true);

        // ensures the frame is the minimum size it needs to be
        // in order display the components within it
        frame.pack();
        // ensures the minimum size is enforced.
        frame.setMinimumSize(frame.getSize());
        frame.setVisible(true);
    }
}
