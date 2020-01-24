package jchess.move.effects;

import jchess.entities.Square;
import jchess.pieces.Piece;

public class PositionChange {
	private final Piece piece;
	private final Square square;
	
	protected PositionChange(Piece piece, Square square) {
		this.piece = piece;
		this.square = square;
	}
	
	/**
	 * @return The moving Piece.
	 */
	protected Piece getPiece() {
		return piece;
	}
	
	/**
	 * @return The target Square.
	 */
	protected Square getSquare() {
		return square;
	}
}