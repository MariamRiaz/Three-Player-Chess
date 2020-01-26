package jchess.entities;

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
