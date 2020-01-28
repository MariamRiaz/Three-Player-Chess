package jchess.game;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * GameView Class which represents the View of a Game Component.
 */
public class GameView extends AbstractGameView implements ComponentListener {

    private IGameController gameController;
    private Component chessboardView;
    private Component moveHistoryView;
    private Component gameClockView;

    /**
     * Constructor for GameView Class.
     *
     * @param gameController  Game Controller of the View
     * @param chessboardView  View of the Chessboard within this Game
     * @param moveHistoryView View of the MoveHistory within this Game
     * @param gameClockView   View of the GameClock within this Game
     */
    public GameView(
            IGameController gameController,
            Component chessboardView,
            Component moveHistoryView,
            Component gameClockView) {

        this.setLayout(null);
        this.gameController = gameController;
        this.chessboardView = chessboardView;
        this.moveHistoryView = moveHistoryView;
        this.gameClockView = gameClockView;

        this.add(chessboardView);

        gameClockView.setSize(new Dimension(400, 100));
        gameClockView.setLocation(new Point(500, 0));
        this.add(gameClockView);

        moveHistoryView.setSize(new Dimension(245, 350));
        moveHistoryView.setLocation(new Point(500, 121));
        this.add(moveHistoryView);

        this.setDoubleBuffered(true);
        addComponentListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public IGameController getGameController() {
        return gameController;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        int chessHeight;
        chessHeight = this.chessboardView.getHeight();
        this.moveHistoryView.setLocation(new Point(chessHeight + 5, 100));
        this.moveHistoryView.setSize(moveHistoryView.getWidth(), chessHeight - 100);
        this.gameClockView.setLocation(new Point(chessHeight + 5, 0));
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
