package jchess.move.effects;

import java.util.ArrayList;

import jchess.game.chessboard.model.Square;
import jchess.move.Move;
import jchess.move.MoveType;
import jchess.pieces.Piece;

public class MoveEffectsBuilder {
	private final Piece moving;
	private final Square trigger, from;
	private final Move move;
	private MoveType flag = null;
	private boolean fromMove;
	
	private ArrayList<PositionChange> positionChanges = new ArrayList<>(), pcReverse = new ArrayList<>();
	private ArrayList<StateChange> stateChanges = new ArrayList<>(), scReverse = new ArrayList<>();
	
	public MoveEffectsBuilder(Piece moving, Square trigger, Square from, Move move, boolean fromMove) {
		this.moving = moving;
		this.trigger = trigger;
		this.from = from;
		this.move = move;
		this.fromMove = fromMove;
	}
	
	public MoveEffectsBuilder(MoveEffect base) {
		if (base == null)
			throw new NullPointerException("'base' of MoveEffectsBuilder cannot be null.");
		
		this.moving = base.getMoving();
		this.trigger = base.getTrigger();
		this.from = base.getFrom();
		this.move = base.getMove();
		this.flag = base.getFlag();
		
		this.positionChanges = base.getPositionChanges();
		this.pcReverse = base.getPositionChangesReverse();
		this.stateChanges = base.getStateChanges();
		this.scReverse = base.getStateChangesReverse();
	}

	public MoveEffectsBuilder addPosChange(Square one, Square two) {
		if (one == null || two == null || one.getPiece() == null)
			return this;
			
		positionChanges.add(new PositionChange(one.getPiece(), two));
		pcReverse.add(new PositionChange(one.getPiece(), one));
		
		if (two.getPiece() != null)
			pcReverse.add(new PositionChange(two.getPiece(), two));
		
		return this;
	}
	
	public MoveEffectsBuilder addStateChange(Piece one, Piece two) {
		if (one == null || two == null)
			return this;
		
		stateChanges.add(new StateChange(one.getID(), two));
		scReverse.add(new StateChange(two.getID(), one));
		
		return this;
	}
	
	public MoveEffectsBuilder flag(MoveType flag) {
		this.flag = flag;
		return this;
	}
	
	public MoveEffectsBuilder setFromMove(boolean flag) {
		this.fromMove = flag;
		return this;
	}
	
	public MoveEffectsBuilder clear() {
		positionChanges.clear();
		pcReverse.clear();
		stateChanges.clear();
		scReverse.clear();
		flag = null;
		
		return this;
	}
	
	public boolean isEmpty() {
		return positionChanges.isEmpty() && stateChanges.isEmpty();
	}
	
	public MoveEffect build() {
		return new MoveEffect(moving, trigger, from, move, flag, fromMove, positionChanges, stateChanges, pcReverse, scReverse);
	}
}
