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
    public Square activeSquare;
    public int active_x_square;
    public int active_y_square;



    public HashMap<Piece, PieceVisual> pieceVisuals = new HashMap<Piece, PieceVisual>();
    public HashMap<Piece, Square> pieceToSquare = new HashMap<Piece, Square>();


    public ChessboardModel(Settings settings) {

        this.settings = settings;
        this.squares = new Square[8][8];// initalization of 8x8 chessboard
        for (int i = 0; i < 8; i++) {// create object for each square
            for (int y = 0; y < 8; y++) {
                this.squares[i][y] = new Square(i, y, null);
            }
        }

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
     * @param upsideDown  bool
     * @param plWhite reference to white player
     * @param plBlack reference to black player
     */
    public void setPieces4NewGame(boolean upsideDown, Player plWhite, Player plBlack) {

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
        }
    }
}
