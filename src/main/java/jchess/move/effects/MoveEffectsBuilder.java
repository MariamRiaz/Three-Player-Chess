package jchess.move.effects;

import java.util.ArrayList;
import java.util.List;

import jchess.game.chessboard.model.Square;
import jchess.move.Move;
import jchess.move.MoveType;
import jchess.pieces.Piece;

public class MoveEffectsBuilder {
	private final Piece moving;
	private final Square squareTo, squareFrom;
	private final Move move;
	private MoveType flag = null;
	private boolean fromMove;
	
	private ArrayList<PositionChange> positionChanges = new ArrayList<>(), positionChangesReversed = new ArrayList<>();
	private ArrayList<StateChange> stateChanges = new ArrayList<>(), stateChangesReversed = new ArrayList<>();
	
	public MoveEffectsBuilder(Piece moving, Square squareTo, Square squareFrom, Move move, boolean fromMove) {
		this.moving = moving;
		this.squareTo = squareTo;
		this.squareFrom = squareFrom;
		this.move = move;
		this.fromMove = fromMove;
	}
	
	public MoveEffectsBuilder(MoveEffect base) {
		if (base == null)
			throw new NullPointerException("'base' of MoveEffectsBuilder cannot be null.");
		
		this.moving = base.getPiece();
		this.squareTo = base.getToSquare();
		this.squareFrom = base.getFromSquare();
		this.move = base.getMove();
		this.flag = base.getMoveType();
		
		this.positionChanges = base.getPositionChanges();
		this.positionChangesReversed = base.getPositionChangesReverse();
		this.stateChanges = base.getStateChanges();
		this.stateChangesReversed = base.getStateChangesReverse();
	}

	public MoveEffectsBuilder addPosChange(Square firstSquare, Square secondSquare) {
		if (firstSquare == null || firstSquare.getPiece() == null)
			return this;
			
		positionChanges.add(new PositionChange(firstSquare.getPiece(), secondSquare));
		positionChangesReversed.add(new PositionChange(firstSquare.getPiece(), firstSquare));
		
		if (secondSquare != null && secondSquare.getPiece() != null)
			positionChangesReversed.add(new PositionChange(secondSquare.getPiece(), secondSquare));
		
		return this;
	}
	
	public MoveEffectsBuilder addStateChange(Piece one, Piece two) {
		if (one == null || two == null)
			return this;
		
		stateChanges.add(new StateChange(one.getID(), two));
		stateChangesReversed.add(new StateChange(two.getID(), one));
		
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
		positionChangesReversed.clear();
		stateChanges.clear();
		stateChangesReversed.clear();
		flag = null;
		
		return this;
	}
	
	public boolean isEmpty() {
		return positionChanges.isEmpty() && stateChanges.isEmpty();
	}
	
	public MoveEffect build() {
		return new MoveEffect(moving, squareTo, squareFrom, move, flag, fromMove, positionChanges, stateChanges, positionChangesReversed, stateChangesReversed);
	}
}
