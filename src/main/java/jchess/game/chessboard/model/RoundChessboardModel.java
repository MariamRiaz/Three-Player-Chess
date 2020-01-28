package jchess.game.chessboard.model;

import jchess.game.player.Player;
import jchess.pieces.Piece;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * Class that holds the state of the RoundChessboard component
 */
public class RoundChessboardModel implements IChessboardModel {
    private List<Square> squares;
    private HashSet<Piece> crucialPieces = new HashSet<>();
    private int squaresPerRow;
    private int rows;
    private boolean hasContinuousRows, innerRimConnected;

    /**
     * Constructor
     * @param rows              int     row count of the chessboard
     * @param squaresPerRow     int     count of squares per row of the chessboard
     * @param continuousRows    boolean
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
     * @param y The index to convert, e.g. -1 or 10.
     * @return The corresponding array index between 0 and the number of rows in the board minus one.
     */
    private int normalizeY(int y) {
    	return y % rows < 0 ? (y % rows) + rows : y % rows;
    }
    
    /**
     * @return The number of rows.
     */
    public int getRows() {
    	return rows;
    }
    
    /**
     * @return The number of Squares per row.
     */
    public int getColumns() {
    	return squaresPerRow;
    }
    
    /**
     * Adds the given Piece to the list of crucial Pieces, which when taken cause their Player to lose.
     * @param piece The Piece to add.
     */
    public void addCrucialPiece(Piece piece) {
    	if (piece == null)
    		return;
    	
    	crucialPieces.add(piece);
    }
    
    /**
     * @param player The Player whose Pieces to return.
     * @return The Pieces of the given Player, which cause him to lose if they are taken.
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
     * @param player The Player whose Pieces to return.
     * @return The Pieces of the given Player, which cause him to lose if they are taken.
     */
    public HashSet<Piece> getCrucialPieces() {
    	HashSet<Piece> retVal = new HashSet<>();
    	for (Piece el : crucialPieces)
    		retVal.add(el);
    	
    	return retVal;
    }

    /**
     * gets the Square corresponding to the given x and y index
     * @param x     int x index of the desired square
     * @param y     int y index of the desired square
     * @return      Square corresponding to the given x and y index
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
        if(optionalSquare.equals(Optional.empty()))
            return null;
            
        return optionalSquare.get();
    }
    
    /**
     * Determines whether or not the given Square is in the area where e.g. pawns of the given player are promoted.
     * @param square The Square to check.
     * @param color The color of the Player for whom to check.
     * @return Whether the given Player's Pieces upon reaching the given Square may be promoted.
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
     * gets the Square where the given Piece is located on
     * @param piece     Piece to get the Square from
     * @return          Square where the given Piece is located on
     */
    public Square getSquare(Piece piece) {
    	if (piece == null)
    		return null;
    	
    	return getSquare(piece.getID());
    }
    
    /**
     * gets the Square where the given Piece is located on
     * @param id     	id of Piece to get the Square from
     * @return          Square where the given Piece is located on
     */
    public Square getSquare(int id) {
        Optional<Square> optionalSquare = squares.stream().filter(s -> s.getPiece() != null && s.getPiece().getID() == id).findFirst();
        if(optionalSquare.equals(Optional.empty()))
            return null;
        
        return optionalSquare.get();//TODO
    }
    
    /**
     * Gets all Squares between the two given Squares, including both of them.
     * @param one The first Square.
     * @param two The second Square.
     * @return The Squares between the two given ones, including them.
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
     * sets the given Piece on the given Square
     * @param piece     Piece   piece to put on the Square
     * @param square    Square  square where to put the piece on
     * @return          Piece   after setting it on the Square
     */
    public Piece setPieceOnSquare(Piece piece, Square square) {        
        Square prev = getSquare(piece);
        if (prev != null)
        	prev.setPiece(null);
        
        if (square != null)
        	square.setPiece(piece);
        return piece;
    }
    
    public List<Square> getSquares() {
    	return squares;
    }
}
