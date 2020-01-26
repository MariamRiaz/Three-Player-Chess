package jchess.view;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractGameClockView extends JPanel {

    public abstract void paint(Graphics g);

    public abstract void update(Graphics g);

    public abstract void updateClocks(int[] timeSpentByPlayers);


}
