package jchess.game;

import java.awt.event.ComponentListener;
import javax.swing.*;


/**
 * Abstract representation of a view of a game.
 */
public abstract class AbstractGameView extends JPanel implements ComponentListener {

    public abstract IGameController getGameController();

}
