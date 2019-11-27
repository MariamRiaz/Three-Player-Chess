package jchess.model;

import jchess.Log;
import jchess.Player;
import jchess.Settings;
import jchess.UI.board.Square;
import jchess.pieces.*;

import java.util.HashMap;
import java.util.logging.Level;

public class ChessboardModel {

    public Settings settings;
    public Square squares[][];// squares of chessboard
    public Piece kingWhite;
    public Piece kingBlack;

    public HashMap<Piece, PieceVisual> pieceVisuals = new HashMap<Piece, PieceVisual>();
    private HashMap<Piece, Square> pieceToSquare = new HashMap<Piece, Square>();


    public ChessboardModel(Settings settings) {

        this.settings = settings;
        this.squares = new Square[8][8];// initalization of 8x8 chessboard

    }

    public Square getSquare(Piece piece) {
        return piece != null && pieceToSquare.containsKey(piece) ? pieceToSquare.get(piece) : null;
    }

    public Square getSquare(int x, int y) { // duplicate method with GUI-related getSquare
        return x < 0 || y < 0 || x >= this.squares.length || y >= this.squares[x].length ? null : this.squares[x][y];
    }

    public Piece getPiece(int x, int y) {
        return getPiece(getSquare(x, y));
    }

    public Piece getPiece(Square square) {
        return square.piece;
    }

    public Piece setPieceOnSquare(Piece piece, Square square) {
        if (piece == null)
            return null;

        if (pieceToSquare.containsKey(piece)) {
            pieceToSquare.get(piece).piece = null;
            pieceToSquare.remove(piece);
        }

        if (square != null) {
            setPieceOnSquare(square.piece, null);
            square.piece = piece;
            pieceToSquare.put(piece, square);
        }

        return piece;
    }

    /**
     * Method setPieces on begin of new game or loaded game
     *
     * @param places  string with pieces to set on chessboard
     * @param plWhite reference to white player
     * @param plBlack reference to black player
     */
    public void setPieces(String places, Player plWhite, Player plBlack) {

        if (places.equals("")) // if newGame
        {
            if (this.settings.upsideDown) {
                this.setPieces4NewGame(true, plWhite, plBlack);
            } else {
                this.setPieces4NewGame(false, plWhite, plBlack);
            }

        } else // if loadedGame
        {
            return;
        }
    }/*--endOf-setPieces--*/

    /**
     *
     */
    private void setPieces4NewGame(boolean upsideDown, Player plWhite, Player plBlack) {

        /* WHITE PIECES */
        Player player = plBlack;
        Player player1 = plWhite;
        if (upsideDown) // if white on Top
        {
            player = plWhite;
            player1 = plBlack;
        }
        this.setFigures4NewGame(0, player, upsideDown);
        this.setPawns4NewGame(1, player);
        this.setFigures4NewGame(7, player1, upsideDown);
        this.setPawns4NewGame(6, player1);
    }/*--endOf-setPieces(boolean upsideDown)--*/

    /**
     * method set Figures in row (and set Queen and King to right position)
     *
     * @param i          row where to set figures (Rook, Knight etc.)
     * @param player     which is owner of pawns
     * @param upsideDown if true white pieces will be on top of chessboard
     */
    private void setFigures4NewGame(int i, Player player, boolean upsideDown) {

        if (i != 0 && i != 7) {
            Log.log(Level.SEVERE, "error setting figures like rook etc.");
            return;
        } else if (i == 0) {
            player.goDown = true;
        }

        setPieceOnSquare(PieceFactory.createRook(player), getSquare(0, i));
        setPieceOnSquare(PieceFactory.createRook(player), getSquare(7, i));
        setPieceOnSquare(PieceFactory.createKnight(player), getSquare(1, i));
        setPieceOnSquare(PieceFactory.createKnight(player), getSquare(6, i));
        setPieceOnSquare(PieceFactory.createBishop(player), getSquare(2, i));
        setPieceOnSquare(PieceFactory.createBishop(player), getSquare(5, i));

        if (upsideDown) {
            setPieceOnSquare(PieceFactory.createQueen(player), getSquare(4, i));

            if (player.color == Player.colors.white)
                kingWhite = setPieceOnSquare(PieceFactory.createKing(player), getSquare(3, i));
            else
                kingBlack = setPieceOnSquare(PieceFactory.createKing(player), getSquare(3, i));
        } else {
            setPieceOnSquare(PieceFactory.createQueen(player), getSquare(3, i));

            if (player.color == Player.colors.white)
                kingWhite = setPieceOnSquare(PieceFactory.createKing(player), getSquare(4, i));
            else
                kingBlack = setPieceOnSquare(PieceFactory.createKing(player), getSquare(4, i));
        }

        setVisual(getSquare(0, i).piece);
        setVisual(getSquare(1, i).piece);
        setVisual(getSquare(2, i).piece);
        setVisual(getSquare(3, i).piece);
        setVisual(getSquare(4, i).piece);
        setVisual(getSquare(5, i).piece);
        setVisual(getSquare(6, i).piece);
        setVisual(getSquare(7, i).piece);
    }

    /**
     * method set Pawns in row
     *
     * @param i      row where to set pawns
     * @param player player which is owner of pawns
     */
    private void setPawns4NewGame(int i, Player player) {
        if (i != 1 && i != 6) {
            Log.log(Level.SEVERE, "error setting pawns etc.");
            return;
        }
        for (int x = 0; x < 8; x++) {
            setPieceOnSquare(PieceFactory.createPawn(player, !player.goDown), getSquare(x, i));
            setVisual(getSquare(x, i).piece);
        }
    }

    private void setVisual(Piece piece) {
        if (piece == null)
            return;
        this.pieceVisuals.put(piece, new PieceVisual(piece.player.color == piece.player.color.black ? piece.type + "-B.png" : piece.type + "-W.png"));
    }
}
