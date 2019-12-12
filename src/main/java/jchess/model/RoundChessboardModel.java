package jchess.model;

import jchess.helper.Log;
import jchess.entities.Player;
import jchess.Settings;
import jchess.entities.Square;
import jchess.pieces.Piece;
import jchess.pieces.PieceFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Class that holds the state of the RoundChessboard component
 */
public class RoundChessboardModel {
    public List<Square> squares;
    public Piece kingWhite;
    public Piece kingBlack;
    public Piece kingGray;
    private int squaresPerRow;
    private int rows;
    private boolean hasContinuousRows;

    /**
     * Constructor
     * @param rows              int     row count of the chessboard
     * @param squaresPerRow     int     count of squares per row of the chessboard
     * @param continuousRows    boolean
     * @param settings          Settings    settings of the application
     */
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

    /**
     * gets the Square corresponding to the given x and y index
     * @param x     int x index of the desired square
     * @param y     int y index of the desired square
     * @return      Square corresponding to the given x and y index
     */
    public Square getSquare(int x, int y) {
    	int newY = hasContinuousRows ? (y % rows < 0 ? (y % rows) + rows : y % rows) : y;
    	
        Optional<Square> optionalSquare = squares.stream().filter(s ->
        	s.getPozX() == x && s.getPozY() == newY).findFirst();
        if(optionalSquare.equals(Optional.empty()))
            return null;
            
        return optionalSquare.get();//TODO
    }

    /**
     * gets the Square where the given Piece is located on
     * @param piece     Piece to get the Square from
     * @return          Square where the given Piece is located on
     */
    public Square getSquare(Piece piece) {
        Optional<Square> optionalSquare = squares.stream().filter(s -> s.getPiece() == piece).findFirst();
        if(optionalSquare.equals(Optional.empty())) {
            return null;
        }
        return optionalSquare.get();//TODO
    }

    /**
     * sets the given Piece on the given Square
     * @param piece     Piece   piece to put on the Square
     * @param square    Square  square where to put the piece on
     * @return          Piece   after setting it on the Square
     */
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

    /**
     * initializes Pieces for given players on startup
     * @param plWhite   Player white player
     * @param plBlack   Player black player
     * @param plGray    Player gray player
     */
    public void initializePieces(Player plWhite, Player plBlack, Player plGray) {
        Player player1 = plBlack;
        Player player2 = plWhite;
        Player player3 = plGray;

        initializePiecesForPlayer(player1, 0);
        initializePiecesForPlayer(player2, 8);
        initializePiecesForPlayer(player3, 16);
    }
}
