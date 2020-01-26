package jchess.view;

import jchess.controller.GameController;

import javax.swing.*;

public class GameView extends JPanel {

    private GameController gameController;

    public GameView(GameController gameController) {
        this.setLayout(null);
        this.gameController = gameController;
    }


    public GameController getGameController() {
        return gameController;
    }
}
