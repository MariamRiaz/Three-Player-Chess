package jchess.game.chessboard.controller;

import jchess.game.player.Player;
import jchess.game.chessboard.model.Square;
import jchess.game.chessboard.view.AbstractChessboardView;
import jchess.game.chessboard.model.IChessboardModel;
import jchess.move.effects.MoveEffect;
import jchess.pieces.Piece;

import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.List;
import java.util.Observer;

public interface IChessboardController extends MouseListener {

    AbstractChessboardView getView();

    void addSelectSquareObserver(Observer observer);

    boolean moveIsPossible(int fromX, int fromY, int toX, int toY);

    boolean moveIsPossible(Square squareFrom, Square squareTo);

    void select(Square sq);

    boolean pieceIsUnsavable(Piece piece);

    HashSet<Piece> getCrucialPieces(Player player);

    Square getActiveSquare();

    void setActiveSquare(Square square);

    Square getSquare(Piece piece);

    List<Square> getSquares();

    void move(Square begin, Square end, boolean refresh, boolean clearForwardHistory);

    void apply(MoveEffect me);

    void reverse(MoveEffect me);

    void unselect();

    boolean undo();

    boolean redo();

    Square getSquareFromClick(int x, int y);

    Square getSquare(int x, int y);

    IChessboardModel getModel();

}