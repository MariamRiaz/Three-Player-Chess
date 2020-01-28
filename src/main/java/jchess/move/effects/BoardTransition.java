package jchess.move.effects;

import jchess.game.history.MoveHistoryEntry;

import java.util.ArrayList;
import java.util.Collections;

public class BoardTransition {
	private final MoveHistoryEntry moveHistoryEntry;
	private final ArrayList<PositionChange> positionChanges, positionChnagesReversed;
	private final ArrayList<StateChange> stateChanges, stateChangesReversed;
	
	protected BoardTransition(ArrayList<PositionChange> positionChanges, ArrayList<StateChange> stateChanges,
			 ArrayList<PositionChange> positionChnagesReversed, ArrayList<StateChange> stateChangesReversed) {
		this(null, positionChanges, stateChanges, positionChnagesReversed, stateChangesReversed);
	}
	
	protected BoardTransition(MoveHistoryEntry moveHistoryEntry, ArrayList<PositionChange> positionChanges, ArrayList<StateChange> stateChanges,
			 ArrayList<PositionChange> positionChnagesReversed, ArrayList<StateChange> stateChangesReversed) {
		this.moveHistoryEntry = moveHistoryEntry;
		this.positionChanges = positionChanges;
		this.stateChanges = stateChanges;
		Collections.reverse(positionChnagesReversed);
		Collections.reverse(stateChangesReversed);
		this.positionChnagesReversed = positionChnagesReversed;
		this.stateChangesReversed = stateChangesReversed;
	}
	
	public MoveHistoryEntry getMoveHistoryEntry() {
		return moveHistoryEntry;
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
