package jchess.controller;

import jchess.model.ChessboardModel;
import jchess.view.ChessboardView;

public class ChessboardController {

    public ChessboardModel model;
    public ChessboardView view;

    public ChessboardController(ChessboardModel model, ChessboardView view){
        this.model = model;
        this.view = view;
    }


}
