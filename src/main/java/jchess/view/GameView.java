package jchess.view;

import jchess.controller.IGameController;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class GameView extends AbstractGameView implements ComponentListener{

    private IGameController gameController;
    private Component chessboardView;
    private Component moveHistoryView;
    private Component gameClockView;


    public GameView(IGameController gameController, Component chessboardView, Component moveHistoryView, Component gameClockView) {
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

    public IGameController getGameController() {
        return gameController;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        int chess_height;
        chess_height = this.chessboardView.getHeight();
        this.moveHistoryView.setLocation(new Point(chess_height + 5, 100));
        this.moveHistoryView.setSize(moveHistoryView.getWidth(), chess_height - 100);
        this.gameClockView.setLocation(new Point(chess_height + 5, 0));
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
