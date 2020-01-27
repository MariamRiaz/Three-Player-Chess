package jchess.game.chessboard.controller;

import jchess.game.chessboard.model.Square;

import java.util.Observable;

public class SquareObservable extends Observable {

    private Square square;

    public Square getSquare() {
        return square;
    }

    public void setSquare(Square square) {
        setChanged();
        notifyObservers(square);
        this.square = square;
    }
}
