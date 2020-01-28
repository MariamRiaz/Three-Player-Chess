package jchess.move.effects;

import java.util.ArrayList;

import jchess.game.chessboard.model.Square;
import jchess.game.history.MoveHistoryEntry;
import jchess.pieces.Piece;

public class BoardTransitionBuilder {
	private MoveHistoryEntry moveHistoryEntry = null;
	
	private ArrayList<PositionChange> positionChanges = new ArrayList<>(), positionChangesReversed = new ArrayList<>();
	private ArrayList<StateChange> stateChanges = new ArrayList<>(), stateChangesReversed = new ArrayList<>();
	
	public BoardTransitionBuilder(MoveHistoryEntry entry) {
		this.moveHistoryEntry = entry;
	}
	
	public BoardTransitionBuilder(BoardTransition base) {
		if (base == null)
			throw new NullPointerException("'base' of MoveEffectsBuilder cannot be null.");
		
		this.moveHistoryEntry = base.getMoveHistoryEntry();
		
		this.positionChanges = base.getPositionChanges();
		this.positionChangesReversed = base.getPositionChangesReverse();
		this.stateChanges = base.getStateChanges();
		this.stateChangesReversed = base.getStateChangesReverse();
	}

	public BoardTransitionBuilder() {}
	
	public MoveHistoryEntry getMoveHistoryEntry() {
		return moveHistoryEntry;
	}
	
	public BoardTransitionBuilder setMoveHistoryEntry(MoveHistoryEntry moveHistoryEntry) {
		this.moveHistoryEntry = moveHistoryEntry;
		return this;
	}
	
	public BoardTransitionBuilder addPosChange(Square firstSquare, Square secondSquare) {
		if (firstSquare == null || firstSquare.getPiece() == null)
			return this;
			
		positionChanges.add(new PositionChange(firstSquare.getPiece(), secondSquare));
		positionChangesReversed.add(new PositionChange(firstSquare.getPiece(), firstSquare));
		
		if (secondSquare != null && secondSquare.getPiece() != null)
			positionChangesReversed.add(new PositionChange(secondSquare.getPiece(), secondSquare));
		
		return this;
	}
	
	public BoardTransitionBuilder addStateChange(Piece one, Piece two) {
		if (one == null || two == null)
			return this;
		
		stateChanges.add(new StateChange(one.getID(), two));
		stateChangesReversed.add(new StateChange(two.getID(), one));
		
		return this;
	}
	
	public BoardTransitionBuilder clear() {
		positionChanges.clear();
		positionChangesReversed.clear();
		stateChanges.clear();
		stateChangesReversed.clear();
		
		return this;
	}
	
	public boolean isEmpty() {
		return positionChanges.isEmpty() && stateChanges.isEmpty();
	}
	
	public BoardTransition build() {
		return new BoardTransition(moveHistoryEntry, positionChanges, stateChanges, positionChangesReversed, stateChangesReversed);
	}
}
