package jchess.model.move_effects;

import java.util.HashMap;
import java.util.Map;

import jchess.entities.Square;
import jchess.pieces.Move;
import jchess.pieces.Piece;

public class MoveEffects {
	public final Piece moving;
	public final Square trigger;
	public final Move move;
	
	public final HashMap<Piece, Square> positionChanges, pcReverse;
	public final HashMap<Piece, Piece> stateChanges, scReverse;
	
	protected MoveEffects(Piece moving, Square trigger, Move move, HashMap<Piece, Square> positionChanges, HashMap<Piece, Piece> stateChanges,
			HashMap<Piece, Square> pcReverse, HashMap<Piece, Piece> scReverse) {
		this.moving = moving;
		this.trigger = trigger;
		this.move = move;
		this.positionChanges = new HashMap<>(positionChanges);
		this.stateChanges = new HashMap<>(stateChanges);
		this.pcReverse = new HashMap<>(pcReverse);
		this.scReverse = new HashMap<>(scReverse);
	}
}
