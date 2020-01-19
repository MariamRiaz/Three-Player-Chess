package jchess.move.effects;

import jchess.entities.Square;
import jchess.pieces.Piece;

public class PositionChange {
	public final Piece piece;
	public final Square square;
	
	protected PositionChange(Piece piece, Square square) {
		this.piece = piece;
		this.square = square;
	}
}
