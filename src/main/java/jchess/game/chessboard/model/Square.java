package jchess.game.chessboard.model;

import jchess.pieces.Piece;

/**
 * Class template for representing square objects on the board
 **/

public class Square {
	private int pozX;
	private int pozY;
	private Piece piece = null; // object Piece on square
	/**
	* @param pozX X coordinate of the square
	* @param pozY Y coordinate of the square
	* @param piece current piece on the square, if any, else null
	 **/
	public Square(int pozX, int pozY, Piece piece) {
		this.pozX = pozX;
		this.pozY = pozY;
		this.piece = piece;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Square))
			return false;
		return ((Square)other).getPozX() == getPozX() && ((Square)other).getPozY() == getPozY();
	}
	
	@Override
	public String toString() {
		return "(" + Integer.toString(pozX) + ", " + Integer.toString(pozY) + ")";
	}
	
	/**
	* getter method for piece
	 **/
	public Piece getPiece() {
		return piece;
	}
	/**
	* setter method for piece
	 **/
	public void setPiece(Piece piece){
		this.piece = piece;
	}
	/**
	 * getter method for PozX
	 **/
	public int getPozX() {
		return pozX;
	}
	/**
	 * getter method for PozY
	 **/
	public int getPozY() {
		return pozY;
	}
}
