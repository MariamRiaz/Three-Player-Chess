package jchess.game.chessboard.view;

import jchess.game.chessboard.model.Square;
import jchess.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

public abstract class AbstractChessboardView extends JPanel {

    public abstract void resetActiveCell();

    public abstract void resetPossibleMoves();

    public abstract void setActiveCell(int x, int y);

    public abstract void setMoves(Set<Square> squares);

    public abstract void updateAfterMove();

    public abstract void removeVisual(int x, int y);

    public abstract void setVisual(Piece piece, int x, int y);

    public abstract List<? extends SquareView> getCells();

    public abstract Point getCircleCenter();

    public abstract void removeVisual(Square square);

}