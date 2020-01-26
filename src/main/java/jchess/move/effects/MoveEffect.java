package jchess.move.effects;

import jchess.entities.Square;
import jchess.move.Move;
import jchess.move.MoveType;
import jchess.pieces.Piece;

import java.util.ArrayList;
import java.util.Collections;

public class MoveEffect {
	private final Piece moving;
	private final Square trigger, from;
	private final Move move;
	private final MoveType flag;
	private final boolean fromMove;
	
	private final ArrayList<PositionChange> positionChanges, pcReverse;
	private final ArrayList<StateChange> stateChanges, scReverse;
	
	protected MoveEffect(Piece moving, Square trigger, Square from, Move move, MoveType flag, boolean fromMove, ArrayList<PositionChange> positionChanges, ArrayList<StateChange> stateChanges,
			ArrayList<PositionChange> pcReverse, ArrayList<StateChange> scReverse) {
		this.moving = moving;
		this.trigger = trigger;
		this.from = from;
		this.move = move;
		this.flag = flag;
		this.fromMove = fromMove;
		this.positionChanges = positionChanges;
		this.stateChanges = stateChanges;
		
		Collections.reverse(pcReverse);
		Collections.reverse(scReverse);
		
		this.pcReverse = pcReverse;
		this.scReverse = scReverse;
	}
	
	/**
	 * @return The Piece initiating this MoveEffect.
	 */
	public Piece getMoving() {
		return moving;
	}
	
	/**
	 * @return The Square, which was clicked to initiate this MoveEffect.
	 */
	public Square getTrigger() {
		return trigger;
	}
	
	/**
	 * @return The Square, on which the Piece initiating the move was located.
	 */
	public Square getFrom() {
		return from;
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
	public MoveType getFlag() {
		return flag;
	}
	
	public boolean isFromMove() {
		return fromMove;
	}
    
    public ArrayList<PositionChange> getPositionChanges() {
    	return positionChanges;
    }
    
    public ArrayList<PositionChange> getPositionChangesReverse() {
    	return pcReverse;
    }
    
    public ArrayList<StateChange> getStateChanges() {
    	return stateChanges;
    }
    
    public ArrayList<StateChange> getStateChangesReverse() {
    	return scReverse;
    }
}
