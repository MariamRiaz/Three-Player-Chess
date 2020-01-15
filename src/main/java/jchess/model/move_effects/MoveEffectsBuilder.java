package jchess.model.move_effects;

import java.util.HashMap;

import jchess.entities.Square;
import jchess.pieces.Move;
import jchess.pieces.Piece;

public class MoveEffectsBuilder {
	private final Piece moving;
	private final Square trigger;
	private final Move move;
	
	private HashMap<Piece, Square> positionChanges = new HashMap<Piece, Square>(), pcReverse = new HashMap<Piece, Square>();
	private HashMap<Piece, Piece> stateChanges = new HashMap<Piece, Piece>(), scReverse = new HashMap<Piece, Piece>();
	
	public MoveEffectsBuilder(Piece moving, Square trigger, Move move) {
		this.moving = moving;
		this.trigger = trigger;
		this.move = move;
	}

	public MoveEffectsBuilder addPosChange(Piece piece, Square square) {
		positionChanges.put(piece, square);
		return this;
	}
	
	public MoveEffectsBuilder addStateChange(Piece piece, Piece state) {
		stateChanges.put(piece, state);
		return this;
	}
	
	public MoveEffectsBuilder addPosChangeRev(Piece piece, Square square) {
		pcReverse.put(piece, square);
		return this;
	}
	
	public MoveEffectsBuilder addStateChangeRev(Piece piece, Piece state) {
		scReverse.put(piece, state);
		return this;
	}
	
	public MoveEffects build() {
		return new MoveEffects(moving, trigger, move, positionChanges, stateChanges, pcReverse, scReverse);
	}
}
