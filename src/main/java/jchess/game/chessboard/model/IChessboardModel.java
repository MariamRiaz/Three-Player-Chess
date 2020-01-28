package jchess.game.chessboard.model;

import jchess.game.player.Player;
import jchess.pieces.Piece;

import java.util.HashSet;
import java.util.List;

/**
 * Interface that must be implemented by a ChessBoard Model.
 */
public interface IChessboardModel {

    /**
     * @return The number of rows.
     */
    int getRows();

    /**
     * @return The number of Squares per row.
     */
    int getColumns();

    /**
     * Adds the given Piece to the list of crucial Pieces, which when taken cause their Player to lose.
     *
     * @param piece The Piece to add.
     */
    void addCrucialPiece(Piece piece);

    /**
     * @param player The Player whose Pieces to return.
     * @return The Pieces of the given Player, which cause them to lose if they are taken.
     */
    HashSet<Piece> getCrucialPieces(Player player);

    /**
     * @return The Pieces of the given Player, which cause them to lose if they are taken.
     */
    HashSet<Piece> getCrucialPieces();

    /**
     * gets the Square corresponding to the given x and y index.
     *
     * @param x int x index of the desired square
     * @param y int y index of the desired square
     * @return Square corresponding to the given x and y index
     */
    Square getSquare(int x, int y);

    /**
     * Determines whether or not the given Square is in the area where e.g. pawns of the given player are promoted.
     *
     * @param square The Square to check.
     * @return Whether the given Player's Pieces upon reaching the given Square may be promoted.
     */
    boolean isInPromotionArea(Square square);

    /**
     * gets the Square where the given Piece is located on.
     *
     * @param piece Piece to get the Square from
     * @return Square where the given Piece is located on
     */
    Square getSquare(Piece piece);

    /**
     * gets the Square where the given Piece is located on.
     *
     * @param id id of Piece to get the Square from
     * @return Square where the given Piece is located on
     */
    Square getSquare(int id);

    /**
     * Gets all Squares between the two given Squares, including both of them.
     *
     * @param one The first Square.
     * @param two The second Square.
     * @return The Squares between the two given ones, including them.
     */
    HashSet<Square> getSquaresBetween(Square one, Square two);

    /**
     * sets the given Piece on the given Square.
     *
     * @param piece  Piece   piece to put on the Square
     * @param square Square  square where to put the piece on
     * @return Piece   after setting it on the Square
     */
    Piece setPieceOnSquare(Piece piece, Square square);

    /**
     * Gets a list of all Squares that make up this board model.
     */
    List<Square> getSquares();

}
