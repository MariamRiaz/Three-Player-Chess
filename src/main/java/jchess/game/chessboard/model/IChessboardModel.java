package jchess.game.chessboard.model;

import jchess.game.player.Player;
import jchess.pieces.Piece;

import java.util.HashSet;
import java.util.List;

/**
 * Interface that must be implemented by all models that represent a chessboard.
 */
public interface IChessboardModel {

    /**
     * Getter for the number of rows of the chessboard.
     *
     * @return Returns the number of rows of the chessboard.
     */
    int getRows();

    /**
     * Getter for the number of columns of the chessboard.
     *
     * @return Returns the number of columns of the chessboard.
     */
    int getColumns();

    /**
     * Adds a new crucial piece to the chessboard. (cannot be captured)
     *
     * @param piece The crucial piece to be added.
     */
    void addCrucialPiece(Piece piece);

    /**
     * Retrieves a set of all crucial pieces belonging to a player. (pieces that cannot be captured)
     *
     * @param player The player the pieces belong to.
     * @return A set of crucial pieces.
     */
    HashSet<Piece> getCrucialPieces(Player player);

    /**
     * Retrieves a set of all crucial pieces, belonging to all players. (pieces that cannot be captured)
     *
     * @return A set of all crucial pieces.
     */
    HashSet<Piece> getCrucialPieces();

    /**
     * Retrieves a square (board-cell) based on the given row- and column coordinates.
     *
     * @param x The column position.
     * @param y The row position.
     * @return The square instance that represents a board-cell.
     */
    Square getSquare(int x, int y);

    /**
     * Checks whether a square is inside the promotion area. (For pawns for example)
     *
     * @param square The square that will be checked.
     * @return True if the square will be considered a promotion-square, false otherwise.
     */
    boolean isInPromotionArea(Square square);

    /**
     * Retrieves a square by looking at the piece that's on it.
     *
     * @param piece The given piece.
     * @return The square where the piece is located.
     */
    Square getSquare(Piece piece);

    /**
     * Retrieves a square a piece is located on given the piece identifier.
     *
     * @param pieceId The identifier of a piece.
     * @return The square where the piece is currently situated.
     */
    Square getSquare(int pieceId);

    /**
     * Retrieves all the squares between two given squares.
     *
     * @param one The first square, lower limit.
     * @param two The second square, upper limit.
     * @return A set of squares between the two given squares.
     */
    HashSet<Square> getSquaresBetween(Square one, Square two);

    /**
     * Maps a piece to a square.
     *
     * @param piece  The given piece.
     * @param square The given square the piece needs to be mapped to.
     */
    void setPieceOnSquare(Piece piece, Square square);

    /**
     * Getter that returns all the squares in the model.
     *
     * @return A list containing all the squares.
     */
    List<Square> getSquares();

}
