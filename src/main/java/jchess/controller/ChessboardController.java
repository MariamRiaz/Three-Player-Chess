package jchess.controller;

import jchess.Settings;
import jchess.model.ChessboardModel;
import jchess.view.ChessboardView;

public class ChessboardController {

    public ChessboardModel model;
    public ChessboardView view;
    public Settings settings;

    public ChessboardController(ChessboardModel model, ChessboardView view, Settings settings){
        this.model = model;
        this.view = view;
        this.settings = settings;
    }

    public void initView(){
        this.view.initView("chessboard.png");
    }

    public void repaint(){
        this.view.repaint();
    }

    public void setPieces4NewGame(){
        this.model.setPieces4NewGame(settings.upsideDown, settings.playerWhite, settings.playerBlack);
    }

    public void resizeChessboard(){
        this.view.resizeChessboard(settings.renderLabels);
    }

}
