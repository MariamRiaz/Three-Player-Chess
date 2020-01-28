package jchess.game;


import java.awt.event.ComponentListener;
import javax.swing.JPanel;

/**
 * Abstract Class that must be extended by a Game View.
 */
public abstract class AbstractGameView extends JPanel implements ComponentListener {

    /**
     * getter for the corresponding Game Controller of this View.
     *
     * @return Game Controller
     */
    public abstract IGameController getGameController();

}
