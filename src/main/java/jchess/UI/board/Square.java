package jchess.UI.board;

import jchess.pieces.Piece;

public class Square {
	public int pozX; // 0-7, becouse 8 squares for row/column
	public int pozY; // 0-7, becouse 8 squares for row/column
	public Piece piece = null;// object Piece on square (and extending Piecie)

	Square(int pozX, int pozY, Piece piece) {
		this.pozX = pozX;
		this.pozY = pozY;
		this.piece = piece;
	}

	Square(Square square) {
		this.pozX = square.pozX;
		this.pozY = square.pozY;
		this.piece = square.piece;
	}
	
	public Piece getPiece() {
		return piece;
	}
	
	public int getX() {
		return pozX;
	}
	
	public int getY() {
		return pozY;
	}
}
