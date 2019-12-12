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

public class RoundChessboardModel {
    public List<Square> squares;
    public Piece kingWhite;
    public Piece kingBlack;
    public Piece kingGray;
    private int squaresPerRow;
    private int rows;
    private boolean hasContinuousRows;

    public RoundChessboardModel(int rows, int squaresPerRow, boolean continuousRows, Settings settings) {
        this.squares = new ArrayList<Square>();
        this.squaresPerRow = squaresPerRow;
        this.rows = rows;
        this.hasContinuousRows = continuousRows;
        
        populateSquares(rows, squaresPerRow);
        initializePieces(settings.getPlayerWhite(), settings.getPlayerBlack(), settings.getPlayerGray());
    }

    private void populateSquares(int rows, int squaresPerRow) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < squaresPerRow; j++) {
                squares.add(new Square(j, i, null));
            }
        }
    }
    
    public Square getSquare(int x, int y) { // duplicate method with GUI-related getSquare
    	int newy = hasContinuousRows ? (y % rows < 0 ? (y % rows) + rows : y % rows) : y;
    	
        Optional<Square> optionalSquare = squares.stream().filter(s ->
        	s.getPozX() == x && s.getPozY() == newy).findFirst();
        if(optionalSquare.equals(Optional.empty()))
            return null;
            
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
        return square == null ? null : square.getPiece();
    }


    public Piece setPieceOnSquare(Piece piece, Square square) {
        if (square == null) {
            Log.log(Level.WARNING, "Piece was not set on square because square is null");
            return null;
        }
        square.setPiece(piece);
        return piece;
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

        if (player.color == Player.colors.white)
            kingWhite = king;
        else if (player.color == Player.colors.gray)
        	kingGray = king;
        else kingBlack = king;
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
