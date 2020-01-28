package jchess.game.clock;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractGameClockView extends JPanel {

    public abstract void updateClocks(int[] timeSpentByPlayers);

    public abstract void setPlayerNames(String playerOne, String playerTwo, String playerThree);


}
