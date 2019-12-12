package jchess.model;

import jchess.Log;
import jchess.Player;
import jchess.Settings;
import jchess.UI.board.Square;
import jchess.pieces.Piece;
import jchess.pieces.PieceFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
/*
* Class that will initialze the board and pieces and will have the records where the initail pieces should be once the board is set
* */


public class RoundChessboardModel {

    public List<Square> squares;
    public Piece kingWhite;
    public Piece kingBlack;
    private int squaresPerRow;
    private int rows;

    public static int botoom = 5;
    public static int top = 0;


    public RoundChessboardModel(int rows, int squaresPerRow, Settings settings) {
        this.squares = new ArrayList<Square>();
        this.squaresPerRow = squaresPerRow;
        this.rows = rows;
        populateSquares(rows, squaresPerRow);
        initializePieces(settings.playerWhite, settings.playerBlack, settings.playerGray);
    }

    private void populateSquares(int rows, int squaresPerRow) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < squaresPerRow; j++) {
                squares.add(new Square(j, i, null));
            }
        }
    }

    public Square getSquare(int x, int y) { // duplicate method with GUI-related getSquare
        Optional<Square> optionalSquare =  squares.stream().filter(s -> s.getPozX() == x && s.getPozY() == y).findFirst();
        if(optionalSquare.equals(Optional.empty())) {
            return null;
        }
        return optionalSquare.get();//TODO
    }

    public Square getSquare(Piece piece) {
        Optional<Square> optionalSquare = squares.stream().filter(s -> s.getPiece() == piece).findFirst();
        if(optionalSquare.equals(Optional.empty())) {
            return null;
        }
        return optionalSquare.get();//TODO
    }

    public Piece getPiece(int x, int y) {
        return getPiece(getSquare(x, y));
    }

    public Piece getPiece(Square square) {
        return square.getPiece();
    }


    public Piece setPieceOnSquare(Piece piece, Square square) {
        if (piece == null) {
            Log.log(Level.WARNING, "Piece was not set on square because piece is null");
            return null;
        }
        if (square == null) {
            Log.log(Level.WARNING, "Piece was not set on square because square is null");
            return null;
        }
        square.setPiece(piece);
        return piece;
    }

    public void removePieceFromSquare(Square square) {
        square.setPiece(null);
    }

    private void initializePiecesForPlayer(Player player, int row) {
        initializePawnsForPlayer(player, row);
        initializeHeavyPiecesForPlayer(player, row);
    }

    private void initializeHeavyPiecesForPlayer(Player player, int row) {
        Piece king = PieceFactory.createKing(player);
        setPieceOnSquare(PieceFactory.createRook(player), getSquare(0, row + 1));
        setPieceOnSquare(PieceFactory.createKnight(player), getSquare(1, row + 1));
        setPieceOnSquare(PieceFactory.createQueen(player), getSquare(2, row + 1));
        setPieceOnSquare(king, getSquare(3, row + 1));
        setPieceOnSquare(PieceFactory.createKnight(player), getSquare(4, row + 1));
        setPieceOnSquare(PieceFactory.createRook(player), getSquare(5, row + 1));

        if (player.color == Player.colors.white) {
            kingWhite = king;
        } else {
            kingBlack = king;
        }
    }

    private void initializePawnsForPlayer(Player player, int row) {
        for (int i = 0; i < squaresPerRow; i++) {
            setPieceOnSquare(PieceFactory.createPawn(player, true), getSquare(i, row));
            setPieceOnSquare(PieceFactory.createPawn(player, false), getSquare(i, row + 2));
        }
    }

    public void initializePieces(Player plWhite, Player plBlack, Player plGray) {
        Player player1 = plBlack;
        Player player2 = plWhite;
        Player player3 = plGray;

        initializePiecesForPlayer(player1, 0);
        initializePiecesForPlayer(player2, 8);
        initializePiecesForPlayer(player3, 16);
    }
}
