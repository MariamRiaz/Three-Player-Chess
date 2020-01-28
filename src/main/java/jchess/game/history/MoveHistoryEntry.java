package jchess.game.history;

import jchess.game.chessboard.model.Square;
import jchess.move.MoveDefinition;
import jchess.move.MoveType;
import jchess.pieces.Piece;

public class MoveHistoryEntry {
	private final Piece piece;
	private final Square toSquare, fromSquare;
	private final MoveDefinition moveDefinition;
	private MoveType priorityMoveType;
	
	public MoveHistoryEntry(Piece piece, Square trigger, Square fromSquare, MoveDefinition move) {
		this.piece = piece;
		this.toSquare = trigger;
		this.fromSquare = fromSquare;
		this.moveDefinition = move;
	}
	
	public void setPriorityMoveType(MoveType priorityMoveType) {
		this.priorityMoveType = priorityMoveType;
	}
	
	public MoveType getPriorityMoveType() {
		return priorityMoveType;
	}
	
	/**
	 * @return The Piece initiating this MoveEffect.
	 */
	public Piece getPiece() {
		return piece;
	}
	
	/**
	 * @return The Square, which was clicked to initiate this MoveEffect.
	 */
	public Square getToSquare() {
		return toSquare;
	}
	
	/**
	 * @return The Square, on which the Piece initiating the move was located.
	 */
	public Square getFromSquare() {
		return fromSquare;
	}
	
	/**
	 * @return The played Move.
	 */
	public MoveDefinition getMove() {
		return moveDefinition;
	}
}
