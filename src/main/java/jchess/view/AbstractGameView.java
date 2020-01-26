package jchess.view;

import jchess.controller.IGameController;

import javax.swing.*;
import java.awt.event.ComponentListener;

public abstract class AbstractGameView extends JPanel implements ComponentListener {

    public abstract IGameController getGameController();

}
