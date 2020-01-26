package jchess.move.effects;

import jchess.entities.Square;
import jchess.move.Move;
import jchess.move.MoveType;
import jchess.pieces.Piece;

import java.util.ArrayList;
import java.util.Collections;

public class MoveEffect {
	private final Piece piece;
	private final Square toSquare, fromSquare;
	private final Move move;
	private final MoveType moveType;
	private final boolean fromMove;
	
	private final ArrayList<PositionChange> positionChanges, positionChnagesReversed;
	private final ArrayList<StateChange> stateChanges, stateChangesReversed;
	
	protected MoveEffect(Piece piece, Square trigger, Square fromSquare, Move move, MoveType moveType, boolean fromMove, ArrayList<PositionChange> positionChanges, ArrayList<StateChange> stateChanges,
						 ArrayList<PositionChange> positionChnagesReversed, ArrayList<StateChange> stateChangesReversed) {
		this.piece = piece;
		this.toSquare = trigger;
		this.fromSquare = fromSquare;
		this.move = move;
		this.moveType = moveType;
		this.fromMove = fromMove;
		this.positionChanges = positionChanges;
		this.stateChanges = stateChanges;
		Collections.reverse(positionChnagesReversed);
		Collections.reverse(stateChangesReversed);
		this.positionChnagesReversed = positionChnagesReversed;
		this.stateChangesReversed = stateChangesReversed;
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
	public Move getMove() {
		return move;
	}
	
	/**
	 * @return The leading MoveType of the Move, e.g. Castling, if this Move has numerous MoveTypes in its conditions that define it, such as Castling and OnlyMove.
	 */
	public MoveType getMoveType() {
		return moveType;
	}
	
	public boolean isFromMove() {
		return fromMove;
	}
    
    public ArrayList<PositionChange> getPositionChanges() {
    	return positionChanges;
    }
    
    public ArrayList<PositionChange> getPositionChangesReverse() {
    	return positionChnagesReversed;
    }
    
    public ArrayList<StateChange> getStateChanges() {
    	return stateChanges;
    }
    
    public ArrayList<StateChange> getStateChangesReverse() {
    	return stateChangesReversed;
    }
}
