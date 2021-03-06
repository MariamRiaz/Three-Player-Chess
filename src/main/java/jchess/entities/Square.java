package jchess.entities;

import jchess.pieces.Piece;

public class Square {
	private int pozX;
	private int pozY;
	private Piece piece = null; // object Piece on square (and extending Piece)

	public Square(int pozX, int pozY, Piece piece) {
		this.pozX = pozX;
		this.pozY = pozY;
		this.piece = piece;
	}

	Square(Square square) {
		this.pozX = square.getPozX();
		this.pozY = square.getPozY();
		this.piece = square.getPiece();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof Square))
			return false;
		return ((Square)other).getPozX() == getPozX() && ((Square)other).getPozY() == getPozY();
	}
	
	public Piece getPiece() {
		return piece;
	}

	public void setPiece(Piece piece){
		this.piece = piece;
	}
	
	public int getPozX() {
		return pozX;
	}
	
	public int getPozY() {
		return pozY;
	}
}
