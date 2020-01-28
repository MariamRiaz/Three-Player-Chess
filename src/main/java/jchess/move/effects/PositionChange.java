package jchess.move.effects;

import jchess.game.chessboard.model.Square;
import jchess.pieces.Piece;

public class PositionChange {
	private final Piece piece;
	private final Square square;
	
	public PositionChange(Piece piece, Square square) {
		this.piece = piece;
		this.square = square;
	}
	
	/**
	 * @return The moving Piece.
	 */
	public Piece getPiece() {
		return piece;
	}
	
	/**
	 * @return The target Square.
	 */
	public Square getSquare() {
		return square;
	}
}
