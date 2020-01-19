package jchess.move.effects;

import java.util.ArrayList;
import java.util.Collections;

import jchess.entities.Square;
import jchess.move.Move;
import jchess.move.MoveType;
import jchess.pieces.Piece;

public class MoveEffects {
	public final Piece moving;
	public final Square trigger;
	public final Move move;
	public final MoveType flag;
	
	public final ArrayList<PositionChange> positionChanges, pcReverse;
	public final ArrayList<StateChange> stateChanges, scReverse;
	
	protected MoveEffects(Piece moving, Square trigger, Move move, MoveType flag, ArrayList<PositionChange> positionChanges, ArrayList<StateChange> stateChanges,
			ArrayList<PositionChange> pcReverse, ArrayList<StateChange> scReverse) {
		this.moving = moving;
		this.trigger = trigger;
		this.move = move;
		this.flag = flag;
		this.positionChanges = positionChanges;
		this.stateChanges = stateChanges;
		
		Collections.reverse(pcReverse);
		Collections.reverse(scReverse);
		
		this.pcReverse = pcReverse;
		this.scReverse = scReverse;
	}
}
