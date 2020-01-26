package jchess.controller;

import java.awt.event.MouseListener;
import java.util.Observer;

public interface IChessboardController extends MouseListener {

    ChessboardView getView();

    void addSelectSquareObserver(Observer observer);

}