package jchess.model;

import jchess.entities.Player;
import jchess.entities.Square;
import jchess.pieces.Piece;

import java.util.HashSet;
import java.util.List;

public interface IChessboardModel {

    int getRows();

    int getColumns();

    void addCrucialPiece(Piece piece);

    HashSet<Piece> getCrucialPieces(Player player);

    Square getSquare(int x, int y);

    boolean isInPromotionArea(Square square);

    Square getSquare(Piece piece);

    Square getSquare(int id);

    HashSet<Square> getSquaresBetween(Square one, Square two);

    Piece setPieceOnSquare(Piece piece, Square square);

    List<Square> getSquares();

}
