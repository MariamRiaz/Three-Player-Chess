package jchess.UI.board;

import jchess.pieces.Piece;

public class Square {
	private int pozX; // 0-7, becouse 8 squares for row/column
	private int pozY; // 0-7, becouse 8 squares for row/column
	private Piece piece = null;// object Piece on square (and extending Piecie)

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
