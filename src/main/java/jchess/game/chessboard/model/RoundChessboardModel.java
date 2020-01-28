package jchess.game.chessboard.model;

import jchess.game.player.Player;
import jchess.pieces.Piece;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * Class that holds the state of the RoundChessboard component.
 */
public class RoundChessboardModel implements IChessboardModel {
    private List<Square> squares;
    private HashSet<Piece> crucialPieces = new HashSet<>();
    private int squaresPerRow;
    private int rows;
    private boolean hasContinuousRows, innerRimConnected;

    /**
     * Constructor.
     *
     * @param rows              int     row count of the chessboard
     * @param squaresPerRow     int     count of squares per row of the chessboard
     * @param continuousRows    boolean
     * @param connectedInnerRim boolean
     */

    public RoundChessboardModel(int rows, int squaresPerRow, boolean continuousRows, boolean connectedInnerRim) {
        this.rows = rows;
        this.squaresPerRow = squaresPerRow;
        this.hasContinuousRows = continuousRows;
        this.innerRimConnected = connectedInnerRim;
        this.squares = new ArrayList<>();
        populateSquares();
    }


    private void populateSquares() {
        squares.clear();

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < squaresPerRow; j++)
                squares.add(new Square(j, i, null));
    }

    /**
     * Converts the given Square y index to an array index in the list of Squares of this board, taking board linkage and into account.
     *
     * @param y The index to convert, e.g. -1 or 10.
     * @return The corresponding array index between 0 and the number of rows in the board minus one.
     */
    private int normalizeY(int y) {
        return y % rows < 0 ? (y % rows) + rows : y % rows;
    }

    /**
     * {@inheritDoc}
     */
    public int getRows() {
        return rows;
    }

    /**
     * {@inheritDoc}
     */
    public int getColumns() {
        return squaresPerRow;
    }

    /**
     * {@inheritDoc}
     */
    public void addCrucialPiece(Piece piece) {
        if (piece == null)
            return;

        crucialPieces.add(piece);
    }

    /**
     * {@inheritDoc}
     */
    public HashSet<Piece> getCrucialPieces(Player player) {
        if (player == null)
            return new HashSet<>();

        HashSet<Piece> retVal = new HashSet<>();
        for (Piece el : crucialPieces)
            if (el.getPlayer().getColor().equals(player.getColor()))
                retVal.add(el);

        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    public HashSet<Piece> getCrucialPieces() {
        HashSet<Piece> retVal = new HashSet<>();
        for (Piece el : crucialPieces)
            retVal.add(el);
        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    public Square getSquare(int x, int y) {
        if (hasContinuousRows) {
            y = normalizeY(y);

            if (innerRimConnected && x < 0) {
                x = -x - 1;
                y = normalizeY(y + rows / 2);
            }
        }

        final int newX = x, newY = y;
        Optional<Square> optionalSquare = squares.stream().filter(s ->
                s.getPozX() == newX && s.getPozY() == newY).findFirst();
        if (optionalSquare.equals(Optional.empty()))
            return null;

        return optionalSquare.get();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInPromotionArea(Square square) {
        return square != null && square.getPozX() == 5;
    }

    /**
     * @return Whether or not the board inner rim is connected, if circular. I.e. whether jumps across the middle are possible.
     */
    public boolean getInnerRimConnected() {
        return hasContinuousRows && innerRimConnected;
    }

    /**
     * {@inheritDoc}
     */
    public Square getSquare(Piece piece) {
        if (piece == null)
            return null;

        return getSquare(piece.getID());
    }

    /**
     * {@inheritDoc}
     */
    public Square getSquare(int id) {
        Optional<Square> optionalSquare = squares.stream().filter(s -> s.getPiece() != null && s.getPiece().getID() == id).findFirst();
        if (optionalSquare.equals(Optional.empty()))
            return null;

        return optionalSquare.get();//TODO
    }

    /**
     * {@inheritDoc}
     */
    public HashSet<Square> getSquaresBetween(Square one, Square two) {
        HashSet<Square> retVal = new HashSet<>();

        if (one == null || two == null)
            return retVal;

        final int minX = Math.min(one.getPozX(), two.getPozX()), maxX = Math.max(one.getPozX(), two.getPozX()),
                minY = Math.min(one.getPozY(), two.getPozY()), maxY = Math.max(one.getPozY(), two.getPozY());

        for (int i = minX; i <= maxX; i++)
            for (int j = minY; j <= maxY; j++)
                retVal.add(this.getSquare(i, j));

        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    public Piece setPieceOnSquare(Piece piece, Square square) {
        Square prev = getSquare(piece);
        if (prev != null)
            prev.setPiece(null);

        if (square != null)
            square.setPiece(piece);
        return piece;
    }

    /**
     * {@inheritDoc}
     */
    public List<Square> getSquares() {
        return squares;
    }
}
