package jchess.game;

import jchess.game.IGameController;

import javax.swing.*;
import java.awt.event.ComponentListener;

public abstract class AbstractGameView extends JPanel implements ComponentListener {

    public abstract IGameController getGameController();

}
